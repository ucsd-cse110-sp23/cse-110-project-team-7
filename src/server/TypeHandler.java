import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * A class that is responsible for detecting the type of
 * request being asked.
 */
class TypeHandler implements HttpHandler {
  private static final String QUESTION_FILE = "question.wav";
  private IWhisper whisper;
  
  TypeHandler(IWhisper w) {
    whisper = w;
  }

  TypeHandler() {
    this(new Whisper());
  }

  public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();

    int code = 200;
    String response;

    String uri = exchange.getRequestMethod().toString();
    
    switch (method) {
      case "POST":
        response = handlePost(exchange);
        break;
      default: 
        response = null;
    }

    if (response == null) {
      code = 400;
      response = "Error: Request not supported";
    }
    exchange.sendResponseHeaders(code, response.length());

    OutputStream outStream = exchange.getResponseBody();
    outStream.write(response.getBytes());
    outStream.close();
  }

  String handlePost(HttpExchange t) {
    try {
      FileOutputStream out = new FileOutputStream(QUESTION_FILE);
      InputStream ios = t.getRequestBody();
      int i;
      while ((i = ios.read()) != -1) {
        out.write(i);
      }
      out.close();

      File f = new File(QUESTION_FILE);
      String inquiry = whisper.speechToText(f);
      String cmd = inquiry.toLowerCase();
      f.delete();

      // Determine type of input, return matching type of request
      if (cmd.startsWith("question")) {
        return "POST  " + inquiry;
      } else if (cmd.startsWith("delete")) {
        return "DELETE";
      } else if (cmd.startsWith("clear")) {
        return "CLEAR ";
      } else if (cmd.startsWith("create")) {
        return "NOOP  " + inquiry;
      } else if (cmd.startsWith("send")) {
        return "PUT   " + inquiry;
      }

      return null;
    } catch (Exception e) {
      return null;
    }
  }
}
