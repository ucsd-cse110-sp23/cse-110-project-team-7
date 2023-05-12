import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

/**
 * A class responsible for handling all incoming
 *   HTTP requests to the /prompt endpoint, using
 *   the GET, POST, and DELETE methods.
 */
class PromptHandler implements HttpHandler {
  private static final String QUESTION_FILE = "question.wav";
  private Storage storage;

  PromptHandler(Storage s) {
    storage = s;
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
      String question = Whisper.speechToText(f);
      f.delete();
      if (question == null) {
        return null;
      }
      
      String response = ChatGPT.ask(question);
      if (response == null) {
        return null;
      }

      HistoryItem item = storage.add(question, response);
      return response;
    } catch (Exception e) {
      return null;
    }
  }

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
}
