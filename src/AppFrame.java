import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

/**
 * Top-level UI frame that constructs a text region
 *   and a taskbar.
 */
class AppFrame extends JFrame {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 480;

  private Storage storage = new Storage();

  private HistoryBar hist;
  private QuestionAndResponse convo;
  private TaskBar taskbar;

  private boolean recording = false;

  AppFrame() {
    setSize(WIDTH, HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    hist = new HistoryBar();
    for (HistoryItem item : storage.history) {
      hist.add(item);
    }
    add(hist, BorderLayout.WEST);

    convo = new QuestionAndResponse();
    add(convo, BorderLayout.CENTER);

    taskbar = new TaskBar();
    add(taskbar, BorderLayout.SOUTH);

    addListeners();
    Runtime.getRuntime().addShutdownHook(
      new Thread(() -> storage.save())
    );
  }

  void addListeners() {
    taskbar.newQuestionButton.addActionListener((ActionEvent e) -> {
      recording = !recording;
      taskbar.newQuestionButton.setText(recording ? "Stop Recording" : "New Question");

      if (!recording) {
        HistoryItem item = storage.add("hello", "world");
        hist.add(item);
      }
    });
  }
}
