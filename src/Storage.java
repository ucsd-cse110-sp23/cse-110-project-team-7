import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A class handling all read/write operations to the
 *   question and response history.
 */
class Storage {
  static final String HISTORY_FILE = "history.json";

  ArrayList<HistoryItem> history = new ArrayList<>();

  Storage(String text) {
    parse(text);
  }

  Storage() {
    String text = "";
    try {
      File file = new File(HISTORY_FILE);
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);

      String line;
      while ((line = br.readLine()) != null) {
        text += line;
      }

      br.close();
    } catch (Exception e) {
      return;
    }

    parse(text);
  }

  public void parse(String text) {
    try {
      JSONTokener tok = new JSONTokener(text);
      JSONArray arr = new JSONArray(tok);

      for (int i = 0; i < arr.length(); i++) {
        JSONObject entry = arr.getJSONObject(i);
        long timestamp = entry.getLong("timestamp");
        String question = entry.getString("question");
        String response = entry.getString("response");
        if (question == null || response == null) {
          throw new IllegalArgumentException("Malformed input");
        }
        add(timestamp, question, response);
      }
    } catch (Exception e) {
      System.err.println("Error: History file is malformed.");
    }
  }

  public void add(String question, String response) {
    history.add(new HistoryItem(question, response));
  }

  public void add(long timestamp, String question, String response) {
    history.add(new HistoryItem(timestamp, question, response));
  }

  public void delete(UUID id) {
    for (int i = 0; i < history.size(); i++) {
      if (history.get(i).id.compareTo(id) == 0) {
        history.remove(i);
        return;
      }
    }
  }

  public void save(String filename) {
    JSONArray arr = new JSONArray();
    for (HistoryItem item : history) {
      JSONObject tmp = new JSONObject();
      tmp.put("uuid", item.id);
      tmp.put("timestamp", item.timestamp);
      tmp.put("question", item.question);
      tmp.put("response", item.response);

      arr.put(tmp);
    }

    try {
      FileWriter fw = new FileWriter(filename);
      fw.write(arr.toString());
      fw.close();
    } catch (Exception e) {
      System.err.println("Error: Failed to write to history file.");
    }
  }

  public void save() {
    save(HISTORY_FILE);
  }
}
