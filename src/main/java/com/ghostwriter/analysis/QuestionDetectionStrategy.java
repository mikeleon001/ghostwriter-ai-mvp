// QuestionDetectionStrategy.java - Concrete Strategy for Question Detection

package com.ghostwriter.analysis;

import com.ghostwriter.message.Conversation;
import com.ghostwriter.message.Message;

import java.util.*;

/**
 * Concrete Strategy: Question Detection
 * Identifies pending questions from conversation
 * 
 * Design Pattern: Strategy (Concrete Strategy)
 */
public class QuestionDetectionStrategy implements AnalysisStrategy {
    
    @Override
    public AnalysisResult analyze(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        
        if (messages == null || messages.isEmpty()) {
            return new AnalysisResult(new ArrayList<>(), new ArrayList<>(), 
                                     new ArrayList<>(), new HashMap<>());
        }
        
        System.out.println("❓ QuestionDetectionStrategy: Finding pending questions...");
        
        List<String> questions = findPendingQuestions(messages);
        
        // Return result with only questions populated
        return new AnalysisResult(new ArrayList<>(), new ArrayList<>(), 
                                 questions, new HashMap<>());
    }
    
    /**
     * Find pending questions (sentences ending with ?)
     * 
     * @param messages List of messages
     * @return List of pending questions
     */
    private List<String> findPendingQuestions(List<Message> messages) {
        
        List<String> questions = new ArrayList<>();
        
        for (Message message : messages) {
            String content = message.getContent();
            
            // Find sentences ending with ?
            String[] sentences = content.split("[.!]");
            
            for (String sentence : sentences) {
                if (sentence.trim().endsWith("?")) {
                    String question = sentence.trim();
                    if (!questions.contains(question)) {
                        questions.add("\"" + question + "\" - " + message.getSender());
                    }
                }
            }
        }
        
        System.out.println("   ✓ Found " + questions.size() + " pending questions");
        return questions;
    }
    
    @Override
    public String getStrategyName() {
        return "QuestionDetectionStrategy";
    }
}
