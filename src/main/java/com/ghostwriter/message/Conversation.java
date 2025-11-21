package com.ghostwriter.message;

import java.util.*;

public class Conversation {
    private String conversationId;
    private String userId;
    private String date;
    private List<Message> messages = new ArrayList<>();
    
    public static Conversation create(String userId, String date) {
        return new Conversation(UUID.randomUUID().toString(), userId, date);
    }
    
    public static Conversation fromDatabase(Map<String, String> data) {
        return new Conversation(data.get("conversation_id"), 
                               data.get("user_id"), 
                               data.get("date"));
    }
    
    private Conversation(String id, String userId, String date) {
        this.conversationId = id;
        this.userId = userId;
        this.date = date;
    }
    
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public String getConversationId() { return conversationId; }
}
