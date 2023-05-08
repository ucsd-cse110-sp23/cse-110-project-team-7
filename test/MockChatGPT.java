import java.io.File;

/**
 * A mock class emulating the standard ChatGPT
 *   class, for use in testing.
 */
class MockChatGPT {
  int counter = 0;

  String ask(String question) {
    switch (counter++) {
      case 0:
        return "2 plus 2 equals 4.";
      case 1:
        return "My favorite color is blue.";
      default:
        return null;
    }
  }
}
