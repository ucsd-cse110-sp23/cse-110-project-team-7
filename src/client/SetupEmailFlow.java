import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

class SetupEmailFlow extends JPanel {
    private static final Dimension TEXT_SIZE = new Dimension(300, 20);

    private JTextField firstName;
    private JTextField lastName;
    private JTextField displayName;
    private JTextField email;
    private JTextField smtpHost;
    private JTextField tlsPort;
    private JTextField password;

    public JButton save;
    public JButton cancel;

    SetupEmailFlow() {
        setPreferredSize(new Dimension(200, 200));

        /*Setup Email */
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

        cancel = new JButton();
        paneSetupEmail.add(cancel);

        save = new JButton();
        paneSetupEmail.add(save);

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
