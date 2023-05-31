import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
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

  /**
   * Given a List of BSON Documents, populate the
   *   history list.
   */
  Storage(List<Document> items) {
    for (Document d : items) {
      add(
          d.get("uuid", String.class),
          d.get("timestamp", Long.class),
          d.get("question", String.class),
          d.get("response", String.class)
      );
    }
  }

  /**
   * Given JSON text, populate the history list.
   */
  Storage(String text) {
    parse(text);
  }

  /**
   * Without input text, read the default filename
   *   and populate the history list.
   */
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

  /**
   * Parse the given string as JSON storing timestamp,
   *   question, and response for each history entry.
   */
  public void parse(String text) {
    try {
      JSONTokener tok = new JSONTokener(text);
      JSONArray arr = new JSONArray(tok);

      for (int i = 0; i < arr.length(); i++) {
        JSONObject entry = arr.getJSONObject(i);
        String id = entry.getString("uuid");
        long timestamp = entry.getLong("timestamp");
        String question = entry.getString("question");
        String response = entry.getString("response");
        if (question == null || response == null) {
          throw new IllegalArgumentException("Malformed input");
        }
        add(id, timestamp, question, response);
      }
    } catch (Exception e) {
      System.err.println("Error: History file is malformed.");
    }
  }

  /**
   * Add a new question/response pair to the storage history.
   */
  public HistoryItem add(String question, String response) {
    HistoryItem out = new HistoryItem(question, response);
    history.add(out);
    return out;
  }

  /**
   * Add a new question/response pair to the storage history with
   *   a particular timestamp, useful for testing.
   */
  public void add(long timestamp, String question, String response) {
    history.add(new HistoryItem(timestamp, question, response));
  }

  /**
   * Add a new question/response pair to the storage history with
   *   a particular timestamp and ID, useful for deserialization.
   */
  public void add(String uuid, long timestamp, String question, String response) {
    history.add(new HistoryItem(uuid, timestamp, question, response));
  }

  /**
   * Gets an item in the history storage by its unique ID.
   */
  public HistoryItem get(UUID id) {
    for (int i = 0; i < history.size(); i++) {
      HistoryItem item = history.get(i);
      if (item.id.compareTo(id) == 0) {
        return item;
      }
    }
    return null;
  }

  /**
   * Gets an item in the history storage by a String
   *   representation of its unique ID.
   */
  public HistoryItem get(String id) {
    try {
      return get(UUID.fromString(id));
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Delete a particular question/response pair from
   *   history by its UUID.
   */
  public boolean delete(UUID id) {
    for (int i = 0; i < history.size(); i++) {
      if (history.get(i).id.compareTo(id) == 0) {
        history.remove(i);
        return true;
      }
    }
    return false;
  }

  /**
   * Delete a particular question/response pair from
   *   history by a String representation of its UUID.
   */
  public boolean delete(String id) {
    try {
      return delete(UUID.fromString(id));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Delete all question/response pairs from the
   *   history.
   */
  public void clear() {
    history.clear();
  }

  /**
   * Serialize the current list of question/response pairs
   *   into a JSON string.
   */
  public String serialize() {
    JSONArray arr = new JSONArray();
    for (HistoryItem item : history) {
      JSONObject tmp = new JSONObject();
      tmp.put("uuid", item.id);
      tmp.put("timestamp", item.timestamp);
      tmp.put("question", item.question);
      tmp.put("response", item.response);

      arr.put(tmp);
    }
    return arr.toString();
  }

  /**
   * Save the current history list as JSON to the given filename.
   */
  public void save(String filename) {
    try {
      FileWriter fw = new FileWriter(filename);
      fw.write(serialize());
      fw.close();
    } catch (Exception e) {
      System.err.println("Error: Failed to write to history file.");
    }
  }

  /**
   * Save the current history list as JSON to the default filename.
   */
  public void save() {
    save(HISTORY_FILE);
  }
}
