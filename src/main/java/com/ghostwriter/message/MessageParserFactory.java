// MessageParserFactory.java - Factory for creating message parsers

package com.ghostwriter.message;

/**
 * MessageParserFactory creates appropriate parser based on file type
 * 
 * Design Pattern: Factory Method Pattern
 * Provides a centralized way to get parser instances
 */
public class MessageParserFactory {
    
    /**
     * Get parser for specified type
     * 
     * @param type Parser type ("whatsapp", "sms", etc.)
     * @return MessageParser instance
     * @throws IllegalArgumentException if type not supported
     */
    public static MessageParser getParser(String type) {
        
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Parser type cannot be null or empty");
        }
        
        String normalizedType = type.toLowerCase().trim();
        
        switch (normalizedType) {
            case "whatsapp":
            case "whats app":
            case "wa":
                return new WhatsAppParser();
                
            // Future parsers can be added here:
            // case "sms":
            //     return new SMSParser();
            // case "telegram":
            //     return new TelegramParser();
            
            default:
                throw new IllegalArgumentException(
                    "Unsupported parser type: " + type + 
                    ". Supported types: whatsapp"
                );
        }
    }
    
    /**
     * Get default parser (WhatsApp)
     * 
     * @return WhatsAppParser instance
     */
    public static MessageParser getDefaultParser() {
        return new WhatsAppParser();
    }
    
    /**
     * Check if parser type is supported
     * 
     * @param type Parser type to check
     * @return true if supported
     */
    public static boolean isSupported(String type) {
        if (type == null || type.trim().isEmpty()) {
            return false;
        }
        
        String normalizedType = type.toLowerCase().trim();
        return normalizedType.equals("whatsapp") || 
               normalizedType.equals("whats app") || 
               normalizedType.equals("wa");
    }
    
    /**
     * Get list of supported parser types
     * 
     * @return Array of supported types
     */
    public static String[] getSupportedTypes() {
        return new String[] { "whatsapp" };
    }
}
