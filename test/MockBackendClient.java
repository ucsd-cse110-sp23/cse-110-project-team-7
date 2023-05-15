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
import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A mock class for simulating communicating to
 *   the application's REST API backend.
 */
class MockBackendClient implements IBackendClient {
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
}
