import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class for interacting with and retrieving data from
 *   OpenAI's DaVinci model.
 */
class ChatGPT {
  private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
  private static final String MODEL = "text-davinci-003";
  private static final int MAX_TOKENS = 100;

  /**
   * Ask ChatGPT a question via a POST request
   *   to its API endpoint.
   *
   * Source: CSE 110 Lab 4
   */
  public static String ask(String question) {
    try {
      if (question == null) {
        return null;
      }

      JSONObject requestBody = new JSONObject();
      requestBody.put("model", MODEL);
      requestBody.put("prompt", question);
      requestBody.put("max_tokens", MAX_TOKENS);
      requestBody.put("temperature", 1.0);

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest
          .newBuilder()
          .uri(new URI(API_ENDPOINT))
          .header("Content-Type", "application/json")
          .header("Authorization", String.format("Bearer %s", System.getenv("OPENAI_TOKEN")))
          .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
          .build();

      HttpResponse<String> response = client.send(
          request,
          HttpResponse.BodyHandlers.ofString()
      );

      String responseBody = response.body();
      JSONObject responseJson = new JSONObject(responseBody);

      JSONArray choices = responseJson.getJSONArray("choices");
      String generatedText = choices.getJSONObject(0).getString("text");

      return generatedText;
    } catch (Exception e) {
      System.err.println(e.toString());
      return null;
    }
  }
}
