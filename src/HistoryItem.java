import java.util.UUID;

/**
 * A class representing a single entry in the question
 *   and response history.
 */
class HistoryItem {
  UUID id;
  long timestamp;
  String question;
  String response;

  HistoryItem(String q, String r) {
    id = UUID.randomUUID();
    timestamp = System.currentTimeMillis() / 1000L;
    question = q;
    response =  r;
  }

  HistoryItem(long t, String q, String r) {
    this(q, r);
    timestamp = t;
  }
}
