import java.io.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Top-level test runner, verifying general program behavior.
 */
public class SayItAssistantTest {
  /* Whisper Tests */
  @Test
  void testWhisperBadFile() {
    assertEquals(null, Whisper.speechToText(null));
  }

  @Test
  void testWhisperEmptyFile() {
    File tmp = new File("dummy");
    assertEquals(null, Whisper.speechToText(tmp));
  }

  @Test
  void testWhisperNoToken() {
    assertEquals(null, Whisper.speechToText(new File("silent.wav")));
  }
}
