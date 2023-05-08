import java.io.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Top-level test runner, verifying general program behavior.
 */
public class SayItAssistantTest {
  /* AudioRecorder tests */
  @Test
  void testAudioRecorder() {
    File file = new File("question.wav");
    AudioRecorder recorder = new AudioRecorder();
    if (recorder.start(file)) {
      recorder.stop();
      assertTrue(file.exists());
      file.delete();
    } else {
      System.out.println("System does not have audio input, skipping test.");
    }
  }

  /* Whisper Tests */
  @Test
  void testWhisperBadFile() {
    assertNull(Whisper.speechToText(null));
  }

  @Test
  void testWhisperEmptyFile() {
    File tmp = new File("dummy");
    assertNull(Whisper.speechToText(tmp));
  }

  @Test
  void testWhisperNoToken() {
    assertNull(Whisper.speechToText(new File("silent.wav")));
  }

  /* ChatGPT Tests */
  @Test
  void testChatGPTBadQuestion() {
    assertNull(ChatGPT.ask(null));
  }

  @Test
  void testChatGPTNoToken() {
    assertNull(ChatGPT.ask("What is the smallest country in the world?"));
  }

  /* User Story 1 Tests (BDD Scenarios) */
  @Test
  void testStory1_BDD1() {
    MockWhisper mockWhisper = new MockWhisper();
    MockChatGPT mockChatGPT = new MockChatGPT();

    File file = new File("question.wav");
    String question = mockWhisper.speechToText(file);
    assertEquals("What is 2 plus 2?", question);

    String response = mockChatGPT.ask(question);
    assertEquals("2 plus 2 equals 4.", response);
  }

  @Test
  void testStory1_BDD2() {
    MockWhisper mockWhisper = new MockWhisper();
    MockChatGPT mockChatGPT = new MockChatGPT();

    File file = new File("question.wav");
    String question = mockWhisper.speechToText(file);
    assertEquals("What is 2 plus 2?", question);

    String response = mockChatGPT.ask(question);
    assertEquals("2 plus 2 equals 4.", response);

    file = new File("question.wav");
    question = mockWhisper.speechToText(file);
    assertEquals("What is your favorite color?", question);

    response = mockChatGPT.ask(question);
    assertEquals("My favorite color is blue.", response);
  }
}
