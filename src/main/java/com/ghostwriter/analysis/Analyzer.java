// Analyzer.java - Text Analysis and Topic Extraction

package com.ghostwriter.analysis;

import com.ghostwriter.message.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analyzer class handles conversation analysis
 * Extracts key topics, identifies action items, calculates statistics
 */
public class Analyzer {
    
    // Common words to ignore in topic extraction
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for",
        "of", "with", "by", "from", "up", "about", "into", "through", "during",
        "is", "am", "are", "was", "were", "be", "been", "being", "have", "has",
        "had", "do", "does", "did", "will", "would", "should", "could", "may",
        "might", "must", "can", "i", "you", "he", "she", "it", "we", "they",
        "me", "him", "her", "us", "them", "my", "your", "his", "its", "our",
        "this", "that", "these", "those", "what", "which", "who", "when",
        "where", "why", "how", "all", "each", "every", "both", "few", "more",
        "most", "some", "such", "no", "not", "only", "own", "same", "so",
        "than", "too", "very", "just", "yeah", "ok", "okay", "yes", "no"
    ));
    
    // Keywords that indicate action items
    private static final Set<String> ACTION_KEYWORDS = new HashSet<>(Arrays.asList(
        "need", "should", "must", "have to", "got to", "remember", "don't forget",
        "make sure", "please", "can you", "could you", "will you", "would you",
        "let's", "we should", "i'll", "i will", "deadline", "by", "before",
        "send", "email", "call", "meet", "schedule", "remind"
    ));
    
    /**
     * Analyze conversation and extract insights
     * 
     * @param conversation Conversation to analyze
     * @return AnalysisResult with topics, action items, statistics
     */
    public AnalysisResult analyzeConversation(Conversation conversation) {
        
        List<Message> messages = conversation.getMessages();
        
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Cannot analyze empty conversation");
        }
        
        System.out.println("🔍 Analyzing " + messages.size() + " messages...");
        
        // Extract key topics
        List<String> topics = extractKeyTopics(messages);
        
        // Identify action items
        List<String> actionItems = identifyActionItems(messages);
        
        // Find pending questions
        List<String> questions = findPendingQuestions(messages);
        
        // Calculate statistics
        Map<String, Object> statistics = calculateStatistics(conversation);
        
        System.out.println("✅ Analysis complete!");
        System.out.println("   Topics found: " + topics.size());
        System.out.println("   Action items: " + actionItems.size());
        System.out.println("   Questions: " + questions.size());
        
        return new AnalysisResult(topics, actionItems, questions, statistics);
    }
    
    /**
     * Extract key topics from messages using word frequency analysis
     * 
     * @param messages List of messages
     * @return List of key topics (top 5)
     */
    public List<String> extractKeyTopics(List<Message> messages) {
        
        // Count word frequencies
        Map<String, Integer> wordFrequency = new HashMap<>();
        
        for (Message message : messages) {
            String content = message.getContent().toLowerCase();
            
            // Remove punctuation and split into words
            String[] words = content.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
            
            for (String word : words) {
                word = word.trim();
                
                // Skip short words and stop words
                if (word.length() < 3 || STOP_WORDS.contains(word)) {
                    continue;
                }
                
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }
        
        // Sort by frequency and get top topics
        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordFrequency.entrySet());
        sortedWords.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // Get top 5 topics with frequency > 1
        List<String> topics = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedWords) {
            if (entry.getValue() > 1 && topics.size() < 5) {
                topics.add(entry.getKey() + " (mentioned " + entry.getValue() + " times)");
            }
        }
        
        return topics;
    }
    
    /**
     * Identify action items from messages
     * Looks for sentences with action keywords
     * 
     * @param messages List of messages
     * @return List of action items
     */
    public List<String> identifyActionItems(List<Message> messages) {
        
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
        
        return actionItems;
    }
    
    /**
     * Find pending questions (sentences ending with ?)
     * 
     * @param messages List of messages
     * @return List of pending questions
     */
    public List<String> findPendingQuestions(List<Message> messages) {
        
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
        
        return questions;
    }
    
    /**
     * Calculate conversation statistics
     * 
     * @param conversation Conversation object
     * @return Map of statistics
     */
    public Map<String, Object> calculateStatistics(Conversation conversation) {
        
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
        
        return stats;
    }
}
