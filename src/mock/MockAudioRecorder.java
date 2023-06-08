import java.io.File;

/**
 * A mock class for handling microphone input.
 */
public class MockAudioRecorder implements IAudioRecorder {
  public boolean start(File file) {
    try {
      file.createNewFile();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void stop() { }
}
