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

/**
 * Top-level UI frame that constructs a text region
 *   and a taskbar.
 */
class AppFrame extends JFrame {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 480;
  private static final String QUESTION_FILE = "question.wav";
  private static final String TOKEN_FILE = ".token";

  /*
   * Individual UI components and the storage
   *   class they all interact with
   */
  private IAudioRecorder recorder = new AudioRecorder();
  private File stream = new File(QUESTION_FILE);
  private IBackendClient client;

  private LoginFlow loginFlow;
  private HistoryBar hist;
  private QuestionAndResponse convo;
  private TaskBar taskbar;

  /* The question/response pair currently being viewed */
  private HistoryItem selected = null;

  /* Map relating UUID strings to question buttons */
  private Map<String, JButton> buttonMap = new HashMap<>();

  /* Whether the user is currently recording audio */
  private boolean recording = false;

  private boolean connected = true;

  AppFrame(IBackendClient inClient) {
    /*
     * Set basic properties of the window frame
     */
    setSize(WIDTH, HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true); 

    /*
     * Set backend client handler
     */
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

    /*
     * Instantiate each individual component,
     *   populating with history where applicable
     */
    hist = new HistoryBar();
    convo = new QuestionAndResponse();
    taskbar = new TaskBar();

    try {
      File tokenFile = new File(TOKEN_FILE);
      if (!tokenFile.exists()) {
        throw new Exception();
      }

      String token = Files.readString(tokenFile.toPath(), StandardCharsets.UTF_8);
      if (client.checkToken(token)) {
        mainView(false);
      } else {
        throw new Exception();
      }
    } catch (Exception e) {
      loginFlow = new LoginFlow();
      add(loginFlow, BorderLayout.CENTER);
    }

    revalidate();
    repaint();

    /*
     * Attach event listeners to relevant buttons.
     */
    addListeners();
  }

  /**
   * Update the UI to show a given question/response
   *   combination.
   */
  void displayItem(HistoryItem item) {
    JButton button = hist.add(item);
    buttonMap.put(item.id.toString(), button);
    button.addActionListener((ActionEvent e) -> {
      convo.show(item);
      selected = item;
    });
    hist.revalidate();
    hist.repaint();
  }

  /**
   * Attach event listeners to all interactive parts
   *   of the UI that depend upon one another.
   * This is an example of the Observer pattern:
   *   We register several lambda functions that each
   *   respective subject calls when it updates.
   */
  void addListeners() {
    taskbar.startButton.addActionListener((ActionEvent e) -> {
      recording = !recording;
      taskbar.startButton.setText(recording ? "Stop" : "Start");

      if (recording) {
        recorder.start(stream);
      } else {
        recorder.stop();
        doAPIRequest();
      }
    });

    if (loginFlow != null) {
      loginFlow.signupDone.addActionListener((ActionEvent e) -> {
        String msg = loginFlow.checkInputs();
        if (msg != null) {
          JOptionPane.showMessageDialog(
              null,
              msg,
              "SayIt Assistant Error",
              JOptionPane.ERROR_MESSAGE
          );
          return;
        }

        if (!client.signup(loginFlow.getEmail(), loginFlow.getPassword())) {
          JOptionPane.showMessageDialog(
              null,
              connected ? "Email already in use." : "Failed to reach server.",
              "SayIt Assistant Error",
              JOptionPane.ERROR_MESSAGE
          );
          return;
        }

        doAutoLogin();
        mainView(true);
      });
      loginFlow.loginDone.addActionListener((ActionEvent e) -> {
        if (!client.login(loginFlow.getEmail(), loginFlow.getPassword())) {
          JOptionPane.showMessageDialog(
              null,
              connected ? "Invalid email or password." : "Failed to reach server.",
              "SayIt Assistant Error",
              JOptionPane.ERROR_MESSAGE
          );
          return;
        }

        doAutoLogin();
        mainView(true);
      });
    }
  }

  /**
   * Set up the main application view, after signup/login.
   */
  private void mainView(boolean hasFlow) {
    if (hasFlow) {
      remove(loginFlow);
    }

    ArrayList<HistoryItem> items = client.getHistory();
    if (items != null) {
      for (HistoryItem item : client.getHistory()) {
        displayItem(item);
      }
    }
    add(hist, BorderLayout.WEST);
    add(convo, BorderLayout.CENTER);
    add(taskbar, BorderLayout.SOUTH);
    revalidate();
    repaint();
  }

  /**
   * Helper for prompting the user to enable automatic
   *   login on this computer.
   */
  private void doAutoLogin() {
    int ans = JOptionPane.showConfirmDialog(
        null,
        "Would you like to enable automatic login on this computer?",
        "SayIt Assistant",
        JOptionPane.YES_NO_OPTION
    );
    if (ans == 0) {
      try {
        PrintWriter writer = new PrintWriter(TOKEN_FILE);
        writer.print(client.getToken());
        writer.close();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            null,
            "Failed to enable automatic login.",
            "SayIt Assistant Error",
            JOptionPane.ERROR_MESSAGE
        );
      }
    }
  }

  /**
   * Helper for making API calls.
   */
  private void doAPIRequest() {
    Thread networkThread = new Thread(() -> {
      String id = (selected == null ? "" : selected.id.toString());
      APIOperation op = client.sendVoice(stream, id);
      if (op == null || !op.success) {
        String msg = "";
        if (op == null) {
          msg = "Failed to make request, please try again.";
        } else if (op.command.equals("send")) {
          msg = "Failed to send email. Are your email settings "
              + "properly configured?";
        } else {
          msg = "Invalid command, please try again.";
        }
        JOptionPane.showMessageDialog(
            null,
            msg,
            "SayIt Assistant Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
      }

      Container parent = null;
      HistoryItem item = null;
      switch (op.command) {
        case "question":
        case "create":
        case "send":
          item = HistoryItem.fromString(op.message);
          displayItem(item);
          convo.show(item);
          selected = item;
          break;
        case "delete":
          if (selected == null) {
            return;
          }
  
          JButton toRemove = buttonMap.get(id);
          parent = toRemove.getParent();
          parent.remove(toRemove);
          parent.revalidate();
          parent.repaint();
          buttonMap.remove(id);
          selected = null;
  
          convo.show(null); 
          break;
        case "clear":
          for (JButton button : buttonMap.values()) {
            if (parent == null) {
              parent = button.getParent();
            }
            parent.remove(button);
          }
          if (parent != null) {
            convo.show(null);
            parent.revalidate();
            parent.repaint();
          }
          break;
        case "set up":
        case "setup":
          new SetupEmailFrame(this.client);
          break;
      }
    });
    networkThread.start();
  }
}
