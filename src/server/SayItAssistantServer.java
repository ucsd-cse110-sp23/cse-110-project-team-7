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
      // System.exit(1);
    }

    try {
      Storage storage = new Storage();

      // When the program shuts down, save storage to file
      Runtime.getRuntime().addShutdownHook(
        new Thread(() -> storage.save())
      );

      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

      HttpServer server = HttpServer.create(
          new InetSocketAddress(HOST, PORT),
          0
      );
      server.createContext("/prompt", new PromptHandler(storage));
      server.setExecutor(threadPoolExecutor);
      server.start();

      System.out.println("Server started on port " + PORT);
    } catch (Exception e) {
      System.err.println("Error: Failed to start server on port " + PORT);
    }
  }
}
