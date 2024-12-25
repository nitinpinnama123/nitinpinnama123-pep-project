package Controller;
// import static org.mockito.ArgumentMatchers.nullable;

import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.ArgumentMatchers.nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
//import java.beans.Statement;
//import java.io.*;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import DAO.AccountDAO;
import DAO.MessageDAO;

import org.h2.engine.Database;
import org.h2.util.json.JSONArray;

import org.h2.util.json.JSONObject;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;
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

    public static Handler registerUser = ctx -> {
        
        Account accCreated = null;
        
        Account acc = ctx.bodyAsClass(Account.class);
        System.out.println("Account user = " + acc.getUsername() + " password = " + acc.getPassword());

        AccountDAO accountDAO = AccountDAO.instance();
        if (acc.getUsername() == null || acc.getUsername().length() == 0) {
            ctx.status(400);
        } else if (acc.getPassword() != null && acc.getPassword().length() < 4) {
            ctx.status(400);
        } else if (accountDAO.getAccountByUsername(acc.getUsername()) == null) {
            accCreated = accountDAO.registerUser(acc.getUsername(), acc.getPassword());
            System.out.println("Inserted given accout" + accCreated);

            ctx.status(200).json(accCreated);
        }
        else {
            ctx.status(400);
        }
    };

    public static Handler getAccountById = ctx -> {
 
        System.out.println("Context = " + ctx.body());


        AccountDAO dao = AccountDAO.instance();

        Account accountToGet = dao.getAccountById(0);
        System.out.println(accountToGet);
    };

    public static Handler loginUser = ctx -> {
        Account acc = ctx.bodyAsClass(Account.class);
        Account accFetched = null;
        AccountDAO accountDAO = AccountDAO.instance();
        if (acc.getUsername() == null || acc.getUsername().length() == 0 
              || acc.getPassword() == null || acc.getPassword().length() == 0)
        {
            ctx.status(401);
        }
        else
        {
            accFetched = accountDAO.getAccountByUsername(acc.getUsername());
            if (accFetched != null)
            {
                if (acc.getPassword().equals(accFetched.getPassword()))
                {
                    ctx.status(200).json(accFetched);
                }
                else {
                    // Passwords don't match, return 401
                    ctx.status(401);
                }
            }
            else {
                // Account does not exist with username provided, return 401
                ctx.status(401);
            }
        }

    };


// The creation of the message will be successful if and only if the message_text is not blank, is not over 255 characters, and posted_by refers to a real, existing user.
    public static Handler createMessage = ctx -> {

        System.out.println("Context = " + ctx.body());
        
        Message m = ctx.bodyAsClass(Message.class);
        System.out.println("Message id = " + m.getMessage_id() + " posted_by = " + m.getPosted_by());

        MessageDAO messageDAO = MessageDAO.instance();
        AccountDAO accountDAO = AccountDAO.instance();
        System.out.println("Calling Message DAO to create message");
        if (m.getMessage_text() == null || m.getMessage_text().length() == 0 || 
            m.getMessage_text().length() > 255) {
            ctx.status(400);
        }
        else if (accountDAO.getAccountById(m.getPosted_by()) == null)
        {
            // User with given id does not exist, return 400
            ctx.status(400);
        } else {
            Message mAdded = messageDAO.createMessage(m.getPosted_by(), m.getMessage_text(), m.getTime_posted_epoch());
            System.out.println("Created given message");
            System.out.println("Message = " + mAdded);
            ctx.status(200).json(mAdded);
        }

    };

    public static Handler getAllMessages = ctx -> {
        
        /*ctx.status(200).result("All Messages received");
        MessageDAO messageDAO = MessageDAO.instance();

        Iterable<Message> allMessages = messageDAO.getAllMessages();
        ctx.json(allMessages);*/

        System.out.println("Context = " + ctx.body());


        MessageDAO msgDAO = MessageDAO.instance();

        List<Message> messages = msgDAO.getAllMessages();
        System.out.println(messages);

        ctx.status(200).json(messages);

    };

    public static Handler getMessageById = ctx -> {

    
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));

        MessageDAO msgDAO = MessageDAO.instance();

        Message messageToGet = msgDAO.getMessageById(id);

        if (messageToGet != null)
        {
            ctx.status(200).json(messageToGet);
        }
        else {
            ctx.status(200);
            //.result("Message not found");
            //ctx.html("Not found");
        }


    };

    public static Handler deleteMessageById = ctx -> {
        /*int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        MessageDAO dao = MessageDAO.instance();
        dao.deleteMessageById(id);

        ctx.status(200).result("Message deleted");*/

        Message mDeleted = null;

        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));

        MessageDAO msgDAO = MessageDAO.instance();

        System.out.println("Deleting message with id = " + id);

        mDeleted = msgDAO.deleteMessageById(id);

        if (mDeleted == null)
        {
            ctx.status(200);
        }
        else {
            System.out.println("Deleted message with id = " + mDeleted);
            ctx.status(200).json(mDeleted);
        }

 
    };

    public static Handler updateMessageTextById = ctx -> {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));

        Message m = ctx.bodyAsClass(Message.class);

        MessageDAO messageDAO = MessageDAO.instance();

        if(messageDAO.getMessageById(id) != null)
        {
            if (m.getMessage_text() == null || m.getMessage_text().length() == 0 || 
                m.getMessage_text().length() > 255)
            {
                // Message text is empty or > 255 characters, return 400
                ctx.status(400);
            }
            else {
                Message mReturn = messageDAO.updateMessageTextById(id, m.getMessage_text());
                ctx.status(200).json(mReturn);
            }

        } else {
            // Message with given id does not exist, return 400
            ctx.status(400);
        }


    };

    public static Handler getAllMessagesByUser = ctx -> {
        int userID = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("account_id")));



        /*int userID = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("posted_by")));
        MessageDAO dao = MessageDAO.instance();
        List<Message> messagesByUser = dao.getAllMessagesByUser(userID);
        
        ctx.json(messagesByUser);
        ctx.status(200).result("All messages by user received");*/

        MessageDAO msgDAO = MessageDAO.instance();

        List<Message> messages = msgDAO.getAllMessagesByUser(userID);
        ctx.status(200).json(messages);
    };


    

    
   
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create()
   
    ;

        /*(app.before("/path/*",ctx -> {

        });*/
        //ConnectionUtil.resetTestDatabase();
        app.post("/register", SocialMediaController.registerUser);
        //app.get("localhost:8080/accounts/{account_id}", new SocialMediaController().getAccountById);
        app.post("/login", SocialMediaController.loginUser);
        app.post("/messages", SocialMediaController.createMessage);
        app.get("/messages",  SocialMediaController.getAllMessages);
        app.get("/messages/{message_id}", SocialMediaController.getMessageById);
        app.delete("/messages/{message_id}", SocialMediaController.deleteMessageById);
        app.patch("/messages/{message_id}", SocialMediaController.updateMessageTextById);
        app.get("/accounts/{account_id}/messages", SocialMediaController.getAllMessagesByUser);
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