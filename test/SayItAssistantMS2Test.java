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
  void testBackendClient() {
    IBackendClient client = new HttpBackendClient();
    IBackendClient mock = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(mock.connected());
  }

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
  void testSignup() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());
    assertTrue(mockClient.signup("helen@gmail.com", "password"));
    assertNotNull(mockClient.getToken());
  }

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
  void testVoiceCommand() {
    IBackendClient mockClient = new MockBackendClient();
    assertEquals("2 plus 2 equals 4.", mockClient.askQuestion("What is 2 plus 2?").response);
  }

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

  /* MS2 User Story 4 Tests (BDD Scenarios) */
  @Test
  void testAccountHistory() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    mockClient.askQuestion("What is 2 plus 2?");
    assertEquals("What is 2 plus 2?", mockClient.getHistory().get(0).question);
  }

  @Test
  void testMS2Story4_BDD1() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    assertEquals(new ArrayList<HistoryItem>(), mockClient.getHistory());
  }

  @Test
  void testMS2Story4_BDD2() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    mockClient.askQuestion("What is 2 plus 2?");

    assertEquals("What is 2 plus 2?", mockClient.getHistory().get(0).question);
    assertEquals("2 plus 2 equals 4.", mockClient.getHistory().get(0).response);
  }

  @Test
  void testMS2Story4_BDD3() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    mockClient.askQuestion("What is 2 plus 2?");
    mockClient.askQuestion("What is your favorite color?");
    mockClient.askQuestion("What is the largest country in the world?");

    assertEquals("What is 2 plus 2?", mockClient.getHistory().get(0).question);
    assertEquals("2 plus 2 equals 4.", mockClient.getHistory().get(0).response);
  }

  /* MS2 User Story 7 Tests (BDD Scenarios) */
  @Test
  void testLogin() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());
    assertFalse(mockClient.login("helen@gmail.com", "password"));
    assertTrue(mockClient.signup("helen@gmail.com", "password"));
    assertTrue(mockClient.login("helen@gmail.com", "password"));
  }

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
