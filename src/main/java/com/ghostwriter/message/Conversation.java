package com.ghostwriter.message;

import com.ghostwriter.database.Database;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Conversation class represents a collection of messages
 * Now includes database persistence
 */
public class Conversation {
    private String conversationId;
    private String userId;
    private String date;
    private List<Message> messages = new ArrayList<>();
    
    /**
     * Create a new conversation
     */
    public static Conversation create(String userId, String date) {
        return new Conversation(UUID.randomUUID().toString(), userId, date);
    }
    
    /**
     * Load conversation from database
     */
    public static Conversation fromDatabase(Map<String, String> data) {
        Conversation conv = new Conversation(
            data.get("conversation_id"), 
            data.get("user_id"), 
            data.get("date")
        );
        
        // Load messages for this conversation
        conv.loadMessages();
        
        return conv;
    }
    
    /**
     * Private constructor
     */
    private Conversation(String id, String userId, String date) {
        this.conversationId = id;
        this.userId = userId;
        this.date = date;
    }
    
    /**
     * Add message to conversation
     */
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    /**
     * Save conversation and all messages to database
     */
    public void save() {
        Database db = Database.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Save conversation record
        Map<String, String> convData = new HashMap<>();
        convData.put("conversation_id", conversationId);
        convData.put("user_id", userId);
        convData.put("date", date);
        convData.put("message_count", String.valueOf(messages.size()));
        convData.put("created_at", sdf.format(new Date()));
        
        db.store("conversations", convData);
        
        // Save all messages
        for (Message message : messages) {
            Map<String, String> msgData = new HashMap<>();
            msgData.put("message_id", message.getMessageId());
            msgData.put("conversation_id", conversationId);
            msgData.put("sender", message.getSender());
            msgData.put("content", message.getContent());
            msgData.put("timestamp", message.getTimestamp());
            msgData.put("created_at", sdf.format(new Date()));
            
            db.store("messages", msgData);
        }
        
        System.out.println("💾 Saved conversation with " + messages.size() + " messages");
    }
    
    /**
     * Load messages from database for this conversation
     */
    private void loadMessages() {
        Database db = Database.getInstance();
        
        String sql = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY timestamp";
        List<Map<String, String>> results = db.query(sql, conversationId);
        
        messages.clear();
        for (Map<String, String> data : results) {
            Message msg = new Message(
                data.get("message_id"),
                data.get("content"),
                data.get("sender"),
                data.get("timestamp")
            );
            messages.add(msg);
        }
    }
    
    /**
     * Get all messages in this conversation
     */
    public List<Message> getMessages() {
        return messages;
    }
    
    /**
     * Get conversation ID
     */
    public String getConversationId() { 
        return conversationId; 
    }
    
    /**
     * Get user ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Get date
     */
    public String getDate() {
        return date;
    }
}
