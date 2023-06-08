import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A common class representing a response sent
 *   by the API to the client.
 */
class APIOperation {
  String command;
  String args;
  String message;
  boolean success;

  public static List<String> commands = Arrays.asList(
      "history",
      "question",
      "delete",
      "clear",
      "create",
      "send",
      "set up",
      "setup"
  );

  APIOperation() {
    command = null;
    message = null;
    success = false;
  }

  APIOperation(String c, String m, boolean s) {
    command = c;
    message = m;
    success = s;
  }

  boolean valid() {
    if (command == null) {
      return false;
    }

    for (String x : commands) {
      if (command.equals(x)) {
        return true;
      }
    }
    return false;
  }

  boolean parseVoice(String voice) {
    String lower = voice.toLowerCase();
    for (String x : commands) {
      if (lower.startsWith(x)) {
        command = x;
        args = voice.substring(x.length() + 1).trim();
        if (args.length() > 0) {
          args = args.substring(0, 1).toUpperCase() + args.substring(1);
        }
        return true;
      }
    }
    return false;
  }

  boolean parseJSON(String json) {
    try {
      JSONTokener tok = new JSONTokener(json);
      JSONObject obj = new JSONObject(tok);

      command = obj.getString("command");
      success = obj.getBoolean("success");
      message = obj.getString("message");
    } catch (Exception e) {
      command = null;
      message = null;
      success = false;
    }
    return valid();
  }

  String serialize() {
    JSONObject obj = new JSONObject();
    obj.put("command", command);
    obj.put("message", message);
    obj.put("success", success);
    return obj.toString();
  }
}
