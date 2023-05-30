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
  public ArrayList<HistoryItem> getHistory();

  public HistoryItem askQuestion(File stream);

  public boolean deleteQuestion(UUID id);

  public boolean clearHistory();

  public boolean connected();
}
