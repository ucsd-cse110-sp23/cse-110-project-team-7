import java.io.File;

/**
 * A mock class emulating the standard Whisper
 *   class, for use in testing.
 */
class MockWhisper implements IWhisper {
  int counter = 0;

  public String speechToText(File file) {
    switch (counter++) {
      case 0:
        return "Question. What is 2 plus 2?";
      case 1:
        return "Question. What is your favorite color?";
      default:
        return null;
    }
  }
}
