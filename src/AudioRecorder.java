import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class AudioRecorder {
    
    private AudioFormat format;
    private TargetDataLine targetDataLine;

    /**
     * The constructor generates the audio
     * format used for constructing the wav
     * file.
     * 
     * Source: CSE 110 Lab 5
     */
    public AudioRecorder() {
        float sampleRate = 44100;

        int sampleSizeInBits = 16;

        int channels = 2;

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
    public void startRecording() {
        Thread thread = new Thread(
            () -> {
                try {

                    DataLine.Info dataLineInfo = new DataLine.Info(
                        TargetDataLine.class,
                        this.format
                    );

                    this.targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                    this.targetDataLine.open(this.format);
                    this.targetDataLine.start();

                    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
                    File audioFile = new File("question.wav");
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE,
                        audioFile);
                } catch (Exception err){
                    err.printStackTrace();
                }
            }
        );
        thread.start();
    }

    public void stopRecording() {
        this.targetDataLine.stop();
        this.targetDataLine.close();
    }
}
