import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A class implementing an HTTP server, handling
 *   all incoming requests to the /prompt endpoint.
 */
class SayItAssistantServer {
  private static final int PORT = 8080;
  private static final String HOST = "localhost";

  public static void main(String[] args) {
    if (System.getenv("OPENAI_TOKEN") == null) {
      System.err.println("Error: No OpenAI token found.");
      System.exit(1);
    }

    try {
      if (args.length > 0 && args[0].equals("--test")) {
        System.out.println("Launching server in test mode.");
      }

      HttpServer server = HttpServer.create(
          new InetSocketAddress(HOST, PORT),
          0
      );

      AuthHandler auth = new AuthHandler();
      if (!auth.ok()) {
        System.err.println("Error: Failed to connect to database.");
        System.exit(2);
      }
      server.createContext("/auth", auth);

      PromptHandler pHandler;
      Prompt prompt = new Prompt();
      if (args.length > 0 && args[0].equals("--test")) {
        pHandler = new PromptHandler(
            new MockChatGPT(), prompt, false
        );
        server.createContext("/type", new TypeHandler(prompt, new MockWhisper()));
      } else {
        pHandler = new PromptHandler(prompt, false);
        server.createContext("/type", new TypeHandler(prompt, new Whisper()));
      }

      if (pHandler == null || !pHandler.ok()) {
        System.err.println("Error: Failed to connect to database.");
        System.exit(3);
      }
      server.createContext("/prompt", pHandler);

      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
      server.setExecutor(threadPoolExecutor);
      server.start();

      System.out.println("Server started on port " + PORT);
    } catch (Exception e) {
      System.err.println("Error: Failed to start server on port " + PORT);
    }
  }
}
