import Controller.SocialMediaController;
import Service.AccountService;
import io.javalin.Javalin;

/**
 * This class is provided with a main method to allow you to manually run and test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
public class Main {
    public static void main(String[] args) {
        SocialMediaController controller = new SocialMediaController(new AccountService(), new MessageService()) {
            
        };
        Javalin app = controller.startAPI();
        app.start(8080);
    }
}
