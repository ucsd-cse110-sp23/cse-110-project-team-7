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

      APIHandler api = new APIHandler(new ChatGPT(), new Whisper());
      if (api == null || !api.ok()) {
        System.err.println("Error: Failed to connect to database.");
        System.exit(3);
      }
      server.createContext("/api", api);

      EmailHandler eHandler = new EmailHandler();
      if (!eHandler.ok()) {
        System.err.println("Error: Failed to connect to database.");
        System.exit(2);
      }
      server.createContext("/email", eHandler);
      
      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
      server.setExecutor(threadPoolExecutor);
      server.start();

      System.out.println("Server started on port " + PORT);
    } catch (Exception e) {
      System.err.println("Error: Failed to start server on port " + PORT);
    }
  }
}
