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
      Storage storage;
      if (args.length > 0 && args[0].equals("--test")) {
        storage = new Storage("[]");
      } else {
        storage = new Storage();

        // When the program shuts down, save storage to file
        Runtime.getRuntime().addShutdownHook(
          new Thread(() -> storage.save())
        );
      }

      HttpServer server = HttpServer.create(
          new InetSocketAddress(HOST, PORT),
          0
      );

      Prompt prompt = new Prompt();
      if (args.length > 0 && args[0].equals("--test")) {
        server.createContext("/prompt", new PromptHandler(
            storage, new MockChatGPT(), prompt)
        );
      } else {
        server.createContext("/prompt", new PromptHandler(storage, prompt));
      }

      server.createContext("/type", new TypeHandler(prompt, new Whisper()));

      AuthHandler auth = new AuthHandler();
      if (!auth.ok()) {
        System.err.println("Error: Failed to connect to database.");
        System.exit(2);
      }
      server.createContext("/auth", auth);

      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
      server.setExecutor(threadPoolExecutor);
      server.start();

      System.out.println("Server started on port " + PORT);
    } catch (Exception e) {
      System.err.println("Error: Failed to start server on port " + PORT);
    }
  }
}
