import com.sun.net.httpserver.HttpExchange;
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
  /* MS2 User Story 1 Tests (BDD Scenarios) */
  @Test
  void testMS2Story1_BDD1() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());

    ArrayList<HistoryItem> empty = new ArrayList<>();
    assertEquals(empty, client.getHistory());

    File tmp = new File("test/silent.wav");
    APIOperation op = client.sendVoice(tmp, "id");
    assertNotNull(op);
    assertTrue(op.success);

    HistoryItem item = HistoryItem.fromString(op.message);
    assertEquals("What is 2 plus 2?", item.question);
    assertEquals("2 plus 2 equals 4.", item.response);
  }

  @Test
  void testMS2Story1_BDD2() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());

    File tmp = new File("test/silent.wav");
    client.sendVoice(tmp, "id1");
    client.sendVoice(tmp, "id2");

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

    File tmp = new File("silent.wav");
    APIOperation op = mockClient.sendVoice(tmp, "id");
    assertTrue(op.success);

    HistoryItem item = HistoryItem.fromString(op.message);
    assertEquals("2 plus 2 equals 4.", item.response);
  }

  @Test
  void testMS2Story3_BDD1() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    APIOperation op = mockClient.sendVoice(null, "id");
    HistoryItem hist = HistoryItem.fromString(op.message);
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);
  }

  @Test
  void testMS2Story3_BDD2() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    APIOperation op = mockClient.sendVoice(null, "id");
    HistoryItem hist = HistoryItem.fromString(op.message);
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);

    op = mockClient.sendVoice(null, "id");
    hist = HistoryItem.fromString(op.message);
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);

    op = mockClient.sendVoice(null, "id");
    hist = HistoryItem.fromString(op.message);
    assertNotNull(hist);
    assertEquals("What is 2 plus 2?", hist.question);
    assertEquals("2 plus 2 equals 4.", hist.response);
  }

  /* MS2 User Story 4 Tests (BDD Scenarios) */
  @Test
  void testAccountHistory() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    APIOperation op = mockClient.sendVoice(null, "id");
    assertTrue(op.success);
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

    APIOperation op = mockClient.sendVoice(null, "id");
    assertTrue(op.success);

    assertEquals("What is 2 plus 2?", mockClient.getHistory().get(0).question);
    assertEquals("2 plus 2 equals 4.", mockClient.getHistory().get(0).response);
  }

  @Test
  void testMS2Story4_BDD3() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());

    APIOperation op = mockClient.sendVoice(null, "id");
    assertTrue(op.success);
    op = mockClient.sendVoice(null, "id");
    assertTrue(op.success);
    op = mockClient.sendVoice(null, "id");
    assertTrue(op.success);

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
