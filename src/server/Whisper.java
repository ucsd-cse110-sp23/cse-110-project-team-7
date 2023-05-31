import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.json.JSONObject;

/**
 * A class with static methods designed to interact
 *   with OpenAI's Whisper API for speech-to-text
 *   conversion.
 */
public class Whisper implements IWhisper {
  private static final String API_ENDPOINT = "https://api.openai.com/v1/audio/transcriptions";
  private static final String MODEL = "whisper-1";

  /* Source: CSE 110 Lab 4 */
  private static void writeParameterToOutputStream(
      OutputStream outputStream,
      String parameterName,
      String parameterValue,
      String boundary
  ) {
    try {
      outputStream.write(("--" + boundary + "\r\n").getBytes());
      outputStream.write(
          ("Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n").getBytes()
      );
      outputStream.write((parameterValue + "\r\n").getBytes());
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  /* Source: CSE 110 Lab 4 */
  private static void writeFileToOutputStream(
      OutputStream outputStream,
      File file,
      String boundary
  ) {
    try {
      outputStream.write(("--" + boundary + "\r\n").getBytes());
      outputStream.write(
          ("Content-Disposition: form-data; name=\"file\"; filename=\""
          + file.getName() + "\"\r\n").getBytes()
      );
      outputStream.write(("Content-Type: audio/mpeg\r\n\r\n").getBytes());

      FileInputStream fileInputStream = new FileInputStream(file);
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = fileInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      fileInputStream.close();
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }

  /* Source: CSE 110 Lab 4 */
  private static String handleSuccessResponse(HttpURLConnection connection) {
    try {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(connection.getInputStream())
      );
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      connection.disconnect();

      JSONObject responseJson = new JSONObject(response.toString());
      return responseJson.getString("text");
    } catch (Exception e) {
      System.err.println(e.toString());
      return null;
    }
  }

  /* Source: CSE 110 Lab 4 */
  private static String handleErrorResponse(HttpURLConnection connection) {
    try {
      BufferedReader errorReader = new BufferedReader(
          new InputStreamReader(connection.getErrorStream())
      );
      String errorLine;
      StringBuilder errorResponse = new StringBuilder();
      while ((errorLine = errorReader.readLine()) != null) {
        errorResponse.append(errorLine);
      }
      errorReader.close();
      connection.disconnect();
      String errorResult = errorResponse.toString();
      System.err.println("Error: " + errorResult);
      return null;
    } catch (Exception e) {
      System.err.println(e.toString());
      return null;
    }
  }

  /**
   * Given an input file, invoke the OpenAI Whisper API for
   *   speech-to-text conversion.
   * Source: CSE 110 Lab 4
   */
  public String speechToText(File file) {
    try {
      if (!file.exists()) {
        return null;
      }

      URL url = new URI(API_ENDPOINT).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);

      String boundary = "Boundary-" + System.currentTimeMillis();
      connection.setRequestProperty(
          "Content-Type",
          "multipart/form-data; boundary=" + boundary
      );
      connection.setRequestProperty("Authorization", "Bearer " + System.getenv("OPENAI_TOKEN"));

      OutputStream outputStream = connection.getOutputStream();

      writeParameterToOutputStream(outputStream, "model", MODEL, boundary);
      writeFileToOutputStream(outputStream, file, boundary);

      outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
      outputStream.flush();
      outputStream.close();

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        return handleSuccessResponse(connection);
      } else {
        return handleErrorResponse(connection);
      }
    } catch (Exception e) {
      System.err.println(e.toString());
      return null;
    }
  }
}
