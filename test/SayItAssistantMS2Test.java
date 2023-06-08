import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;

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
  void testClientSignup() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());
    assertTrue(mockClient.signup("helen@gmail.com", "password"));
    assertNotNull(mockClient.getToken());
  }

  @Test
  void testDBSignup() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));
    assertEquals(1, users.size());

    assertEquals("token=newemail", db.createUser("newemail", "pass"));
    assertEquals(2, users.size());
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
  void testClientLogin() {
    IBackendClient mockClient = new MockBackendClient();
    assertTrue(mockClient.connected());
    assertFalse(mockClient.login("helen@gmail.com", "password"));
    assertTrue(mockClient.signup("helen@gmail.com", "password"));
    assertTrue(mockClient.login("helen@gmail.com", "password"));
  }

  @Test
  void testDBLogin() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    users.put("email", new Document("_id", "email"));
    assertEquals("token=email", db.loginUser("email", "pass"));

    assertNull(db.loginUser("notreal", "pass"));
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

  /* MS2 User Story 8 Tests (BDD scenarios) */
  @Test
  void testMS2Story8_BDD1() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.setupEmail("Helen", "Doe", "Helen Doe", "helen@gmail.com", 
    "smtp.gmail.com", "587", "emailpassword"));
  }

  @Test
  void testMS2Story8_BDD2() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.setupEmail("Helen", "Doe", "Helen Doe", "helen@gmail.com", 
    "smtp.gmail.com", "587", "emailpassword"));

    try{
      Thread.sleep(1000);
      assertTrue(client.setupEmail("Helen", "Doe", "Helen Doe", "helen@gmail.com", 
    "smtp.gmail.com", "587", "emailpassword"));
    } catch(InterruptedException e) {
      assertTrue(false);
    }
    
  }

  @Test
  void testMS2Story8_BDD3() {
    IBackendClient client = new MockBackendClient();
    assertTrue(client.connected());
    assertTrue(client.setupEmail("Helen", "Doe", "Helen Doe", "helen@gmail.com",
                "smtp.gmail.com", "587", "emailpassword"));
    assertNotNull(client.retrieveEmail());
  }

  /* DBDriver Tests */
  @Test
  void testDBDriverGet() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());
    assertNull(db.getUser("email"));

    users.put("email", new Document());
    assertNotNull(db.getUser("email"));
  }

  @Test
  void testDBDriverUser() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));
    assertEquals(1, users.size());

    assertNotNull(db.loginUser("email", "pass"));
  }

  @Test
  void testDBDriverHistory() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    assertTrue(db.addHistory(user, new HistoryItem("A", "B")));
    assertEquals(1, db.getHistory(user).size());

    assertTrue(db.setHistory(user, new ArrayList<Document>()));
    assertEquals(0, db.getHistory(user).size());
  }

  /* MS2 User Story 9 Tests (BDD Scenarios) */
  @Test
  void testMS2Story9_BDD1() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    HistoryItem email = new HistoryItem(
        "Create an email to Jeff.",
        "Hi Jeff, how are you?"
    );
    email.type = "email";

    assertTrue(db.addHistory(user, email));
    assertEquals(1, db.getHistory(user).size());
  }

  @Test
  void testMS2Story9_BDD2() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    HistoryItem email = new HistoryItem(
        "Create an email to Jeff.",
        "Hi Jeff, how are you?"
    );
    email.type = "email";

    assertTrue(db.addHistory(user, email));
    assertEquals(1, db.getHistory(user).size());

    email = new HistoryItem(
        "Create an email to Jeff.",
        "Hi Jeff, how is the weather?"
    );
    email.type = "email";

    assertTrue(db.addHistory(user, email));
    assertEquals(2, db.getHistory(user).size());
  }

  /* MS2 User Story 10 Tests (BDD Scenarios) */
  @Test
  void testMS2Story10_BDD1() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));
    assertTrue(db.setupEmail(db.getUser("email"), new Document()));
    IMail mail = new MockMail(db.getUser("email"));
    assertTrue(mail.ok());

    assertTrue(mail.send("to@to.com", "subj", "body"));
  }

  @Test
  void testMS2Story10_BDD2() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));
    assertFalse(db.setupEmail(db.getUser("email"), null));

    IMail mail = new MockMail(db.getUser("email"));
    assertFalse(mail.send("to@to.com", "subj", "body"));
  }

  /* MS2 User Story 5 Tests (BDD Scenarios) */
  @Test
  void testMS2Story5_BDD1() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    assertTrue(db.addHistory(user, new HistoryItem("What is the world?", "My oyster.")));
    assertEquals(1, db.getHistory(user).size());

    List<Document> hist = db.getHistory(user);
    hist.remove(0);
    assertTrue(db.setHistory(user, hist));
    assertEquals(0, hist.size());
  }

  @Test
  void testMS2Story5_BDD2() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    assertTrue(db.addHistory(user, new HistoryItem("What is the world?", "My oyster.")));
    assertTrue(db.addHistory(user, new HistoryItem("What is blue?", "A color.")));
    assertTrue(db.addHistory(user, new HistoryItem("What is?", "Not sure.")));
    assertEquals(3, db.getHistory(user).size());

    List<Document> hist = db.getHistory(user);
    hist.remove(0);
    assertTrue(db.setHistory(user, hist));
    assertEquals(2, hist.size());
  }

  @Test
  void testMS2Story5_BDD3() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    List<Document> hist = db.getHistory(user);
    if (hist.size() > 0) {
      hist.remove(0);
    }
    assertTrue(db.setHistory(user, hist));
    assertEquals(0, hist.size());
  }

  /* MS2 User Story 6 Tests (BDD Scenarios) */
  @Test
  void testMS2Story6_BDD1() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    assertTrue(db.addHistory(user, new HistoryItem("What is the world?", "My oyster.")));
    assertTrue(db.addHistory(user, new HistoryItem("What is blue?", "A color.")));
    assertTrue(db.addHistory(user, new HistoryItem("What is?", "Not sure.")));

    List<Document> hist = db.getHistory(user);
    assertEquals(3, hist.size());

    hist.clear();

    assertTrue(db.setHistory(user, hist));
    assertEquals(0, db.getHistory(user).size());
  }

  @Test
  void testMS2Story6_BDD2() {
    Map<String, Document> users = new HashMap<>();
    IDBDriver db = new MockDBDriver(users);
    assertTrue(db.ok());

    assertEquals("token=email", db.createUser("email", "pass"));

    Document user = db.getUser("email");
    assertNotNull(user);
    assertEquals(0, db.getHistory(user).size());

    List<Document> hist = db.getHistory(user);
    assertEquals(0, hist.size());

    hist.clear();

    assertTrue(db.setHistory(user, hist));
    assertEquals(0, db.getHistory(user).size());
  }
}
