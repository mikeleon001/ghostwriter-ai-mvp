// ActionItemStrategy.java - Concrete Strategy for Action Item Detection

package com.ghostwriter.analysis;

import com.ghostwriter.message.Conversation;
import com.ghostwriter.message.Message;

import java.util.*;

/**
 * Concrete Strategy: Action Item Detection
 * Identifies tasks and action items from conversation
 * 
 * Design Pattern: Strategy (Concrete Strategy)
 */
public class ActionItemStrategy implements AnalysisStrategy {
    
    // Keywords that indicate action items
    private static final Set<String> ACTION_KEYWORDS = new HashSet<>(Arrays.asList(
        "need", "should", "must", "have to", "got to", "remember", "don't forget",
        "make sure", "please", "can you", "could you", "will you", "would you",
        "let's", "we should", "i'll", "i will", "deadline", "by", "before",
        "send", "email", "call", "meet", "schedule", "remind"
    ));
    
    @Override
    public AnalysisResult analyze(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        
        if (messages == null || messages.isEmpty()) {
            return new AnalysisResult(new ArrayList<>(), new ArrayList<>(), 
                                     new ArrayList<>(), new HashMap<>());
        }
        
        System.out.println("📋 ActionItemStrategy: Identifying action items...");
        
        List<String> actionItems = identifyActionItems(messages);
        
        // Return result with only action items populated
        return new AnalysisResult(new ArrayList<>(), actionItems, 
                                 new ArrayList<>(), new HashMap<>());
    }
    
    /**
     * Identify action items from messages
     * Looks for sentences with action keywords
     * 
     * @param messages List of messages
     * @return List of action items
     */
    private List<String> identifyActionItems(List<Message> messages) {
        
        List<String> actionItems = new ArrayList<>();
        
        for (Message message : messages) {
            String content = message.getContent();
            
            // Split into sentences
            String[] sentences = content.split("[.!?]");
            
            for (String sentence : sentences) {
                String lowerSentence = sentence.toLowerCase().trim();
                
                // Check if sentence contains action keywords
                for (String keyword : ACTION_KEYWORDS) {
                    if (lowerSentence.contains(keyword)) {
                        // Clean up and add to action items
                        String cleanSentence = sentence.trim();
                        if (cleanSentence.length() > 10 && !actionItems.contains(cleanSentence)) {
                            actionItems.add("\"" + cleanSentence + "\" - " + message.getSender());
                        }
                        break;
                    }
                }
            }
        }
        
        System.out.println("   ✓ Found " + actionItems.size() + " action items");
        return actionItems;
    }
    
    @Override
    public String getStrategyName() {
        return "ActionItemStrategy";
    }
}
