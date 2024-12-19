package Controller;
import static org.mockito.ArgumentMatchers.nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import java.io.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import DAO.MessageDAO;

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
        String username = Objects.requireNonNull(ctx.pathParam("username"));
        String password = Objects.requireNonNull(ctx.pathParam("password"));
        AccountDAO accountDAO = AccountDAO.instance();
        Account acc = accountDAO.registerUser(username, password);
        
        if (acc.username != null && acc.password.length() >= 4)
        {
            ctx.json(acc.username);
            ctx.json(acc.password);
            ctx.status(200).result("Username and password valid");
        }
        else {
            ctx.status(400).result("Username or password invalid");
        }
    };

    public Handler loginUser = ctx -> {
        AccountDAO accountDAO = AccountDAO.instance();
        accountDAO.loginUser(accountDAO);

        ctx.status(200).result("Successful login"); 

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
        
        ctx.status(200).result("All Messages received");
        MessageDAO messageDAO = MessageDAO.instance();

        Iterable<Message> allMessages = messageDAO.getAllMessages();
        ctx.json(allMessages);

        

    };

    public Handler getMessageById = ctx -> {

        /*String request = ctx.body();
        Message parsedRequest = parseMessageFromJson(request);
        ctx.status(200).result("Message received");
        for (Message m : messages)
        {
            m = parseMessageFromJson(request);
            if (m.getMessage_id() == parsedRequest.getMessage_id())
            {
                ctx.json(m);
            }

        }*/

        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        MessageDAO dao = MessageDAO.instance();
        Stream<Message> message = dao.getMessageById(id);

        if (message.toArray().length > 0)
        {
            ctx.json(message);
            ctx.status(200).result("Message received");
        }
        else {
            ctx.status(200).result("Message not found");
            ctx.html("Not found");
        }


    };

    public Handler deleteMessageById = ctx -> {
        /*String request = ctx.body();
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
        }*/

        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        MessageDAO dao = MessageDAO.instance();
        dao.deleteMessageById(id);

        ctx.status(200).result("Message deleted");

    };

    public Handler updateMessageTextById = ctx -> {
        /*String request = ctx.body();
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

        }*/

        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        String str = Objects.requireNonNull(ctx.pathParam("str"));
        MessageDAO dao = MessageDAO.instance();
        Stream<Message> m = dao.getMessageById(id);
        Message x = (Message) m;
        String originalText = x.getMessage_text();
        int originalId = id;
        dao.updateMessageTextById(x.getMessage_id(), str);
        String updatedText = x.getMessage_text();
        int updatedId = x.getMessage_id();

        if (updatedId == originalId && updatedText.length() > 0 
        && updatedText.length() <= 255)
        {
            ctx.json(x);
            ctx.status(200).result("Message text successfully updated");
        }
        else {
            ctx.status(400).result("Message text could not be updated");
        }
    };

    public Handler getAllMessagesByUser = ctx -> {
        

        int userID = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("posted_by")));
        MessageDAO dao = MessageDAO.instance();
        List<Message> messagesByUser = dao.getAllMessagesByUser(userID);
        
        ctx.json(messagesByUser);
        ctx.status(200).result("All messages by user received");



    };


    

    
   
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.before("/path/*",ctx -> {

        });
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