import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 * UI for logging in or signing up.
 */
class LoginFlow extends JPanel {
  private static final Dimension TEXT_SIZE = new Dimension(300, 25);

  private JTextField signupEmail, signupPassword, signupVerify, loginEmail, loginPassword;
  private JTabbedPane tabs;

  public JButton signupDone, loginDone;

  LoginFlow() {
    // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setPreferredSize(new Dimension(200, 200));

    /* SIGNUP */
    JPanel paneSignup = new JPanel();
    paneSignup.setLayout(new BoxLayout(paneSignup, BoxLayout.Y_AXIS));

    paneSignup.add(new JLabel("Email:"));
    signupEmail = new JTextField();
    // signupEmail.setPreferredSize(TEXT_SIZE);
    paneSignup.add(signupEmail);

    paneSignup.add(new JLabel("Password:"));
    signupPassword = new JPasswordField();
    // signupPassword.setPreferredSize(TEXT_SIZE);
    paneSignup.add(signupPassword);

    paneSignup.add(new JLabel("Password (Verify):"));
    signupVerify = new JPasswordField();
    // signupVerify.setPreferredSize(TEXT_SIZE);
    paneSignup.add(signupVerify);

    signupDone = new JButton("Sign Up");
    paneSignup.add(signupDone);

    /* LOGIN */
    JPanel paneLogin = new JPanel();
    paneLogin.setLayout(new GridLayout(4, 0));

    loginEmail = new JTextField();
    loginEmail.setPreferredSize(TEXT_SIZE);
    paneLogin.add(loginEmail);

    loginPassword = new JPasswordField();
    loginPassword.setPreferredSize(TEXT_SIZE);
    paneLogin.add(loginPassword);

    loginDone = new JButton("Log In");
    paneLogin.add(loginDone);

    tabs = new JTabbedPane();
    tabs.addTab("Sign Up", paneSignup);
    tabs.addTab("Log In", paneLogin);

    add(tabs);
  }

  public String checkInputs() {
    if (tabs.getSelectedIndex() == 0) {
      if (signupEmail.getText().length() == 0 ||
          signupPassword.getText().length() == 0 ||
          signupVerify.getText().length() == 0
      ) {
        return "Required field(s) are empty.";
      }
      if (!signupEmail.getText().contains("@")) {
        return "Invalid email format.";
      }
      if (!signupPassword.getText().equals(signupVerify.getText())) {
        return "Passwords do not match.";
      }
    }
    return null;
  }

  public String getEmail() {
    return (tabs.getSelectedIndex() == 0) ? signupEmail.getText() : loginEmail.getText();
  }

  public String getPassword() {
    return (tabs.getSelectedIndex() == 0) ? signupPassword.getText() : loginPassword.getText();
  }
}
