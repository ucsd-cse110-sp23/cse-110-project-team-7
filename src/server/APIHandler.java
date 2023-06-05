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
  private boolean mocked;

  APIHandler(IChatGPT c, IWhisper w, boolean mock) {
    chatGPT = c;
    whisper = w;
    mocked = mock;

    try {
      MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));
      MongoDatabase database = client.getDatabase("users");
      users = database.getCollection("users");
    } catch (Exception e) {
      users = null;
    }
  }

  public boolean ok() {
    return (users != null);
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

      Document user = users.find(eq("_id", new ObjectId(token))).first();
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
      exchange.sendResponseHeaders(
          op.success ? 200 : 400,
          response.length()
      );

      OutputStream outStream = exchange.getResponseBody();
      outStream.write(response.getBytes());
      outStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieve the specific ID given or all past
   *   questions/responses if none is present.
   */
  private void getHistory(Document user, APIOperation op) {
    List<Document> hist = user.get("history", List.class);
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
  void askQuestion(Document user, boolean isEmail, APIOperation op) {
    try {
      String question = URLDecoder.decode(op.args, "UTF-8");
      String response = chatGPT.ask(question);

      if (response == null) {
        return;
      }

      List<Document> hist = user.get("history", List.class);
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

      if (!mocked) {
        Bson updates = Updates.set("history", hist);
        UpdateResult result = users.updateOne(
            new Document().append("_id", user.get("_id")),
            updates
        );
        if (result.getModifiedCount() == 0) {
          return;
        }
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
  void deleteQuestion(Document user, String id, APIOperation op) {
    List<Document> hist = user.get("history", List.class);
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

    if (!mocked) {
      Bson updates = Updates.set("history", hist);
      UpdateResult result = users.updateOne(
          new Document().append("_id", user.get("_id")),
          updates
      );
      if (result.getModifiedCount() == 0) {
        return;
      }
    }
    op.success = true;
  }
  
  /**
   * Clear the user's entire history.
   */
  void clearHistory(Document user, APIOperation op) {
    List<Document> hist = user.get("history", List.class);
    if (hist == null) {
      return;
    }
    hist.clear();

    if (!mocked) {
      Bson updates = Updates.set("history", hist);
      UpdateResult result = users.updateOne(
          new Document().append("_id", user.get("_id")),
          updates
      );
      if (result.getModifiedCount() == 0) {
        return;
      }
    }
    op.success = true;
  }

  /**
   * Send the email associated with the given ID.
   */
  void sendEmail(Document user, String id, APIOperation op) {
    IMail mail = new Mail(user);
    if (!mail.ok()) {
      return;
    }

    List<Document> hist = user.get("history", List.class);
    if (hist == null) {
      return;
    }

    for (Document d : hist) {
      if (
        d.get("uuid", String.class).equals(id)
        && d.get("email", Boolean.class) == Boolean.TRUE
      ) {
        op.success = mail.send(
            op.args,
            "Message from SayIt Assistant user",
            d.get("response", String.class)
        );
        return;
      }
    }
  }
}
