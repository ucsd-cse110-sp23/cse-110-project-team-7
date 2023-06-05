import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.bson.Document;

class Mail implements IMail {
  Session session;
  String from;

  Mail(Document user) {
    try {
      Document email = user.get("email", Document.class);

      Properties props = System.getProperties();
      props.put("mail.smtp.host", email.get("smtp", String.class));
      session = Session.getInstance(props, null);
      from = email.get("address", String.class);
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
      return false;
    }
  }
}
