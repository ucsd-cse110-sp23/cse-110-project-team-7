import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

class SetupEmailFrame extends JFrame {
  private static final int WIDTH = 400;
  private static final int HEIGHT = 280;

  /*
   * UI Components
   */
  private IBackendClient client;

  private JTextField firstName;
  private JTextField lastName;
  private JTextField displayName;
  private JTextField email;
  private JTextField smtpHost;
  private JTextField tlsPort;
  private JTextField password;

  private JButton save;
  private JButton cancel;

  SetupEmailFrame(IBackendClient inClient) {
    setSize(WIDTH, HEIGHT);
    setVisible(true);

    client = inClient;
    String[] data = client.retrieveEmail();

    JPanel paneSetupEmail = new JPanel();
    paneSetupEmail.setLayout(new BoxLayout(paneSetupEmail, BoxLayout.Y_AXIS));

    paneSetupEmail.add(new JLabel("First Name: "));
    firstName = new JTextField(data[0]);
    paneSetupEmail.add(firstName);

    paneSetupEmail.add(new JLabel("Last Name: "));
    lastName = new JTextField(data[1]);
    paneSetupEmail.add(lastName);
        
    paneSetupEmail.add(new JLabel("Display Name: "));
    displayName = new JTextField(data[2]);
    paneSetupEmail.add(displayName);

    paneSetupEmail.add(new JLabel("Email: "));
    email = new JTextField(data[3]);
    paneSetupEmail.add(email);

    paneSetupEmail.add(new JLabel("SMTP host: "));
    smtpHost = new JTextField(data[4]);
    paneSetupEmail.add(smtpHost);

    paneSetupEmail.add(new JLabel("TLS port: "));
    tlsPort = new JTextField(data[5]);
    paneSetupEmail.add(tlsPort);

    paneSetupEmail.add(new JLabel("Password: "));
    password = new JPasswordField(data[6]);
    paneSetupEmail.add(password);

    cancel = new JButton("Cancel");
    paneSetupEmail.add(cancel, BorderLayout.SOUTH);

    save = new JButton("Save");
    paneSetupEmail.add(save, BorderLayout.SOUTH);

    add(paneSetupEmail, BorderLayout.CENTER);

    revalidate();
    repaint();

    addListeners();
  }

  void addListeners() {
    this.cancel.addActionListener((ActionEvent e) -> {
      this.dispose();
    });
    this.save.addActionListener((ActionEvent e) -> {
      String inputCheck = this.checkInputs();
      if (inputCheck != null) {
        JOptionPane.showMessageDialog(
            null,
            inputCheck,
            "Invalid inputs",
            JOptionPane.ERROR_MESSAGE
        );
      } else {
        boolean success = client.setupEmail(
            getFirstName(), 
            getLastName(), 
            getDisplayName(), 
            getEmail(), 
            getSMTPHost(), 
            getTLSPort(), 
            getPassword()
        );
        if (!success) {
          JOptionPane.showMessageDialog(
              null,
              "Error updating email",
              "Server Error",
              JOptionPane.ERROR_MESSAGE
          );
        } else {
          dispose();
        }
      }
    });
  }

  public String checkInputs() {
    if (firstName.getText().length() == 0 
        || lastName.getText().length() == 0
        || displayName.getText().length() == 0
        || email.getText().length() == 0
        || smtpHost.getText().length() == 0
        || tlsPort.getText().length() == 0
        || password.getText().length() == 0 
    ) {
      return "Required field(s) are empty.";
    }
    if (!email.getText().contains("@")) {
      return "Invalid email format.";
    }

    return null;
  }

  public String getFirstName() {
    return this.firstName.getText();
  }

  public String getLastName() {
    return this.lastName.getText();
  }

  public String getDisplayName() {
    return this.displayName.getText();
  }

  public String getEmail() {
    return this.email.getText();
  }

  public String getSMTPHost() {
    return this.smtpHost.getText();
  }

  public String getTLSPort() {
    return this.tlsPort.getText();
  }

  public String getPassword() {
    return this.password.getText();
  }
}
