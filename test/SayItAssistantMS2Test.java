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
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());

    ArrayList<HistoryItem> empty = new ArrayList<>();
    assertEquals(empty, client.getHistory());

    HistoryItem item = client.askQuestion("What is 2 plus 2?");
    assertNotNull(item);
    assertEquals("What is 2 plus 2?", item.question);
    assertEquals("2 plus 2 equals 4.", item.response);

    item = client.askQuestion("What is 2 plus 2?");
    assertNotNull(item);
    assertEquals("What is 2 plus 2?", item.question);
    assertEquals("2 plus 2 equals 4.", item.response);
  }

  @Test
  void testMS2Story1_BDD2() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());

    client.askQuestion("What is 2 plus 2?");
    client.askQuestion("What is 2 plus 2?");

    ArrayList<HistoryItem> hist = client.getHistory();
    assertEquals("What is 2 plus 2?", hist.get(0).question);
    assertEquals("2 plus 2 equals 4.", hist.get(0).response);

    assertEquals("What is 2 plus 2?", hist.get(1).question);
    assertEquals("2 plus 2 equals 4.",  hist.get(1).response);
  }

  /* MS2 User Story 2 Tests (BDD Scenarios) */
  @Test
  void testMS2Story2_BDD1() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());

    assertTrue(client.signup("helen@gmail.com", "password"));
    assertNotNull(client.getToken());
    assertTrue(client.checkToken(client.getToken()));
  }

  @Test
  void testMS2Story2_BDD2() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.signup("helen@gmail.com", "password"));

    assertFalse(client.signup("helen@gmail.com", "newpassword"));
  }
  
  @Test
  void testMS2Story2_BDD3() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    
    String password = "password";
    String verification = "notpassword";
    assertFalse(password.equals(verification));

    verification = password;
    assertTrue(client.signup("helen@gmail.com", password));
  }

  /* MS2 User Story 3 Tests (BDD Scenarios) */
  @Test
  void testMS2Story3_BDD1() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    File f = new File("test/silent.wav");
    assertEquals("POST", mockClient.questionType(f));

    HistoryItem hist = mockClient.askQuestion("Question. What is 2 plus 2?");
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);
  }

  @Test
  void testMS2Story3_BDD2() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    File f = new File("test/silent.wav");
    assertEquals("POST", mockClient.questionType(f));

    HistoryItem hist = mockClient.askQuestion("Question. What is 2 plus 2?");
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);

    hist = mockClient.askQuestion("Question. What is 2 plus 2?");
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);

    hist = mockClient.askQuestion("Question. What is 2 plus 2?");
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);
  }

  /* MS2 User Story 7 Tests (BDD Scenarios) */
  @Test
  void testMS2Story7_BDD1() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.signup("helen@gmail.com", "password"));

    assertTrue(client.login("helen@gmail.com", "password"));
  }

  @Test
  void testMS2Story7_BDD2() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.signup("helen@gmail.com", "password"));

    assertFalse(client.login("helen@gmail.com", "notpassword"));
  }

  @Test
  void testMS2Story7_BDD3() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.signup("helen@gmail.com", "password"));

    assertFalse(client.login("nothelen@gmail.com", "password"));
  }

  @Test
  void testMS2Story7_BDD4() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.signup("helen@gmail.com", "password"));
    assertTrue(client.checkToken(client.getToken()));
  }
}
