import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A class representing a single entry in the question
 *   and response history.
 */
class HistoryItem {
  UUID id;
  long timestamp;
  String question;
  String response;
  boolean isEmail;

  /**
   * Instantiate a new item with only a question/response pair,
   *   filling in other fields automatically.
   */
  HistoryItem(String q, String r) {
    id = UUID.randomUUID();
    timestamp = System.currentTimeMillis() / 1000L;
    question = q;
    response =  r;
    isEmail = false;
  }

  /**
   * Instantiate a new item with a question/response pair
   *   and a current timestamp, useful for testing.
   */
  HistoryItem(long t, String q, String r) {
    this(q, r);
    timestamp = t;
    isEmail = false;
  }

  /**
   * Instantiate a new item as above, but with a
   *   manually-specified UUID instead of a random one.
   */
  HistoryItem(String i, long t, String q, String r, boolean em) {
    this(t, q, r);

    try {
      id = UUID.fromString(i);
    } catch (Exception e) {
      id = UUID.randomUUID();
    }
    isEmail = em;
  }

  /**
   * Parse a JSON string and return a new HistoryItem.
   */
  static HistoryItem fromString(String json) {
    JSONTokener tok = new JSONTokener(json);
    JSONObject obj = new JSONObject(tok);

    return new HistoryItem(
        obj.getString("uuid"),
        obj.getLong("timestamp"),
        obj.getString("question"),
        obj.getString("response"),
        obj.getBoolean("email")
    );
  }

  /**
   * Generate a JSON string from the current item.
   */
  String serialize() {
    JSONObject tmp = new JSONObject();
    tmp.put("uuid", id.toString());
    tmp.put("timestamp", timestamp);
    tmp.put("question", question);
    tmp.put("response", response);
    tmp.put("email", isEmail);
    return tmp.toString();
  }
}
