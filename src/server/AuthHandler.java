import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * A class responsible for allowing users to
 *   sign up and login to their accounts over HTTP.
 */
class AuthHandler implements HttpHandler {
  private MongoCollection<Document> users;

  AuthHandler() {
    try {
      if (System.getenv("MONGO_URI") == null) {
        throw new Exception("MONGO_URI environment variable not defined.");
      }

      MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));
      MongoDatabase database = client.getDatabase("users");
      users = database.getCollection("users");
    } catch (Exception e) {
      e.printStackTrace();
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
  public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();
    String response = null;
    int code = 200;

    if (!method.equals("POST")) {
      response = "Error: Request not supported.";
      code = 400;
      exchange.sendResponseHeaders(
          code,
          response.length()
      );

      OutputStream outStream = exchange.getResponseBody();
      outStream.write(response.getBytes());
      outStream.close();
      return;
    }

    String uri = exchange.getRequestURI().toString();
    String[] params = uri.split("/");

    if (params.length >= 5) {
      String action = params[2];
      String email = params[3];
      String password = params[4];

      switch (action) {
        case "signup":
          response = handleSignup(email, password);
          break;
        case "login":
          response = handleLogin(email, password);
          break;
        case "check":
          response = handleCheck(email);
          break;
      }
    }

    if (response == null) {
      code = 400;
      response = "Internal server error.";
    }
    exchange.sendResponseHeaders(
        code,
        response.length()
    );

    OutputStream outStream = exchange.getResponseBody();
    outStream.write(response.getBytes());
    outStream.close();
  }

  private String handleSignup(String email, String password) {
    Document doc = users.find(eq("email", email)).first();
    if (doc != null) return null;

    ObjectId id = new ObjectId();
    doc = new Document("_id", id)
      .append("email", email)
      .append("password", hashPass(password))
      .append("history", new Document())
      .append("auto", new ArrayList<String>());
    users.insertOne(doc);
    return "token=" + id.toString();
  }

  private String handleLogin(String email, String password) {
    /* TODO: Implement */
    return null;
  }

  /*
  private String handleAuto(String token, String mac) {
    Document query = new Document(eq("_id", token));
    Document update = new Document(
        "$push",
        new Document("auto", mac)
    );
    UpdateResult result = users.updateOne(query, update);
    if (result.getModifiedCount() != 1) {
      return null;
    }
    return "Success.";
  }
  */

  private String handleCheck(String token) {
    Document doc = users.find(eq("_id", new ObjectId(token))).first();
    return (doc != null) ? "Success." : null;
  }

  private byte[] hashPass(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      return null;
    }
  }
}
