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

  /**
   * Instantiate a new item with only a question/response pair,
   *   filling in other fields automatically.
   */
  HistoryItem(String q, String r) {
    id = UUID.randomUUID();
    timestamp = System.currentTimeMillis() / 1000L;
    question = q;
    response =  r;
  }

  /**
   * Instantiate a new item with a question/response pair
   *   and a current timestamp, useful for testing.
   */
  HistoryItem(long t, String q, String r) {
    this(q, r);
    timestamp = t;
  }

  /**
   * Instantiate a new item as above, but with a
   *   manually-specified UUID instead of a random one.
   */
  HistoryItem(String i, long t, String q, String r) {
    this(t, q, r);

    try {
      id = UUID.fromString(i);
    } catch (Exception e) {
      id = UUID.randomUUID();
    }
  }
}
