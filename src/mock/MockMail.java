import org.bson.Document;

/**
 * Simple mock class for sending emails.
 */
class MockMail implements IMail {
  Document user;

  MockMail(Document u) {
    user = u;
  }

  public boolean ok() {
    return (user != null);
  }

  public boolean send(String to, String subject, String body) {
    return ok() && (user.get("emailAccount") != null);
  }
}
