import org.bson.Document;

/**
 * Simple mock class for sending emails.
 */
class MockMail implements IMail {
  Document user;

  MockMail(Document u) {
    user = u;
  }

  /**
   * Return whether an email is able to be sent.
   */
  public boolean ok() {
    return (user != null);
  }

  /**
   * Send an email with the given contents.
   */
  public boolean send(String to, String subject, String body) {
    return ok() && (user.get("emailAccount") != null);
  }
}
