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
class PromptHandler implements HttpHandler {
  private MongoCollection<Document> users;
  private IChatGPT chatGPT;
  private IPrompt prompt;
  private boolean mocked;

  PromptHandler(IChatGPT c, IPrompt p, boolean mock) {
    chatGPT = c;
    prompt = p;
    mocked = mock;

    try {
      MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));
      MongoDatabase database = client.getDatabase("users");
      users = database.getCollection("users");
    } catch (Exception e) {
      users = null;
    }
  }

  PromptHandler(IPrompt p, boolean mock) {
    this(new ChatGPT(), p, mock);
  }

  public boolean ok() {
    return (users != null);
  }

  /**
   * Based on the incoming request's method, generate the
   *   appropriate response.
   */
  public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();

    int code = 200;
    String response;

    String uri = exchange.getRequestURI().toString();
    String[] params = uri.split("/");
    String token = (params.length >= 3) ? params[2] : null;
    String query = (params.length >= 4) ? params[3] : null;

    if (method.equals("TRACE")) {
      response = handleTrace();
    } else {
      try {
        if (token == null) {
          throw new Exception();
        }
        Document user = users.find(eq("_id", new ObjectId(token))).first();
        if (user == null) {
          throw new Exception();
        }

        System.out.println(method);
        switch (method) {
          case "GET":
            response = handleGet(user, query);
            break;
          case "POST":
            response = handlePost(user, query);
            break;
          case "DELETE":
            response = handleDelete(user, query);
            break;
          case "PUT":
            System.out.println("In patch case");
            response = handlePut(user, query);
          default:
            System.out.println("In default case");
            response = null;
            break;
        }
      } catch (Exception e) {
        e.printStackTrace();
        response = null;
      }
    }

    if (response == null) {
      code = 400;
      response = "Error: Request not supported.";
    }
    exchange.sendResponseHeaders(
        code,
        response.length()
    );

    OutputStream outStream = exchange.getResponseBody();
    outStream.write(response.getBytes());
    outStream.close();
  }

  /**
   * When a GET request is made, retrieve the specific ID
   *   given or all past questions/responses if none is
   *   present.
   */
  String handleGet(Document user, String query) {
    List<Document> hist = user.get("history", List.class);
    if (hist == null) {
      return null;
    }

    if (query != null) {
      HistoryItem item;
      for (Document tmp : hist) {
        if (tmp.get("uuid").equals(query)) {
          return tmp.get("response", String.class);
        }
      }
      return null;
    }

    return (new Storage(hist)).serialize();
  }

  /**
   * When a POST request is made, retrieve the given file
   *   and perform Whisper/ChatGPT operations.
   */
  String handlePost(Document user, String query) {
    try {
      String promptQ = prompt.getPrompt();
      String question = URLDecoder.decode(query, "UTF-8");
      String response = chatGPT.ask(question);

      if (response == null) {
        return null;
      }

      List<Document> hist = user.get("history", List.class);
      if (hist == null) {
        return null;
      }

      Storage s = new Storage(hist);
      HistoryItem item = s.add(question, response);

      Document doc = new Document()
        .append("uuid", item.id.toString())
        .append("timestamp", item.timestamp)
        .append("question", question)
        .append("response", response);
      hist.add(doc);

      if (!mocked) {
        Bson updates = Updates.set("history", hist);
        UpdateResult result = users.updateOne(
            new Document().append("_id", user.get("_id")),
            updates
        );
        if (result.getModifiedCount() == 0) {
          return null;
        }
      }

      JSONObject obj = new JSONObject();
      obj.put("uuid", item.id);
      obj.put("timestamp", item.timestamp);
      obj.put("question", question);
      obj.put("response", response);
      return obj.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * When a DELETE request is made, delete the appropriate
   *   history item (or all, if one was not given).
   */
  String handleDelete(Document user, String query) {
    List<Document> hist = user.get("history", List.class);
    if (hist == null) {
      return null;
    }

    if (query != null) {
      boolean hit = false;
      for (int i = 0; i < hist.size(); i++) {
        if (hist.get(i).get("uuid", String.class).equals(query)) {
          hist.remove(i);
          hit = true;
          break;
        }
      }
      if (!hit) {
        return null;
      }
    } else {
      hist.clear();
    }

    if (!mocked) {
      Bson updates = Updates.set("history", hist);
      UpdateResult result = users.updateOne(
          new Document().append("_id", user.get("_id")),
          updates
      );
      if (result.getModifiedCount() == 0) {
        return null;
      }
    }

    return "Successfully deleted.";
  }

  /**
   * When a patch request is made it will update, the send email
   * field of a user document
   * @param user
   * @param query
   * @return
   */
  String handlePut(Document user, String query) {
    String currEmail = user.get("sendEmail", String.class);
    System.out.println("In handle patch");

    try {
      if(currEmail == null) {
        System.out.println("Adding field");
        user.append("sendEmail", query);
        System.out.println(user.toString());
        return "Successfully updated email";
      } else {
        System.out.println("Updating field");
        user.replace("sendEmail", query);
        return "Successfully updated email"; 
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null; 
    }
  }

  /**
   * When a TRACE request is made, respond with a success
   *   message.
   */
  String handleTrace() {
    return "Successfully connected.";
  }
}
