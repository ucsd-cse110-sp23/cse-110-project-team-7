import java.io.File;

/**
 * A mock class emulating the standard Whisper
 *   class, for use in testing.
 */
class MockWhisper {
  int counter = 0;

  String speechToText(File file) {
    switch (counter++) {
      case 0:
        return "What is 2 plus 2?";
      case 1:
        return "What is your favorite color?";
      default:
        return null;
    }
  }
}
