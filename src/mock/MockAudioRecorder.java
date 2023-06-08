import java.io.File;

/**
 * A mock class for handling microphone input.
 */
public class MockAudioRecorder implements IAudioRecorder {
  /**
   * Start the mock recording by creating a new file.
   */
  public boolean start(File file) {
    try {
      file.createNewFile();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Stop the mock recording by doing nothing.
   */
  public void stop() { }
}
