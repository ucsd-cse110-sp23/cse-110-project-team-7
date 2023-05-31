import java.io.*;
import java.util.ArrayList;
import java.util.UUID;
import org.bson.Document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test runner for all Milestone 1 features and stories.
 */
class SayItAssistantMS1Test {
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
    Whisper w = new Whisper();
    assertNull(w.speechToText(null));
  }

  @Test
  void testWhisperEmptyFile() {
    Whisper w = new Whisper();
    File tmp = new File("dummy");
    assertNull(w.speechToText(tmp));
  }

  @Test
  void testWhisperNoToken() {
    Whisper w = new Whisper();
    assertNull(w.speechToText(new File("test/silent.wav")));
  }

  /* ChatGPT Tests */
  @Test
  void testChatGPTBadQuestion() {
    ChatGPT c = new ChatGPT();
    assertNull(c.ask(null));
  }

  @Test
  void testChatGPTNoToken() {
    ChatGPT c = new ChatGPT();
    assertNull(c.ask("What is the smallest country in the world?"));
  }

  /* User Story 1 Tests (BDD Scenarios) */
  @Test
  void testStory1_BDD1() {
    MockWhisper mockWhisper = new MockWhisper();
    MockChatGPT mockChatGPT = new MockChatGPT();

    File file = new File("question.wav");
    String question = mockWhisper.speechToText(file);
    assertEquals("Question. What is 2 plus 2?", question);

    String response = mockChatGPT.ask(question);
    assertEquals("2 plus 2 equals 4.", response);
  }

  @Test
  void testStory1_BDD2() {
    MockWhisper mockWhisper = new MockWhisper();
    MockChatGPT mockChatGPT = new MockChatGPT();

    File file = new File("question.wav");
    String question = mockWhisper.speechToText(file);
    assertEquals("Question. What is 2 plus 2?", question);

    String response = mockChatGPT.ask(question);
    assertEquals("2 plus 2 equals 4.", response);

    file = new File("question.wav");
    question = mockWhisper.speechToText(file);
    assertEquals("Question. What is your favorite color?", question);

    response = mockChatGPT.ask(question);
    assertEquals("My favorite color is blue.", response);
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
    String no_uuid = null;
    assertFalse(s.delete(no_uuid));
    assertEquals(s.history.size(), 0);

    s.add("question", "response");
    s.add("question", "response");
    s.add("question", "response");
    assertTrue(s.delete(s.history.get(0).id));
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

  @Test
  void testStorageSameQuestion() throws Exception {
    Storage s = new Storage("[]");
    s.add("What is 2 + 2?", "4");
    s.add("What is 2 + 2?", "The answer is 4.");
    s.add("What is 2 + 2?", "Two plus two equals 4");

    String dest = "same_prompts.json";
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
    assertEquals(s.history.size(), 3);
    assertEquals(s.history.get(0).question, "What is 2 + 2?");
    assertEquals(s.history.get(0).response, "4");
    assertEquals(s.history.get(1).question, "What is 2 + 2?");
    assertEquals(s.history.get(1).response, "The answer is 4."); 
    assertEquals(s.history.get(2).question, "What is 2 + 2?");
    assertEquals(s.history.get(2).response, "Two plus two equals 4");
  }

  @Test
  void testStorageSerialize() {
    final String dummy_uuid = "123e4567-e89b-42d3-a456-556642440000";

    Storage s = new Storage("[]");
    assertEquals("[]", s.serialize());

    s.add(dummy_uuid, 0, "hello", "world");
    assertEquals("[{\"question\":\"hello\",\"response\":\"world\",\"uuid\":\"" + dummy_uuid + "\",\"timestamp\":0}]", s.serialize());
  }

  /* User Story 2 Tests (BDD Scenarios) */
  @Test
  void testStory2_BDD1() throws Exception {
    Storage s = new Storage("[]");
    ArrayList<String> questions = new ArrayList<>();
    questions.add("What is 2 + 2?");
    questions.add("What is the meaning of life?");
    questions.add("Is the sky purple?");
    questions.add("What is your name?");
    questions.add("What is your favorite color?");
    ArrayList<String> answers = new ArrayList<>();
    answers.add("4");
    answers.add("Life is the condition that distinguishes animals and plants from" +
    " inorganic matter, including the capacity for growth, reproduction, functional activity, and continual change" + 
    " preceding death.");
    answers.add("No");
    answers.add("Say-It Assistant");
    answers.add("Green");

    s.add(0, questions.get(0), answers.get(0));
    s.add(1, questions.get(1), answers.get(1));
    s.add(2, questions.get(2), answers.get(2));
    s.add(3, questions.get(3), answers.get(3));
    s.add(4, questions.get(4), answers.get(4));
    String dest = "BDD1.json";
    s.save(dest);
    File file = new File(dest);
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
    
    assertEquals(s.history.size(), 5);
    assertEquals(s.history.get(0).question, questions.get(0));
    assertEquals(s.history.get(1).question, questions.get(1));
    assertEquals(s.history.get(2).question, questions.get(2));
    assertEquals(s.history.get(3).question, questions.get(3));
    assertEquals(s.history.get(4).question, questions.get(4));

    assertEquals(s.history.get(0).response, answers.get(0));
    assertEquals(s.history.get(1).response, answers.get(1));
    assertEquals(s.history.get(2).response, answers.get(2));
    assertEquals(s.history.get(3).response, answers.get(3));
    assertEquals(s.history.get(4).response, answers.get(4));
  }


  @Test
  void testStory2_BDD2() throws Exception {
    Storage s = new Storage("[]");
    String dest = "BDD2.json";
    s.save(dest);
    File file = new File(dest);

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
    assertEquals(s.history.size(), 0);
  }

  @Test
  void testStory2_BDD3() throws Exception {
    Storage s = new Storage("[]");
    for (int i = 0; i <= 20; i++) {
      s.add(i, "What is " + i + " + " + i + "?", "" + (i+i));
    }
    String dest = "BDD3.json";
    s.save(dest);
    File file = new File(dest);
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
    assertEquals(s.history.size(), 21);
    for (int i = 0;  i <= 20; i++) {
      assertEquals(s.history.get(i).question, "What is " + i + " + " + i + "?");
      assertEquals(s.history.get(i).response, "" + (i+i));
    }
  }
  
  /* PromptHandler Tests */
  @Test
  void testHandlerGet() {
    PromptHandler handler = new PromptHandler(new MockChatGPT(), new Prompt(), true);
    ArrayList<Document> list = new ArrayList<>();
    list.add(new Document()
        .append("uuid", "fake_id")
        .append("timestamp", 1000L)
        .append("question", "What is 2 plus 2?")
        .append("response", "4")
    );
    Document user = new Document().append("history", list);

    // Query all
    assertNotNull(handler.handleGet(user, null));

    // Query one that does not exist
    assertNull(handler.handleGet(user, UUID.randomUUID().toString()));

    // Query one that does exist
    assertEquals("4", handler.handleGet(user, "fake_id"));

    // Delete from storage, then confirm question no longer exists
    list.remove(0);
    assertNull(handler.handleGet(user, "fake_id"));
    assertEquals("[]", handler.handleGet(user, null));
  }

  @Test
  void testHandlerDelete() {
    PromptHandler handler = new PromptHandler(new MockChatGPT(), new Prompt(), true);

    ArrayList<Document> list = new ArrayList<>();
    Document user = new Document().append("history", list);

    // Delete non-existent item(s)
    assertNull(handler.handleDelete(user, UUID.randomUUID().toString()));

    // Delete all items
    assertEquals("Successfully deleted.", handler.handleDelete(user, null));

    // Delete existing item
    list.add(new Document().append("uuid", "fake_id"));
    assertEquals("Successfully deleted.", handler.handleDelete(user, "fake_id"));
  }

  /* HttpClient Tests */
  @Test
  void testHttpClientGet() {
    IBackendClient mockClient = new MockBackendClient();

    // Should fail gracefully if server is not running
    assertEquals(0, mockClient.getHistory().size());
  }

  @Test
  void testHttpClientPost() {
    IBackendClient concreteClient = new HttpBackendClient();
    IBackendClient mockClient = new MockBackendClient();

    assertNull(concreteClient.askQuestion(null));

    assertNull(concreteClient.askQuestion("What is 2 plus 2?"));

    HistoryItem hist = mockClient.askQuestion(null);
    assertNotNull(hist);
  }

  @Test
  void testHttpClientDelete() {
    IBackendClient mockClient = new MockBackendClient();

    assertTrue(mockClient.deleteQuestion(null));
    assertTrue(mockClient.deleteQuestion(UUID.randomUUID()));

    assertTrue(mockClient.clearHistory());
  }

  /* User Story 7 Tests (BDD Scenarios) */
  @Test
  void testStory7_BDD1() {
    IBackendClient client = new MockBackendClient();
    HistoryItem hist = client.askQuestion(null);
    assertEquals(hist.question, "What is 2 plus 2?");
    assertEquals(hist.response, "2 plus 2 equals 4.");
  }

  @Test
  void testStory7_BDD2() {
    IBackendClient client = new MockBackendClient();
    ArrayList<HistoryItem> hist = client.getHistory();
    assertNotNull(hist);

    HistoryItem item = client.askQuestion("What is 2 plus 2?");
    assertNotNull(item);
  }

  @Test
  void testStory7_BDD3() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.deleteQuestion(null));
    assertTrue(client.deleteQuestion(UUID.randomUUID()));
  }

  @Test
  void testStory7_BDD4() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.clearHistory());
  }
}
