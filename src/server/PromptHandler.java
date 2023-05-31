import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;
import org.json.JSONObject;

/**
 * A class responsible for handling all incoming
 *   HTTP requests to the /prompt endpoint, using
 *   the GET, POST, and DELETE methods.
 */
class PromptHandler implements HttpHandler {
  private static final String QUESTION_FILE = "question.wav";
  private Storage storage;
  private IWhisper whisper;
  private IChatGPT chatGPT;

  PromptHandler(Storage s, IWhisper w, IChatGPT c) {
    storage = s;
    whisper = w;
    chatGPT = c;
  }

  PromptHandler(Storage s) {
    this(s, new Whisper(), new ChatGPT());
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
    String query = null;
    if (params.length >= 3) {
      query = params[2];
    }

    switch (method) {
      case "GET":
        response = handleGet(query);
        break;
      case "POST":
        response = handlePost(query, exchange);
        break;
      case "DELETE":
        response = handleDelete(query);
        break;
      case "TRACE":
        response = handleTrace(query);
        break;
      default:
        response = null;
        break;
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
  String handleGet(String query) {
    if (query != null) {
      HistoryItem item = storage.get(UUID.fromString(query));
      if (item == null) {
        return null;
      }
      return item.response;
    }

    return storage.serialize();
  }

  /**
   * When a POST request is made, retrieve the given file
   *   and perform Whisper/ChatGPT operations.
   */
  String handlePost(String query, HttpExchange t) {
    try {
      FileOutputStream out = new FileOutputStream(QUESTION_FILE);
      InputStream ios = t.getRequestBody();
      int i;
      while ((i = ios.read()) != -1) {
        out.write(i);
      }
      out.close();

      File f = new File(QUESTION_FILE);
      String question = whisper.speechToText(f);
      f.delete();
      if (question == null) {
        return null;
      }
      
      String response = chatGPT.ask(question);
      if (response == null) {
        return null;
      }

      HistoryItem item = storage.add(question, response);

      JSONObject obj = new JSONObject();
      obj.put("uuid", item.id);
      obj.put("timestamp", item.timestamp);
      obj.put("question", question);
      obj.put("response", response);
      return obj.toString();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * When a DELETE request is made, delete the appropriate
   *   history item (or all, if one was not given).
   */
  String handleDelete(String query) {
    if (query != null) {
      if (!storage.delete(UUID.fromString(query))) {
        return null;
      }
    } else {
      storage.clear();
    }
    return "Successfully deleted.";
  }

  /**
   * When a TRACE request is made, respond with a success
   *   message.
   */
  String handleTrace(String query) {
    return "Successfully connected.";
  }
}
