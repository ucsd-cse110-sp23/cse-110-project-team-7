import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFrame;

/**
 * Top-level UI frame that constructs a text region
 *   and a taskbar.
 */
class AppFrame extends JFrame {
  private static final String QUESTION_FILE = "question.wav";

  /*
   * Individual UI components and the utilities
   *   class they all interact with
   */
  private QuestionAndResponse convo;
  private TaskBar taskbar;
  
  private AudioRecorder recorder;
  private File stream;

  /* Whether the user is currently recording audio */
  private boolean recording = false;


  AppFrame() {
    /*
     * Set basic properties of the window frame
     */
    setSize(640, 480);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);

    convo = new QuestionAndResponse();
    add(convo, BorderLayout.CENTER);

    taskbar = new TaskBar();
    add(taskbar, BorderLayout.SOUTH);

    recorder = new AudioRecorder();
    addListeners();
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
        stream = new File(QUESTION_FILE);
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

          convo.show(question, response);
        });
        networkThread.start();
      }
    });
  }
}
