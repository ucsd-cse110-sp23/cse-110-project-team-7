import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.bson.Document;

class Mail implements IMail {
  Session session;
  String from;

  Mail(Document user) {
    try {
      Document email = user.get("emailAccount", Document.class);
      from = email.get("email", String.class);

      Properties props = System.getProperties();
      props.put("mail.smtp.host", email.get("smtpHost", String.class));
      props.put("mail.smtp.port", email.get("tlsPort", String.class));
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");

      Authenticator auth = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(
              from,
              email.get("password", String.class)
          );
        }
      };

      session = Session.getInstance(props, auth);
    } catch (Exception e) {
      session = null;
    }
  }

  public boolean ok() {
    return (session != null);
  }

  public boolean send(String to, String subject, String body) {
    try {
      MimeMessage msg = new MimeMessage(session);
      msg.addHeader("Content-Type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");

      InternetAddress[] addr = InternetAddress.parse(from, false);
      msg.setFrom(addr[0]);
      msg.setReplyTo(addr);
      msg.setSubject(subject, "UTF-8");
      msg.setText(body, "UTF-8");
      msg.setSentDate(new Date());

      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));

      Transport.send(msg);
      return true;
    } catch (Exception e) {
        e.printStackTrace();
      return false;
    }
  }
}
