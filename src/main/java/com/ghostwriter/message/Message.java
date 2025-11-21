package com.ghostwriter.message;

public class Message {
    private String messageId;
    private String content;
    private String sender;
    private String timestamp;
    
    public Message(String messageId, String content, String sender, String timestamp) {
        this.messageId = messageId;
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public String getContent() { return content; }
    public String getSender() { return sender; }
    public String getTimestamp() { return timestamp; }
}
