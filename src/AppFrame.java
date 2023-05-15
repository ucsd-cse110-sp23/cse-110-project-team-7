import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
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
  private Storage storage = new Storage();
  private AudioRecorder recorder = new AudioRecorder();
  private File stream = new File(QUESTION_FILE);

  private HistoryBar hist;
  private QuestionAndResponse convo;
  private TaskBar taskbar;

  /* Whether the user is currently recording audio */
  private boolean recording = false;

  AppFrame() {
    /*
     * Set basic properties of the window frame
     */
    setSize(WIDTH, HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    /*
     * Instantiate each individual component,
     *   populating with history where applicable
     */
    hist = new HistoryBar();
    for (HistoryItem item : storage.history) {
      displayItem(item);
    }
    add(hist, BorderLayout.WEST);

    convo = new QuestionAndResponse();
    add(convo, BorderLayout.CENTER);

    taskbar = new TaskBar();
    add(taskbar, BorderLayout.SOUTH);

    /*
     * Attach event listeners and save the user's
     *   history on program shutdown
     */
    addListeners();
    Runtime.getRuntime().addShutdownHook(
      new Thread(() -> storage.save())
    );
  }

  /**
   * Update the UI to show a given question/response
   *   combination.
   */
  void displayItem(HistoryItem item) {
    JButton button = hist.add(item);
    button.addActionListener((ActionEvent e) -> {
      convo.show(item);
    });
  }

  /**
   * Attach event listeners to all interactive parts
   *   of the UI that depend upon one another.
   */
  void addListeners() {
    taskbar.newQuestionButton.addActionListener((ActionEvent e) -> {
      recording = !recording;
      taskbar.newQuestionButton.setText(recording ? "Stop Recording" : "New Question");

      if (recording) {
        recorder.start(stream);
      } else {
        recorder.stop();

        Thread networkThread = new Thread(() -> {
          String question = Whisper.speechToText(stream);
          stream.delete();
          if (question == null) {
            return;
          }

          String response = ChatGPT.ask(question);
          if (response == null) {
            return;
          }

          HistoryItem item = storage.add(question, response);
          displayItem(item);
          convo.show(item);
        });
        networkThread.start();
      }
    });

    taskbar.clearAllButton.addActionListener((ActionEvent e) -> {
      storage.clear();
      System.out.println("All responses were cleared");
    });
  }
}
