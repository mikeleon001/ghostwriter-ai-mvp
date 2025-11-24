// StatisticsStrategy.java - Concrete Strategy for Statistics Calculation

package com.ghostwriter.analysis;

import com.ghostwriter.message.Conversation;
import com.ghostwriter.message.Message;

import java.util.*;

/**
 * Concrete Strategy: Statistics Calculation
 * Calculates conversation statistics and metrics
 * 
 * Design Pattern: Strategy (Concrete Strategy)
 */
public class StatisticsStrategy implements AnalysisStrategy {
    
    @Override
    public AnalysisResult analyze(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        
        if (messages == null || messages.isEmpty()) {
            return new AnalysisResult(new ArrayList<>(), new ArrayList<>(), 
                                     new ArrayList<>(), new HashMap<>());
        }
        
        System.out.println("📊 StatisticsStrategy: Calculating statistics...");
        
        Map<String, Object> statistics = calculateStatistics(conversation);
        
        // Return result with only statistics populated
        return new AnalysisResult(new ArrayList<>(), new ArrayList<>(), 
                                 new ArrayList<>(), statistics);
    }
    
    /**
     * Calculate conversation statistics
     * 
     * @param conversation Conversation object
     * @return Map of statistics
     */
    private Map<String, Object> calculateStatistics(Conversation conversation) {
        
        List<Message> messages = conversation.getMessages();
        Map<String, Object> stats = new HashMap<>();
        
        // Total message count
        stats.put("total_messages", messages.size());
        
        // Count messages per sender
        Map<String, Integer> senderCounts = new HashMap<>();
        for (Message message : messages) {
            String sender = message.getSender();
            senderCounts.put(sender, senderCounts.getOrDefault(sender, 0) + 1);
        }
        stats.put("sender_breakdown", senderCounts);
        
        // Find most active sender
        String mostActive = senderCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        stats.put("most_active", mostActive);
        
        // Time range (first and last message)
        if (!messages.isEmpty()) {
            stats.put("first_message", messages.get(0).getTimestamp());
            stats.put("last_message", messages.get(messages.size() - 1).getTimestamp());
        }
        
        // Calculate average message length
        double avgLength = messages.stream()
            .mapToInt(m -> m.getContent().length())
            .average()
            .orElse(0);
        stats.put("avg_message_length", Math.round(avgLength));
        
        System.out.println("   ✓ Calculated " + stats.size() + " statistics");
        return stats;
    }
    
    @Override
    public String getStrategyName() {
        return "StatisticsStrategy";
    }
}
