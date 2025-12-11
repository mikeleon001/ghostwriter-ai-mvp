// TopicExtractionStrategy.java - Concrete Strategy for Topic Extraction

package com.ghostwriter.analysis;

import com.ghostwriter.message.Conversation;
import com.ghostwriter.message.Message;

import java.util.*;

/**
 * Concrete Strategy: Topic Extraction
 * Analyzes conversation to extract key topics using word frequency
 * 
 * Design Pattern: Strategy (Concrete Strategy)
 */
public class TopicExtractionStrategy implements AnalysisStrategy {
    
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
    
    @Override
    public AnalysisResult analyze(Conversation conversation) {
        List<Message> messages = conversation.getMessages();
        
        if (messages == null || messages.isEmpty()) {
            return new AnalysisResult(new ArrayList<>(), new ArrayList<>(), 
                                     new ArrayList<>(), new HashMap<>());
        }
        
        System.out.println("🔍 TopicExtractionStrategy: Analyzing topics...");
        
        List<String> topics = extractKeyTopics(messages);
        
        // Return result with only topics populated
        return new AnalysisResult(topics, new ArrayList<>(), 
                                 new ArrayList<>(), new HashMap<>());
    }
    
    /**
     * Extract key topics from messages using word frequency analysis
     * 
     * @param messages List of messages
     * @return List of key topics (top 5)
     */
    private List<String> extractKeyTopics(List<Message> messages) {
        
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
        
        System.out.println("   ✓ Found " + topics.size() + " key topics");
        return topics;
    }
    
    @Override
    public String getStrategyName() {
        return "TopicExtractionStrategy";
    }
}
