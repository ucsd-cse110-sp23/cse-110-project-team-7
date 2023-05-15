import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import org.json.JSONTokener;
import org.json.JSONObject;

/**
 * A class for communicating with the application's
 *   REST API backend.
 */
class HttpClient {
  private static final String API_ENDPOINT = "http://localhost:8080/prompt";

  static ArrayList<HistoryItem> getHistory() {
    Storage s = new Storage("[]");
    String history = finishRequest(initRequest(API_ENDPOINT, "GET"));
    if (history == null) {
      return null;
    }
    s.parse(history);
    return s.history;
  }

  static HistoryItem askQuestion(File stream) {
    try {
      byte[] data = Files.readAllBytes(stream.toPath());
      HttpURLConnection conn = initRequest(API_ENDPOINT, "POST");
      String json = finishRequest(conn, new String(data, StandardCharsets.UTF_8));

      JSONTokener tok = new JSONTokener(json);
      JSONObject obj = new JSONObject(tok);
      String id = obj.getString("uuid");
      long timestamp = obj.getLong("timestamp");
      String question = obj.getString("question");
      String response = obj.getString("response");

      return new HistoryItem(id, timestamp, question, response);
    } catch (Exception e) {
      return null;
    }
  }

  private static boolean delete(String addendum) {
    try {
      String res = finishRequest(initRequest(API_ENDPOINT + addendum, "DELETE"));
      return res.equals("Successfully deleted.");
    } catch (Exception e) {
      return false;
    }
  }

  static boolean deleteQuestion(UUID id) {
    if (id == null) {
      return false;
    }
    return delete("/" + id.toString());
  }

  static boolean clearHistory() {
    return delete("");
  }

  private static HttpURLConnection initRequest(String endpoint, String method) {
    try {
      URL url = new URI(endpoint).toURL();
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod(method);
      conn.setDoOutput(true);
      return conn;
    } catch (Exception e) {
      return null;
    }
  }

  private static String finishRequest(HttpURLConnection conn, String data) {
    try {
      OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
      out.write(data, 0, data.length());
      out.flush();
      out.close();
      return finishRequest(conn);
    } catch (Exception e) {
      return null;
    }
  }

  private static String finishRequest(HttpURLConnection conn) {
    try {
      BufferedReader in = new BufferedReader(
        new InputStreamReader(conn.getInputStream())
      );
      String response = in.readLine();
      in.close();
      return response;
    } catch (Exception e) {
      return null;
    }
  }
}
