import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

/**
 * A concrete MongoDB driver for encapsulating
 *   database logic.
 */
class DBDriver implements IDBDriver {
  MongoCollection<Document> users;

  DBDriver() {
    try {
      if (System.getenv("MONGO_URI") == null) {
        System.err.println("Error: MONGO_URI environment variable not defined.");
        users = null;
        return;
      }

      MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));
      MongoDatabase database = client.getDatabase("users");
      users = database.getCollection("users");
    } catch (Exception e) {
      users = null;
    }
  }

  /**
   * Whether a DB connection has been established.
   */
  public boolean ok() {
    return (users != null);
  }

  /**
   * Get a user document based on its token ID.
   */
  public Document getUser(String token) {
    return users.find(eq("_id", new ObjectId(token))).first();
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
    Bson updates = Updates.set("history", hist);
    UpdateResult result = users.updateOne(
        new Document("_id", user.get("_id")),
        updates
    );
    return (result.getMatchedCount() > 0);
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

    return setHistory(user, hist);
  }

  /**
   * Save email settings using the given Document.
   */
  public boolean setupEmail(Document user, Document acct) {
    Bson updates = Updates.set("emailAccount", acct);
    UpdateResult result = users.updateOne(
        new Document("_id", user.get("_id")),
        updates
    );

    return (result.getMatchedCount() > 0);
  }

  /**
   * Create a user with the given email and password.
   */
  public String createUser(String email, String password) {
    Document doc = users.find(eq("email", email)).first();
    if (doc != null) {
      return null;
    }

    ObjectId id = new ObjectId();
    doc = new Document("_id", id)
        .append("email", email)
        .append("password", hashPass(password))
        .append("history", new ArrayList<Document>())
        .append("emailAccount",
            new Document("firstName", "")
                .append("lastName", "")
                .append("displayName", "")
                .append("smtpHost", "")
                .append("tlsPort", "")
                .append("password", "")
        );
    users.insertOne(doc);
    return "token=" + id.toString();
  }

  /**
   * Login a user with the given email and password.
   */
  public String loginUser(String email, String password) {
    Document doc = users.find(eq("email", email)).first();
    if (doc == null) {
      return null;
    }

    byte[] hashed = hashPass(password);
    if (!doc.get("password").equals(new Binary(hashed))) {
      return null;
    }
    return "token=" + doc.get("_id").toString();
  }

  /**
   * Helper function to SHA-256 hash a password.
   */
  private byte[] hashPass(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(password.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      return null;
    }
  }
}
