import java.io.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Top-level test runner, verifying general program behavior.
 */
public class SayItAssistantTest {
  @Test
  void testDummy() {
    assertEquals(0, 0);
  }

  /**
   * Tests to ensure that a recording file is generated
   */
  @Test
  void testAudioRecord() {
    AudioRecorder recorder = new AudioRecorder();

    recorder.startRecording();

    try {
      Thread.sleep(5000);
    } catch(Exception e) {
      e.printStackTrace();
    }
    recorder.stopRecording();

    File file = new File("question.wav");

    assertTrue(file.exists());

  }
}
