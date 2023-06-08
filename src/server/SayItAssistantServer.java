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

      IDBDriver db = new DBDriver();
      if (!db.ok()) {
        System.err.println("Error: Failed to connect to database.");
        System.exit(2);
      }

      AuthHandler auth = new AuthHandler(db);
      server.createContext("/auth", auth);

      APIHandler api = new APIHandler(new ChatGPT(), new Whisper(), db);
      server.createContext("/api", api);
      
      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
      server.setExecutor(threadPoolExecutor);
      server.start();

      System.out.println("Server started on port " + PORT);
    } catch (Exception e) {
      System.err.println("Error: Failed to start server on port " + PORT);
    }
  }
}
