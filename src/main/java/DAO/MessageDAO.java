package DAO;

import static org.mockito.ArgumentMatchers.nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Model.Message;

public class MessageDAO {
    private static MessageDAO messageDAO = null;

    private List<Message> messages = new ArrayList<Message>();

    public static MessageDAO instance() {
        if (messageDAO == null)
        {
            messageDAO = new MessageDAO();
        }
        return messageDAO;
    }

    public void createMessage(int messageID, int postedBy, String msg_text, long time_posted_epoch) {
        Message m = new Message(messageID, postedBy, msg_text, time_posted_epoch);
        messages.add(m);
    }

    public Stream<Message> getMessageById(int id)
    {
        return messages.stream()
        .filter(m -> m.message_id == id);
    }
    
    public Iterable<Message> getAllMessages()
    {
        return messages.stream()
        .collect(Collectors.toList());
    }

    public Iterable<Message> deleteMessageById(int id)
    {
        return messages.stream()
        .filter(m -> m.message_id != id)
        .collect(Collectors.toList());
    }
    
    public void updateMessageTextById(int id, String str)
    {
        Stream<Message> m = messages.stream()
                    .filter(a -> a.message_id == id);
        Message x = (Message) m;
        x.setMessage_text(str);
        
        
    }

    public List<Message> getAllMessagesByUser(int userID)
    {
        return messages.stream()
        .filter(m -> m.posted_by == userID)
        .collect(Collectors.toList());
    }
}
