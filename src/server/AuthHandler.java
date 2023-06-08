import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

/**
 * A class responsible for allowing users to
 *   sign up and login to their accounts over HTTP.
 */
class AuthHandler implements HttpHandler {
  IDBDriver db;

  AuthHandler(IDBDriver d) {
    db = d;
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

    String action = params.length >= 3 ? params[2] : null;
    String email  = params.length >= 4 ? URLDecoder.decode(params[3], "UTF-8") : null;
    String pass   = params.length >= 5 ? URLDecoder.decode(params[4], "UTF-8") : null;

    switch (action) {
      case "signup":
        response = handleSignup(email, pass);
        break;
      case "login":
        response = handleLogin(email, pass);
        break;
      case "check":
        response = handleCheck(email);
        break;
      case "connected":
        response = "Successfully connected.";
        break;
      case "setup":
        response = handleSetup(params);
        break;
      default:
        response = null;
        break;
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
    return db.createUser(email, password);
  }

  private String handleLogin(String email, String password) {
    return db.loginUser(email, password);
  }

  private String handleCheck(String token) {
    return (db.getUser(token) != null) ? "Success." : null;
  }

  private String handleSetup(String[] params) {
    try {
      String token = params[3];
      Document user = db.getUser(token);

      if (params.length == 4) {
        return user.get("emailAccount", Document.class).toJson();
      }

      String[] keys = new String[] {
        "firstName", "lastName", "displayName",
        "email", "smtpHost", "tlsPort", "password"
      };

      Document acct = new Document();
      for (int i = 0; i < keys.length; i++) {
        acct.append(keys[i], URLDecoder.decode(params[i + 4], "UTF-8"));
      }

      return (db.setupEmail(user, acct) ? "Success." : null);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
