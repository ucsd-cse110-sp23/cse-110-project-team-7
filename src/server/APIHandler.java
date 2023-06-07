import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.json.JSONObject;

/**
 * A class responsible for handling all incoming
 *   HTTP requests to the /prompt endpoint, using
 *   the GET, POST, and DELETE methods.
 */
class APIHandler implements HttpHandler {
  private static final String QUESTION_FILE = "question.wav";

  private MongoCollection<Document> users;
  private IChatGPT chatGPT;
  private IWhisper whisper;
  private Document mockUser;

  APIHandler(IChatGPT c, IWhisper w, Document mock) {
    chatGPT = c;
    whisper = w;
    mockUser = mock;

    try {
      MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));
      MongoDatabase database = client.getDatabase("users");
      users = database.getCollection("users");
    } catch (Exception e) {
      users = null;
    }
  }

  APIHandler(IChatGPT c, IWhisper w) {
    this(c, w, null);
  }

  public boolean mock() {
    return (mockUser != null);
  }

  public boolean ok() {
    return (mock() || users != null);
  }

  /**
   * Based on the incoming request's method, generate the
   *   appropriate response.
   */
  public void handle(HttpExchange exchange) {
    APIOperation op = new APIOperation("none", "Invalid request.", false);

    try {
      String method = exchange.getRequestMethod();
      String uri = exchange.getRequestURI().toString();

      String[] params = uri.split("/");
      String token = params[2];
      String id = (params.length >= 4 ? params[3] : null);
      if (token == null) {
        throw new Exception("Invalid request token.");
      }

      Document user = mock()
        ? mockUser
        : users.find(eq("_id", new ObjectId(token))).first();

      if (user == null) {
        throw new Exception("User does not exist.");
      }

      if (method.equals("GET")) {
        getHistory(user, op);
      } else {
        FileOutputStream out = new FileOutputStream(QUESTION_FILE);
        InputStream ios = exchange.getRequestBody();
        int i;
        while ((i = ios.read()) != -1) {
          out.write(i);
        }
        out.close();

        File f = new File(QUESTION_FILE);
        String voice = whisper.speechToText(f);
        f.delete();

        if (!op.parseVoice(voice)) {
          throw new Exception("Invalid command.");
        }
        switch (op.command) {
          case "question":
            askQuestion(user, false, op);
            break;
          case "delete":
            deleteQuestion(
                user,
                id,
                op
            );
            break;
          case "clear":
            clearHistory(user, op);
            break;
          case "create":
            askQuestion(user, true, op);
            break;
          case "send":
            sendEmail(
                user,
                id,
                op
            );
            break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      String response = op.serialize();
      byte[] resBytes = response.getBytes("UTF-8");
      exchange.sendResponseHeaders(
          op.success ? 200 : 400,
          resBytes.length
      );

      OutputStream outStream = exchange.getResponseBody();
      outStream.write(resBytes);
      outStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Helper function for getting the user's history
   *   as a List, minimizing the number of unchecked
   *   cast warnings.
   */
  private List<Document> userHistory(Document user) {
    @SuppressWarnings("unchecked")
    List<Document> hist = (List<Document>) user.get("history");
    return hist;
  }

  /**
   * Retrieve the specific ID given or all past
   *   questions/responses if none is present.
   */
  private void getHistory(Document user, APIOperation op) {
    op.command = "history";
    List<Document> hist = userHistory(user);
    if (hist == null) {
      return;
    }
    op.message = (new Storage(hist)).serialize();
    op.success = true;
  }

  /**
   * Retrieve the given file and perform
   *   Whisper/ChatGPT operations.
   */
  private void askQuestion(Document user, boolean isEmail, APIOperation op) {
    try {
      String question = URLDecoder.decode(op.args, "UTF-8");
      String response = chatGPT.ask(question);

      if (response == null) {
        return;
      }

      List<Document> hist = userHistory(user);
      if (hist == null) {
        return;
      }

      Storage s = new Storage(hist);
      HistoryItem item = s.add(question, response);

      Document doc = new Document()
          .append("uuid", item.id.toString())
          .append("timestamp", item.timestamp)
          .append("question", question)
          .append("response", response)
          .append("email", isEmail);
      hist.add(doc);

      if (!mock()) {
        Bson updates = Updates.set("history", hist);
        UpdateResult result = users.updateOne(
            new Document().append("_id", user.get("_id")),
            updates
        );
        if (result.getModifiedCount() == 0) {
          return;
        }
      } else {
        user.put("history", hist);
      }

      JSONObject obj = new JSONObject();
      obj.put("uuid", item.id);
      obj.put("timestamp", item.timestamp);
      obj.put("question", question);
      obj.put("response", response);
      obj.put("email", isEmail);

      op.message = obj.toString();
      op.success = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Delete the appropriate history item.
   */
  private void deleteQuestion(Document user, String id, APIOperation op) {
    List<Document> hist = userHistory(user);
    if (id == null || hist == null) {
      return;
    }

    boolean hit = false;
    for (int i = 0; i < hist.size(); i++) {
      if (hist.get(i).get("uuid", String.class).equals(id)) {
        hist.remove(i);
        hit = true;
        break;
      }
    }
    if (!hit) {
      return;
    }

    if (!mock()) {
      Bson updates = Updates.set("history", hist);
      UpdateResult result = users.updateOne(
          new Document().append("_id", user.get("_id")),
          updates
      );
      if (result.getModifiedCount() == 0) {
        return;
      }
    } else {
      user.put("history", hist);
    }
    op.success = true;
  }
  
  /**
   * Clear the user's entire history.
   */
  private void clearHistory(Document user, APIOperation op) {
    List<Document> hist = userHistory(user);
    if (hist == null) {
      return;
    }
    hist.clear();

    if (!mock()) {
      Bson updates = Updates.set("history", hist);
      UpdateResult result = users.updateOne(
          new Document().append("_id", user.get("_id")),
          updates
      );
      if (result.getModifiedCount() == 0) {
        return;
      }
    } else {
      user.put("history", hist);
    }
    op.success = true;
  }

  /**
   * Send the email associated with the given ID.
   */
  private void sendEmail(Document user, String id, APIOperation op) {
    IMail mail = new Mail(user);
    if (!mail.ok()) {
      return;
    }

    List<Document> hist = userHistory(user);
    if (hist == null) {
      return;
    }

    try {
      for (Document d : hist) {
        if (
            d.get("uuid", String.class).equals(id)
            && d.get("email", Boolean.class) == Boolean.TRUE
        ) {
          String res = d.get("response", String.class);
          String[] lines = res.split("\n");

          String pre = "Subject: ";
          String subj = lines[0].substring(pre.length());
          String body = res.substring(pre.length() + subj.length() + 1);
          op.success = mail.send(
              op.args,
              subj,
              body
          );
          return;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
