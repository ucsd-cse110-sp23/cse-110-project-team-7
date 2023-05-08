/**
 * SayItAssistant.java: Program entry point, constructs the UI.
 */
public class SayItAssistant {
  /**
   * Verify that the user's API token is set, then construct
   *   the UI.
   */
  public static void main(String[] args) {
    if (System.getenv("OPENAI_TOKEN") == null) {
      System.err.println("Error: No OpenAI token found.");
      // System.exit(1);
    }
    new AppFrame();
  }
}
