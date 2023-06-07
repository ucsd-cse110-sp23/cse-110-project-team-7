
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
  private SetupEmailFlow setupEmailFlow;

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

    setupEmailFlow = new SetupEmailFlow();
    add(setupEmailFlow, BorderLayout.CENTER);

    revalidate();
    repaint();

    addListeners();

  }

  void addListeners() {
    setupEmailFlow.cancel.addActionListener((ActionEvent e) -> {
        this.dispose();
      });
      setupEmailFlow.save.addActionListener((ActionEvent e) -> {
        String inputCheck = setupEmailFlow.checkInputs();
        if (inputCheck != null) {
          JOptionPane.showMessageDialog(
            null,
            inputCheck,
            "Invalid inputs",
            JOptionPane.ERROR_MESSAGE
          );
        }
        Boolean success = client.addEmailDetails(setupEmailFlow.getFirstName(), 
              setupEmailFlow.getLastName(), 
              setupEmailFlow.getDisplayName(), 
              setupEmailFlow.getEmail(), 
              setupEmailFlow.getSMTPHost(), 
              setupEmailFlow.getTLSPort(), 
              setupEmailFlow.getPassword());
        if(!success) {
          JOptionPane.showMessageDialog(
            null,
            "Error updating email",
            "Server Error",
            JOptionPane.ERROR_MESSAGE
          );
        }
        this.dispose();
      });
  }

  
}

