import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A class for communicating with the application's
 *   REST API backend.
 * This is an example of the Adapter and Strategy
 *   patterns:
 * - Adapter: The frontend and backend are both written,
 *     this code links them together with minimal changes.
 * - Strategy: This class can be easily swapped for any
 *     other class implementing the IBackendClient interface
 *     (e.g. MockBackendClient for testing).
 */
class HttpBackendClient implements IBackendClient {
  private static final String AUTH_ENDPOINT = "http://localhost:8080/auth";
  private static final String API_ENDPOINT = "http://localhost:8080/prompt";

  private String token;

  private boolean authHelper(String action, String email, String password) {
    try {
      String[] params = new String[] {
          AUTH_ENDPOINT,
          action,
          URLEncoder.encode(email, "UTF-8"),
          URLEncoder.encode(password, "UTF-8")
      };
      String res = finishRequest(initRequest(String.join("/", params), "POST"));
      if (res == null || !res.contains("token=")) {
        token = null;
        return false;
      }
      token = res.split("=")[1];
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean signup(String email, String password) {
    return authHelper("signup", email, password);
  }

  public boolean login(String email, String password) {
    return authHelper("login", email, password);
  }

  public String getToken() {
    return token;
  }

  public boolean checkToken(String tok) {
    String res = finishRequest(initRequest(AUTH_ENDPOINT + "/check/" + tok, "POST"));
    if (res == null || !res.equals("Success.")) {
      return false;
    }
    token = tok;
    return true;
  }

  /**
   * Fetch all past questions and responses via a GET request.
   */
  public ArrayList<HistoryItem> getHistory() {
    Storage s = new Storage("[]");
    String history = finishRequest(initRequest(API_ENDPOINT, "GET"));
    if (history == null) {
      return null;
    }
    s.parse(history);
    return s.history;
  }

  /**
   * Ask a new question by POSTing a File with voice data.
   */
  public HistoryItem askQuestion(File stream) {
    try {
      HttpURLConnection conn = initRequest(API_ENDPOINT, "POST");
      OutputStream out = conn.getOutputStream();
      Files.copy(stream.toPath(), out);
      String json = finishRequest(conn);

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

  /**
   * Helper method for deleting something, whether it be
   *   a specific question or all past questions/responses.
   */
  private boolean delete(String addendum) {
    try {
      String res = finishRequest(initRequest(API_ENDPOINT + addendum, "DELETE"));
      return res.equals("Successfully deleted.");
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Delete a single question based on its UUID.
   */
  public boolean deleteQuestion(UUID id) {
    if (id == null) {
      return false;
    }
    return delete("/" + id.toString());
  }

  /**
   * Clear the entire question/response history.
   */
  public boolean clearHistory() {
    return delete("");
  }

  /**
   * Return true if the backend is reachable, and false otherwise.
   */
  public boolean connected() {
    try {
      String res = finishRequest(initRequest(API_ENDPOINT, "TRACE"));
      return res.equals("Successfully connected.");
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Helper method for initializing a network request.
   */
  private HttpURLConnection initRequest(String endpoint, String method) {
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

  /**
   * Helper method for finalizing a network request.
   */
  private String finishRequest(HttpURLConnection conn) {
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
