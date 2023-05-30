import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test runner for all Milestone 2 features and stories.
 */
class SayItAssistantMS2Test {
  @BeforeAll
  static void pre_startServer() {
    String[] args = { "--test" };
    SayItAssistantServer.main(args);
  }

  /* MS2 User Story 1 Tests (BDD Scenarios) */
  @Test
  void testMS2Story1_BDD1() {
    IBackendClient client = new HttpBackendClient();
    assertTrue(client.connected());

    ArrayList<HistoryItem> empty = new ArrayList<>();
    assertEquals(empty, client.getHistory());

    HistoryItem item = client.askQuestion(new File("test/silent.wav"));
    assertNotNull(item);
    assertEquals("What is 2 plus 2?", item.question);
    assertEquals("2 plus 2 equals 4.", item.response);

    item = client.askQuestion(new File("test/silent.wav"));
    assertNotNull(item);
    assertEquals("What is your favorite color?", item.question);
    assertEquals("My favorite color is blue.", item.response);
  }

  @Test
  void testMS2Story1_BDD2() {
    IBackendClient client = new HttpBackendClient();
    assertTrue(client.connected());

    ArrayList<HistoryItem> hist = client.getHistory();
    assertEquals("What is 2 plus 2?", hist.get(0).question);
    assertEquals("2 plus 2 equals 4.", hist.get(0).response);

    assertEquals("What is your favorite color?", hist.get(1).question);
    assertEquals("My favorite color is blue.",  hist.get(1).response);
  }
}