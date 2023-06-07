
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
import javax.swing.JTextField;
import javax.swing.border.Border;

class SetupEmailFrame extends JFrame {
  private static final int WIDTH = 400;
  private static final int HEIGHT = 280;
  private static final String TOKEN_FILE = ".token";

  /*
   * UI Components
   */
  private IBackendClient client;
  private boolean connected = true;

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
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    client = inClient;
    if (!client.connected()) {
        JOptionPane.showMessageDialog(
          null,
          "Failed to connect to server, application will not function correctly.",
          "SayIt Assistant Error",
          JOptionPane.ERROR_MESSAGE
      );
      connected = false;
    }

    JPanel paneSetupEmail = new JPanel();
    paneSetupEmail.setLayout(new BoxLayout(paneSetupEmail, BoxLayout.Y_AXIS));

    paneSetupEmail.add(new JLabel("First Name: "));
    firstName = new JTextField();
    paneSetupEmail.add(firstName);

    paneSetupEmail.add(new JLabel("Last Name: "));
    lastName = new JTextField();
    paneSetupEmail.add(lastName);
        
    paneSetupEmail.add(new JLabel("Display Name: "));
    displayName = new JTextField();
    paneSetupEmail.add(displayName);

    paneSetupEmail.add(new JLabel("Email: "));
    email = new JTextField();
    paneSetupEmail.add(email);

    paneSetupEmail.add(new JLabel("SMTP host: "));
    smtpHost = new JTextField();
    paneSetupEmail.add(smtpHost);

    paneSetupEmail.add(new JLabel("TLS port: "));
    tlsPort = new JTextField();
    paneSetupEmail.add(tlsPort);

    paneSetupEmail.add(new JLabel("Password: "));
    password = new JTextField();
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
        System.out.println("Checking cancel");
        this.dispose();
      });
    this.save.addActionListener((ActionEvent e) -> {
      System.out.println("Checking save");
        String inputCheck = this.checkInputs();
        if (inputCheck != null) {
          JOptionPane.showMessageDialog(
            null,
            inputCheck,
            "Invalid inputs",
            JOptionPane.ERROR_MESSAGE
          );
        } else {
          Boolean success = client.addEmailDetails(this.getFirstName(), 
              this.getLastName(), 
              this.getDisplayName(), 
              this.getEmail(), 
              this.getSMTPHost(), 
              this.getTLSPort(), 
              this.getPassword());
          if(!success) {
            JOptionPane.showMessageDialog(
            null,
            "Error updating email",
            "Server Error",
            JOptionPane.ERROR_MESSAGE
          );
          } else {
            this.dispose();
          }
        }
      });
  }

  public String checkInputs() {
    if(firstName.getText().length() == 0 
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

