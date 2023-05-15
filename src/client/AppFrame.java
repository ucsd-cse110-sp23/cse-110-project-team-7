import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Top-level UI frame that constructs a text region
 *   and a taskbar.
 */
class AppFrame extends JFrame {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 480;
  private static final String QUESTION_FILE = "question.wav";

  /*
   * Individual UI components and the storage
   *   class they all interact with
   */
  private AudioRecorder recorder = new AudioRecorder();
  private File stream = new File(QUESTION_FILE);
  private IBackendClient client;

  private HistoryBar hist;
  private QuestionAndResponse convo;
  private TaskBar taskbar;

  /* The question/response pair currently being viewed */
  private HistoryItem selected = null;

  /* Map relating UUID strings to question buttons */
  private Map<String, JButton> buttonMap = new HashMap<>();

  /* Whether the user is currently recording audio */
  private boolean recording = false;

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

    /*
     * Instantiate each individual component,
     *   populating with history where applicable
     */
    hist = new HistoryBar();
    ArrayList<HistoryItem> items = client.getHistory();
    if (items != null) {
      for (HistoryItem item : client.getHistory()) {
        displayItem(item);
      }
    }
    add(hist, BorderLayout.WEST);

    convo = new QuestionAndResponse();
    add(convo, BorderLayout.CENTER);

    taskbar = new TaskBar();
    add(taskbar, BorderLayout.SOUTH);

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
    taskbar.deleteQuestionButton.addActionListener((ActionEvent e) -> {
      if (selected == null || !client.deleteQuestion(selected.id)) {
        return;
      }

      JButton toRemove = buttonMap.get(selected.id.toString());
      Container parent = toRemove.getParent();
      parent.remove(toRemove);
      parent.revalidate();
      parent.repaint();
      buttonMap.remove(selected.id.toString());
      selected = null;

      convo.show(null);
    });
    taskbar.newQuestionButton.addActionListener((ActionEvent e) -> {
      recording = !recording;
      taskbar.newQuestionButton.setText(recording ? "Stop Recording" : "New Question");

      if (recording) {
        recorder.start(stream);
      } else {
        recorder.stop();

        Thread networkThread = new Thread(() -> {
          HistoryItem item = client.askQuestion(stream);
          displayItem(item);
          convo.show(item);
          selected = item;
        });
        networkThread.start();
      }
    });
    taskbar.clearAllButton.addActionListener((ActionEvent e) -> {
      if (!client.clearHistory()) {
        return;
      }

      Container parent = null;
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
    });
  }
}
