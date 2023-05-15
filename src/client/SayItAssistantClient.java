/**
 * Class containing the program entry point, including
 *   no logic of its own.
 */
public class SayItAssistantClient {
  /**
   * Program entry point, constructs the UI using the
   *   default HTTP-based backend client.
   */
  public static void main(String[] args) {
    new AppFrame(new HttpBackendClient());
  }
}
