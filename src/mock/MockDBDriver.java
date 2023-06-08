import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 * A mock class for emulating MongoDB calls.
 */
class MockDBDriver implements IDBDriver {
  Map<String, Document> users;

  MockDBDriver(Map<String, Document> u) {
    users = u;
  }

  /**
   * Whether a DB connection has been established.
   */
  public boolean ok() {
    return true;
  }

  /**
   * Get a user document based on its token ID.
   */
  public Document getUser(String token) {
    return users.get(token);
  }

  /**
   * Get a user's list of HistoryItems as Documents.
   */
  public List<Document> getHistory(Document user) {
    @SuppressWarnings("unchecked")
    List<Document> out = (List<Document>) user.get("history");
    return out;
  }

  /**
   * Set a user's history list.
   */
  public boolean setHistory(Document user, List<Document> hist) {
    user.put("history", hist);
    return true;
  }

  /**
   * Append an item to a user's history list.
   */
  public boolean addHistory(Document user, HistoryItem item) {
    @SuppressWarnings("unchecked")
    List<Document> hist = (List<Document>) user.get("history");
    Document doc = new Document()
        .append("uuid", item.id.toString())
        .append("timestamp", item.timestamp)
        .append("question", item.question)
        .append("response", item.response)
        .append("type", item.type);
    hist.add(doc);

    user.put("history", hist);
    return true;
  }

  /**
   * Save email settings using the given Document.
   */
  public boolean setupEmail(Document user, Document acct) {
    user.append("emailAccount", acct);
    return (user != null && acct != null);
  }

  /**
   * Create a user with the given email and password.
   */
  public String createUser(String email, String password) {
    users.put(email,
        new Document("_id", email)
        .append("history", new ArrayList<Document>())
    );
    return "token=" + email;
  }

  /**
   * Login a user with the given email and password.
   */
  public String loginUser(String email, String password) {
    Document doc = users.get(email);
    if (doc == null) {
      return null;
    }
    return "token=" + doc.get("_id");
  }
}
