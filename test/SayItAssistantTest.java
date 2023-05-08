import java.io.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Top-level test runner, verifying program behavior.
 */
class SayItAssistantTest {
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

  /* Storage Tests */
  @Test
  void testStorageAdd() {
    Storage s = new Storage("[]");
    assertEquals(s.history.size(), 0);
    
    s.add("question", "response");
    s.add("question", "response");
    s.add("question", "response");
    assertEquals(s.history.size(), 3);
  }

  @Test
  void testStorageDelete() {
    Storage s = new Storage("[]");
    s.delete(null);
    assertEquals(s.history.size(), 0);

    s.add("question", "response");
    s.add("question", "response");
    s.add("question", "response");
    s.delete(s.history.get(0).id);
    assertEquals(s.history.size(), 2);
  }

  @Test
  void testStorageSave() throws Exception {
    Storage s = new Storage("[]");
    s.add(0, "question", "response");

    String dest = "test_history.json";
    s.save(dest);
    File file = new File(dest);
    assertTrue(file.exists());

    BufferedReader br = new BufferedReader(
      new FileReader(file)
    );

    String line, text = "";
    while ((line = br.readLine()) != null) {
      text += line;
    }
    br.close();
    file.delete();

    s = new Storage(text);
    assertEquals(s.history.size(), 1);
    assertEquals(s.history.get(0).timestamp, 0);
  }

  @Test
  void testStorageMalformed() { 
    Storage s = new Storage("[{\"}]");
    assertEquals(s.history.size(), 0);

    s.parse("[{\"timestamp\": 0, \"question\": \"\"}]");
    assertEquals(s.history.size(), 0);
  }
}
