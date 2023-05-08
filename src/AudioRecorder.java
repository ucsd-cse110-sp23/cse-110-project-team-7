import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

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

    this.format = new AudioFormat(
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
  public void start(File file) {
    try {
      this.targetDataLine = AudioSystem.getTargetDataLine(this.format);
      this.targetDataLine.open(this.format);
      this.targetDataLine.start();

      AudioInputStream audioInputStream = new AudioInputStream(this.targetDataLine);

      Thread audioThread = new Thread(() -> {
        try {
          AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        } catch (Exception err) {
          err.printStackTrace();
        }
      });

      audioThread.start();

      this.audioThread = audioThread;
    } catch (Exception err){
      err.printStackTrace();
    }
  }

  /**
   * This method stops the thread that runs the recording.
   * 
   * Source: CSE 110 Lab 5
   */
  public void stop() {
    try {
      this.targetDataLine.stop();
      this.targetDataLine.close();
      this.audioThread.join();
      System.out.println("Finished recording");
    } catch (Exception err) {
      err.printStackTrace();
    }
  }
}
