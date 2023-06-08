import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;


/**
 * A class to represent the management of documents
 * pertaining to a users email information
 */
class EmailHandler implements HttpHandler {
    private MongoCollection<Document> users;

    EmailHandler() {
        try {
            MongoClient client = MongoClients.create(System.getenv("MONGO_URI"));
            MongoDatabase database = client.getDatabase("users");
            users = database.getCollection("users");
        } catch (Exception e) {
            e.printStackTrace();
            users = null;
        }
    }

    public boolean ok() {
        return (users != null);
    }

    /**
     * Handle incoming request methods
     */
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        int code = 200;
        String response;

        String uri = exchange.getRequestURI().toString();
        String[] params = uri.split("/");
        String token = (params.length >= 3) ? params[2] : null;
        String firstName = (params.length >= 4) ? params[3] : null;
        String lastName = (params.length >= 5) ? params[4] : null;
        String displayName = (params.length >= 6) ? params[5] : null;
        String email = (params.length >= 7) ? params[6] : null;
        String smtpHost = (params.length >= 8) ? params[7] : null;
        String tlsPort = (params.length >= 9) ? params[8] : null;
        String password = (params.length >= 10) ? params[9] : null;

        try{
            Document user = users.find(eq("_id", new ObjectId(token))).first();
            if (user == null) {
                throw new Exception("User not found");
            }
            switch (method) {
                case "POST":
                    response = handlePost(user, 
                        firstName, 
                        lastName, 
                        displayName, 
                        email,
                        smtpHost,
                        tlsPort,
                        password);
                    break;
                default:
                    response = null;
                    break;
            
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = null;
        }

        if (response == null) {
            code = 400;
            response = "Error: Request not supported";
        }

        exchange.sendResponseHeaders(code, response.length());

        OutputStream outStream = exchange.getResponseBody();
        outStream.write(response.getBytes());
        outStream.close();
    }

    /**
     * Method to add email information values to a user
     * document
     */
    String handlePost(Document user, String firstName, String lastName, String displayName, String email,
            String smtpHost, String tlsPort, String password) {
        try{
            user.replace("firstName", firstName);
            user.replace("lastName", lastName); 
            user.replace("displayName", displayName);
            user.replace("email", email);
            user.replace("smtpHost", smtpHost);
            user.replace("tlsPort", tlsPort);
            user.replace("password", password);
            return "Success Updating Email.";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
