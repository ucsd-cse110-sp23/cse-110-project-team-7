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
 * request being asked
 */
public class TypeHandler implements HttpHandler {
    private static final String QUESTION_FILE = "question.wav";
    private IPrompt prompt;
    private IWhisper whisper;
    
    TypeHandler(IPrompt p, IWhisper w) {
        whisper = w;
        prompt = p;
    }

    TypeHandler() {
        this(new Prompt(), new Whisper());
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

        if(response == null) {
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
            while((i = ios.read()) != -1) {
                out.write(i);
            }
            out.close();

            File f = new File(QUESTION_FILE);
            String inquiry = whisper.speechToText(f);
            f.delete();
            this.prompt.updatePrompt(inquiry);

            Pattern questionPattern = Pattern.compile("Question", Pattern.CASE_INSENSITIVE);
            Pattern deletePattern = Pattern.compile("Delete", Pattern.CASE_INSENSITIVE);
            Pattern clearPattern = Pattern.compile("Clear", Pattern.CASE_INSENSITIVE);
            String[] questionList = inquiry.split(" ");
      // find if request is question
            if(questionPattern.matcher(questionList[0]).find()) {
                return "POST  " + inquiry;
            } else if (deletePattern.matcher(questionList[0]).find()) {
                return "DELETE";
            } else if (clearPattern.matcher(questionList[0]).find()) {
                return "CLEAR ";
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }
}
