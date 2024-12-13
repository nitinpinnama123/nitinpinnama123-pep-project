package Service;

import java.util.List;

import Model.Message;

public interface MessageService {
    Message createMessage(int posted_by, String message_text);

    Message getMessageById(int message_id);

    List<Message> getAllMessages();

    List<Message> getMessagesByUserId(int account_id);
    
    void deleteMessage(int message_id);

    void updateMessage(int message_id, String newMessageText);

}