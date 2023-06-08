import java.io.File;

interface IAudioRecorder {
  boolean start(File file);

  void stop();
}
