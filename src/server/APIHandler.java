import static com.mongodb.client.model.Filters.eq;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

/**
 * A class responsible for handling all incoming
 *   HTTP requests to the /prompt endpoint, using
 *   the GET, POST, and DELETE methods.
 */
class APIHandler implements HttpHandler {
  private static final String QUESTION_FILE = "question.wav";

  private IChatGPT chatGPT;
  private IWhisper whisper;
  private IDBDriver db;

  APIHandler(IChatGPT c, IWhisper w, IDBDriver d) {
    chatGPT = c;
    whisper = w;
    db = d;
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

      Document user = db.getUser(token);
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
            askQuestion(user, "question", op);
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
            askQuestion(user, "email", op);
            break;
          case "send":
            sendEmail(
                user,
                id,
                op
            );
            break;
          case "set up":
          case "setup":
            op.message = "";
            op.success = true;
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
          200,
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
   * Retrieve the specific ID given or all past
   *   questions/responses if none is present.
   */
  private void getHistory(Document user, APIOperation op) {
    op.command = "history";
    List<Document> hist = db.getHistory(user);
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
  private void askQuestion(Document user, String type, APIOperation op) {
    try {
      String question = URLDecoder.decode(op.args, "UTF-8");

      String post = type.equals("email") ? " Please begin with a line starting with \"Subject: \" followed by an empty line, and sign off as [Display Name Here]." : "";
      String response = chatGPT.ask(question + post);

      if (type.equals("email")) {
        Document email = user.get("emailAccount", Document.class);
        String display = email.get("displayName", String.class);

        if (response.indexOf("[Display Name Here]") > -1) {
          response = response.replace("[Display Name Here]", display);
        } else {
          response = response + "\n" + display;
        }
      }

      if (response == null) {
        return;
      }

      HistoryItem item = new HistoryItem(question, response);
      item.type = type;
      if (!db.addHistory(user, item)) {
        return;
      }

      JSONObject obj = new JSONObject();
      obj.put("uuid", item.id);
      obj.put("timestamp", item.timestamp);
      obj.put("question", question);
      obj.put("response", response);
      obj.put("type", type);

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
    List<Document> hist = db.getHistory(user);
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

    op.success = db.setHistory(user, hist);
  }
  
  /**
   * Clear the user's entire history.
   */
  private void clearHistory(Document user, APIOperation op) {
    List<Document> hist = db.getHistory(user);
    if (hist == null) {
      return;
    }
    hist.clear();

    op.success = db.setHistory(user, hist);
  }

  /**
   * Send the email associated with the given ID.
   */
  private void sendEmail(Document user, String id, APIOperation op) {
    IMail mail = new Mail(user);
    if (!mail.ok()) {
      return;
    }

    List<Document> hist = db.getHistory(user);
    if (hist == null) {
      return;
    }

    try {
      for (Document d : hist) {
        if (
            d.get("uuid", String.class).equals(id)
            && d.get("type", String.class).equals("email")
        ) {
          String res = d.get("response", String.class);
          String[] lines = res.split("\n");
          int i = 0;
          while (lines[i].trim().isEmpty()) {
            i++;
          }

          String pre = "Subject: ";

          Document email = user.get("emailAccount", Document.class);
          String display = email.get("displayName", String.class);

          int ind = res.indexOf(pre) + pre.length();
          String subj = res.substring(ind, ind + res.substring(ind).indexOf("\n"));
          String body = res.substring(ind + 1 + res.substring(ind).indexOf("\n") + 1);

          pre = "Email to ";
          String to = op.args.substring(pre.length()).replace(" at ", "@");
          to = to.substring(0, to.length() - 1);

          op.message = "Email to " + to;
          if (!mail.send(
              to,
              subj,
              body)
          ) {
            return;
          }

          HistoryItem item = new HistoryItem(op.message, "Email successfully sent.");
          item.type = "send";
          if (!db.addHistory(user, item)) {
            return;
          }

          op.message = item.serialize();
          op.success = true;
          return;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
