import java.io.File;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;

/**
 * Top-level UI frame that constructs a text region
 *   and a taskbar.
 */
class AppFrame extends JFrame {
  private static final String QUESTION_FILE = "question.wav";

  private QuestionAndResponse convo;
  private TaskBar taskbar;
  
  private AudioRecorder recorder;
  private File stream;

  /* Whether the user is currently recording audio */
  private boolean recording = false;


  AppFrame() {
    this.setSize(640, 480);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

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

        Thread whisperThread = new Thread(() -> {
          String question = Whisper.speechToText(stream);
          System.out.println(question);
        });
        whisperThread.start();

        stream.delete();
      }
    });
  }
}
