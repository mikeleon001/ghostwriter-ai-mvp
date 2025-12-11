package com.ghostwriter.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Message class represents a single message in a conversation.
 * 
 * Design Pattern: BUILDER PATTERN
 * Purpose: Simplify object construction with many optional parameters.
 * Usage:
 *   Message msg = new Message.Builder(content, sender)
 *       .messageId(id)
 *       .timestamp(timestamp)
 *       .build();
 *
 * @author Mihail Chitorog
 * @version 2.0
 */
public class Message {
    private String messageId;
    private String content;
    private String sender;
    private String timestamp;

    // ============================================
    // BUILDER PATTERN IMPLEMENTATION
    // ============================================

    /**
     * Builder class for constructing Message objects.
     * Implements the Builder design pattern to handle multiple optional parameters.
     */
    public static class Builder {
        // Required parameters
        private final String content;
        private final String sender;

        // Optional parameters with defaults
        private String messageId = UUID.randomUUID().toString();
        private String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        /**
         * Create a Builder with required parameters.
         *
         * @param content The message content (required)
         * @param sender The message sender (required)
         */
        public Builder(String content, String sender) {
            this.content = content;
            this.sender = sender;
        }

        /**
         * Set custom message ID (optional).
         */
        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        /**
         * Set timestamp (optional, defaults to now).
         */
        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Build the Message object.
         *
         * @return A new Message instance
         */
        public Message build() {
            return new Message(this);
        }
    }

    /**
     * Private constructor used by Builder.
     */
    private Message(Builder builder) {
        this.messageId = builder.messageId;
        this.content = builder.content;
        this.sender = builder.sender;
        this.timestamp = builder.timestamp;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public String getContent() { return content; }
    public String getSender() { return sender; }
    public String getTimestamp() { return timestamp; }
}
