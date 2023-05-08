import java.io.File;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

/**
 * A class for handling microphone input, for use with
 *   OpenAI's Whisper API.
 */
public class AudioRecorder {
  private AudioFormat format;
  private TargetDataLine targetDataLine;
  private Thread audioThread;

  /**
   * The constructor generates the audio
   * format used for constructing the wav
   * file.
   * 
   * Source: CSE 110 Lab 5
   */
  public AudioRecorder() {
    float sampleRate = 8000;
    int sampleSizeInBits = 16;
    int channels = 1;
    boolean signed = true;
    boolean bigEndian = false;

    format = new AudioFormat(
      sampleRate,
      sampleSizeInBits,
      channels,
      signed,
      bigEndian
    );
  }

  /**
   * This method creates an audio file of 
   * user's input. 
   * 
   * Source: CSE 110 Lab 5
   */
  public boolean start(File file) {
    try {
      targetDataLine = AudioSystem.getTargetDataLine(format);
      targetDataLine.open(format);
      targetDataLine.start();

      AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

      audioThread = new Thread(() -> {
        try {
          AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        } catch (Exception err) {
          err.printStackTrace();
        }
      });

      audioThread.start();
      return true;
    } catch (Exception err) {
      err.printStackTrace();
      return false;
    }
  }

  /**
   * This method stops the thread that runs the recording.
   * 
   * Source: CSE 110 Lab 5
   */
  public void stop() {
    try {
      targetDataLine.stop();
      targetDataLine.close();
      audioThread.join();
    } catch (Exception err) {
      err.printStackTrace();
    }
  }
}
