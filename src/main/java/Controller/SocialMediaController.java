package Controller;
import static org.mockito.ArgumentMatchers.nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.h2.util.json.JSONArray;

import org.h2.util.json.JSONObject;

import Model.Account;
import Model.Message;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
   
    private List<Account> accounts = new ArrayList<>();

    private List<Message> messages = new ArrayList<>();

    public SocialMediaController() {


    }
    Random rand = new Random();
    public Handler registerUser = ctx -> {
        String request = ctx.body();
      
        Account newAccount = parseAccountFromJson(request);

        if (newAccount.getUsername() == null || newAccount.getUsername().trim().isEmpty())
        {
            ctx.status(400).result("Username cannot be blank");
            return;
        }

        if (newAccount.getPassword().length() < 4)
        {
            ctx.status(400).result("Password has to be at least 4 characters in length");
            return;
        }

        if ((newAccount.getUsername()) != null) {
            ctx.status(400).result("Account with this username already exists");
            return;
        }

        int accountId = rand.nextInt(1000);

        newAccount.setAccount_id(accountId);
    

        accounts.add(newAccount);

        ctx.json(newAccount);
    };

    public Handler loginUser = ctx -> {
    
        String request = ctx.body();
        

        Account a = parseAccountFromJson(request);

        if (a != null) {
            for (Account b : accounts)
            {
                if (b.getUsername().equals(a.getUsername()))
                {
                    ctx.status(200).result("Successful login");
                    break;
                }
            }
            ctx.status(401).result("Login failed");
        }
        else {
            ctx.status(401).result("Login failed");
        }

       




    

    };
// The creation of the message will be successful if and only if the message_text is not blank, is not over 255 characters, and posted_by refers to a real, existing user.
    public Handler createMessage = ctx -> {
        String request = ctx.body();
        Message newMessage = parseMessageFromJson(request);
        Account account = parseAccountFromJson(request);
        if (newMessage.getMessage_text() == null)
        {
            ctx.status(400).result("Message text cannot be blank");
            return;
        }
        if (newMessage.getMessage_text().length() > 255)
        {
            ctx.status(400).result("Message text cannot be over 255 characters");
            return;
        }

        if (newMessage.getPosted_by() != account.getAccount_id())
        {
            ctx.status(400).result("Not a real user");
            return;
        }

        ctx.status(200).result("Message successful");
        ctx.json(newMessage);

        messages.add(newMessage);
    
    



    };

    public Handler getAllMessages = ctx -> {
        String request = ctx.body();
        
        ctx.status(200).result("Message received");
        for (Message m : messages)
        {
            ctx.json(m);
        }

        

    };

    public Handler getMessageById = ctx -> {

        String request = ctx.body();
        Message parsedRequest = parseMessageFromJson(request);
        ctx.status(200).result("Message received");
        for (Message m : messages)
        {
            m = parseMessageFromJson(request);
            if (m.getMessage_id() == parsedRequest.getMessage_id())
            {
                ctx.json(m);
            }

        }


    };

    public Handler deleteMessageById = ctx -> {
        String request = ctx.body();
        Message parsedRequest = parseMessageFromJson(request);
        ctx.status(200).result("Message deleted");
        for (Message m : messages)
        {
            m = parseMessageFromJson(request);
            if (m.message_id == parsedRequest.message_id)
            {
                ctx.json(m);
            }
            else {
                // Response body is empty
            }
        }

    };

    public Handler updateMessageTextById = ctx -> {
        String request = ctx.body();
        Message parsedRequest = parseMessageFromJson(request);

        for (Message m : messages)
        {
            m = parseMessageFromJson(request);
            if (m.getMessage_id() == parsedRequest.getMessage_id())
            {
                if (parsedRequest.getMessage_text() == null || parsedRequest.getMessage_text().length() > 255)
                {
                    ctx.status(400).result("Message text cannot be blank");
                    return;
                }
                else {
                    m.setMessage_text(parsedRequest.getMessage_text());
                    ctx.status(200).result("Message text successfully updated");
                    ctx.json(m);
                }
            }

        }




    };

    public Handler getAllMessagesByUser = ctx -> {
        List<Message> messagesByUser = new ArrayList<Message>();
        String request = ctx.body();
        Account parsedRequestAccount = parseAccountFromJson(request);

        ctx.status(200).result("Messages by user received");
        for (Message m : messages)
        {
            m = parseMessageFromJson(request);
            if (m.getPosted_by() == parsedRequestAccount.getAccount_id())
            {
                messagesByUser.add(m);
            }
        }
        ctx.json(messagesByUser);



    };

    private Account parseAccountFromJson(String json) throws JsonMappingException, JsonProcessingException {
        // Parse the JSON and return an Account object
        // You can use a library like Gson or Jackson for JSON parsing
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        Account a = new Account(node.get("account_id").asInt(), node.get("username").asText(), node.get("password").asText());
        return a;
    }

    private Message parseMessageFromJson (String json) throws JsonMappingException, JsonProcessingException 
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        Message m = new Message(node.get("posted_by").asInt(), node.get("message_text").asText(), node.get("time_posted_epoch").asLong());
        return m;
    }

    

    
   
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("localhost:8080/register", new SocialMediaController().registerUser);
        app.post("localhost:8080/login", new SocialMediaController().loginUser);
        app.post("localhost:8080/messages", new SocialMediaController().createMessage);
        app.get("localhost:8080/messages", new SocialMediaController().getAllMessages);
        app.get("localhost:8080/messages/{message_id}", new SocialMediaController().getMessageById);
        app.delete("localhost:8080/messages/{message_id}", new SocialMediaController().deleteMessageById);
        app.patch("localhost:8080/messages/{message_id}", new SocialMediaController().updateMessageTextById);
        app.get("localhost:8080/accounts/{account_id}", new SocialMediaController().getAllMessagesByUser);
        return app;
    }




    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    /*private void exampleHandler(Context context) {
        context.json("sample text");
    }*/


}