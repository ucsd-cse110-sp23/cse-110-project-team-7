import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * An interface for a linkage between the
 *   application frontend and backend.
 * This is an example of the Adapter and Strategy
 *   patterns:
 * - Strategy: This interface allows for any concrete
 *     class implementing the IBackendClient interface
 *     to give the client its functionality.
 */
interface IBackendClient {
  public boolean signup(String email, String password);

  public boolean login(String email, String password);

  public String getToken();

  public boolean checkToken(String tok);

  public ArrayList<HistoryItem> getHistory();

  public String questionType(File stream);

  public HistoryItem askQuestion(String question);

  public boolean deleteQuestion(UUID id);

  public boolean addEmailDetails(String firstName, String lastName, String displayName, 
  String email, String smtpHost, String tlsPort, String password);

  public boolean clearHistory();

  public boolean connected();
}
