// WhatsAppParser.java - Parser for WhatsApp conversation exports

package com.ghostwriter.message;

import java.util.*;
import java.util.regex.*;

/**
 * WhatsAppParser parses WhatsApp chat export files
 * 
 * WhatsApp Export Format:
 * [12/10/24, 2:31 PM] Alice: Hey, how are you?
 * [12/10/24, 2:35 PM] Bob: I'm good, thanks!
 */
public class WhatsAppParser implements MessageParser {
    
    // Regex pattern for WhatsApp message format
    // Matches: [date, time] Sender: Message content
    private static final Pattern WHATSAPP_PATTERN = Pattern.compile(
        "\\[(\\d{1,2}/\\d{1,2}/\\d{2,4}),\\s*(\\d{1,2}:\\d{2}(?:\\s*[AP]M)?)\\]\\s*([^:]+):\\s*(.+)"
    );
    
    @Override
    public List<Message> parse(String content) {
        
        if (content == null || content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Message> messages = new ArrayList<>();
        String[] lines = content.split("\n");
        
        System.out.println("📱 Parsing WhatsApp format...");
        
        String currentMessageId = null;
        String currentSender = null;
        String currentTimestamp = null;
        StringBuilder currentContent = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                continue;
            }
            
            // Skip system messages (e.g., "Messages and calls are end-to-end encrypted")
            if (line.contains("end-to-end encrypted") || 
                line.contains("created group") ||
                line.contains("changed the subject")) {
                continue;
            }
            
            Matcher matcher = WHATSAPP_PATTERN.matcher(line);
            
            if (matcher.matches()) {
                // Save previous message if exists
                if (currentMessageId != null) {
                    messages.add(new Message(
                        currentMessageId,
                        currentContent.toString().trim(),
                        currentSender,
                        currentTimestamp
                    ));
                }
                
                // Start new message
                currentMessageId = UUID.randomUUID().toString();
                String date = matcher.group(1);
                String time = matcher.group(2);
                currentSender = matcher.group(3).trim();
                currentContent = new StringBuilder(matcher.group(4).trim());
                currentTimestamp = formatTimestamp(date, time);
                
            } else {
                // Multi-line message - append to current message
                if (currentContent.length() > 0) {
                    currentContent.append(" ").append(line);
                }
            }
        }
        
        // Don't forget the last message!
        if (currentMessageId != null) {
            messages.add(new Message(
                currentMessageId,
                currentContent.toString().trim(),
                currentSender,
                currentTimestamp
            ));
        }
        
        System.out.println("✅ Parsed " + messages.size() + " WhatsApp messages");
        
        return messages;
    }
    
    /**
     * Format timestamp to consistent format
     * Converts WhatsApp format to: yyyy-MM-dd HH:mm:ss
     */
    private String formatTimestamp(String date, String time) {
        try {
            // Convert date format: 12/10/24 -> 2024-12-10
            String[] dateParts = date.split("/");
            String month = String.format("%02d", Integer.parseInt(dateParts[0]));
            String day = String.format("%02d", Integer.parseInt(dateParts[1]));
            String year = dateParts[2];
            
            // Convert 2-digit year to 4-digit
            if (year.length() == 2) {
                int yearNum = Integer.parseInt(year);
                year = (yearNum > 50) ? "19" + year : "20" + year;
            }
            
            // Convert time format: 2:31 PM -> 14:31:00
            time = time.replace(" ", "");
            boolean isPM = time.toUpperCase().contains("PM");
            boolean isAM = time.toUpperCase().contains("AM");
            
            time = time.replaceAll("[APM]", "").trim();
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            
            // Convert to 24-hour format
            if (isPM && hour != 12) {
                hour += 12;
            } else if (isAM && hour == 12) {
                hour = 0;
            }
            
            return String.format("%s-%s-%s %02d:%02d:00", 
                year, month, day, hour, minute);
            
        } catch (Exception e) {
            // Fallback to original if parsing fails
            return date + " " + time;
        }
    }
}
