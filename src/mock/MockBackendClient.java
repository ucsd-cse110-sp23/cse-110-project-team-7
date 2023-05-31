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
  Set<String> users = new HashSet<>();
  String token = null;

  /**
   * Sign up for the service using an email and password.
   */
  public boolean signup(String email, String password) {
    for (String user : users) {
      if (user.equals(email)) {
        return false;
      }
    }
    users.add(email);
    token = email;
    return true;
  }

  /**
   * Log in to the service using an email and password.
   */
  public boolean login(String email, String password) {
    if (!users.contains(email)) {
      return false;
    }
    token = email;
    return true;
  }

  /**
   * Return a fake account token.
   */
  public String getToken() {
    return token;
  }

  /**
   * Validate the user's account token.
   */
  public boolean checkToken(String tok) {
    return (tok != null);
  }

  /**
   * Fetch all past questions and responses via a GET request.
   */
  public ArrayList<HistoryItem> getHistory() {
    return new ArrayList<>();
  }

  /**
   * Ask a new question by POSTing a File with voice data.
   */
  public HistoryItem askQuestion(File stream) {
    return new HistoryItem("What is 2 plus 2?", "4");
  }

  /**
   * Delete a single question based on its UUID.
   */
  public boolean deleteQuestion(UUID id) {
    return true;
  }

  /**
   * Clear the entire question/response history.
   */
  public boolean clearHistory() {
    return true;
  }

  /**
   * Returns whether the client is connected to the backend.
   */
  public boolean connected() {
    return true;
  }
}
