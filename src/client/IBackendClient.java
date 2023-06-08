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

  public boolean setupEmail(
      String first,
      String last,
      String display,
      String email,
      String smtp,
      String tls,
      String pass
  );

  public boolean connected();

  public boolean checkToken(String tok);

  public String getToken();

  public String[] retrieveEmail();

  public ArrayList<HistoryItem> getHistory();

  public APIOperation sendVoice(File stream, String id);
}
