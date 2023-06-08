import java.util.List;
import org.bson.Document;

interface IDBDriver {
  boolean ok();

  Document getUser(String token);

  List<Document> getHistory(Document user);

  boolean setHistory(Document user, List<Document> hist);

  boolean addHistory(Document user, HistoryItem item);

  boolean setupEmail(Document user, Document acct);

  String createUser(String email, String password);

  String loginUser(String email, String password);
}
