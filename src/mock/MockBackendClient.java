import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A mock class for simulating communicating to
 *   the application's REST API backend.
 */
class MockBackendClient implements IBackendClient {
  ArrayList<HistoryItem> history = new ArrayList<>();
  Set<String[]> users = new HashSet<>();
  String token = null;

  /**
   * Sign up for the service using an email and password.
   */
  public boolean signup(String email, String password) {
    for (String[] user : users) {
      if (user[0].equals(email)) {
        return false;
      }
    }
    users.add(new String[] { email, password });
    token = email;
    return true;
  }

  /**
   * Log in to the service using an email and password.
   */
  public boolean login(String email, String password) {
    for (String[] user : users) {
      if (user[0].equals(email) && user[1].equals(password)) {
        token = email;
        return true;
      }
    }
    return false;
  }

  /**
   * Save the user's email configuration.
   */
  public boolean setupEmail(
      String first,
      String last,
      String display,
      String email,
      String smtp,
      String tls,
      String pass
  ) {
    return true;
  }

  /**
   * Returns whether the client is connected to the backend.
   */
  public boolean connected() {
    return true;
  }

  /**
   * Validate the user's account token.
   */
  public boolean checkToken(String tok) {
    return (tok != null);
  }

  /**
   * Return a fake account token.
   */
  public String getToken() {
    return token;
  }

  /**
   * Get the user's email configuration.
   */
  public String[] retrieveEmail() {
    return new String[] {
      "first", "last", "display",
      "smtp", "tls", "password"
    };
  }

  /**
   * Fetch all past questions and responses via a GET request.
   */
  public ArrayList<HistoryItem> getHistory() {
    return history;
  }

  /**
   * Ask a new question by POSTing a question string.
   */
  public APIOperation sendVoice(File stream, String id) {
    HistoryItem item = new HistoryItem(
        "What is 2 plus 2?",
        "2 plus 2 equals 4."
    );
    history.add(item);
    return new APIOperation("question", item.serialize(), true);
  }
}
