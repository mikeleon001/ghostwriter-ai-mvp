// Analyzer.java - REFACTORED WITH STRATEGY PATTERN

package com.ghostwriter.analysis;

import com.ghostwriter.message.Conversation;

import java.util.*;

/**
 * Analyzer class - Context for Strategy Pattern
 * Coordinates multiple analysis strategies to produce comprehensive results
 * 
 * Design Pattern: Strategy (Context)
 * Purpose: Allows flexible composition of analysis algorithms at runtime
 * 
 * Instead of doing all analysis itself, Analyzer delegates to strategy objects.
 * This makes it easy to:
 * - Add new analysis types without modifying Analyzer
 * - Test each analysis type independently
 * - Select which analyses to run dynamically
 */
public class Analyzer {
    
    // List of strategies to apply
    private List<AnalysisStrategy> strategies;
    
    /**
     * Constructor with default strategies
     * Creates analyzer with all standard analysis strategies
     */
    public Analyzer() {
        this.strategies = new ArrayList<>();
        // Add default strategies
        addStrategy(new TopicExtractionStrategy());
        addStrategy(new ActionItemStrategy());
        addStrategy(new QuestionDetectionStrategy());
        addStrategy(new StatisticsStrategy());
    }
    
    /**
     * Constructor with custom strategies
     * Allows client to specify which strategies to use
     * 
     * @param strategies List of analysis strategies
     */
    public Analyzer(List<AnalysisStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }
    
    /**
     * Add a strategy to the analyzer
     * Allows runtime composition of strategies
     * 
     * @param strategy Strategy to add
     */
    public void addStrategy(AnalysisStrategy strategy) {
        if (strategy != null) {
            this.strategies.add(strategy);
        }
    }
    
    /**
     * Set strategies (replaces all existing strategies)
     * 
     * @param strategies New list of strategies
     */
    public void setStrategies(List<AnalysisStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }
    
    /**
     * Get current strategies
     * 
     * @return List of active strategies
     */
    public List<AnalysisStrategy> getStrategies() {
        return new ArrayList<>(strategies);
    }
    
    /**
     * Analyze conversation using all configured strategies
     * 
     * STRATEGY PATTERN IN ACTION:
     * Instead of doing analysis itself, Analyzer delegates to each strategy
     * and aggregates the results
     * 
     * @param conversation Conversation to analyze
     * @return Comprehensive AnalysisResult combining all strategy results
     */
    public AnalysisResult analyzeConversation(Conversation conversation) {
        
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation cannot be null");
        }
        
        if (conversation.getMessages() == null || conversation.getMessages().isEmpty()) {
            throw new IllegalArgumentException("Cannot analyze empty conversation");
        }
        
        System.out.println("\n🔍 Analyzing conversation with " + strategies.size() + " strategies...");
        System.out.println("   Total messages: " + conversation.getMessages().size());
        
        // Collect results from all strategies
        List<String> allTopics = new ArrayList<>();
        List<String> allActionItems = new ArrayList<>();
        List<String> allQuestions = new ArrayList<>();
        Map<String, Object> allStatistics = new HashMap<>();
        
        // Execute each strategy
        for (AnalysisStrategy strategy : strategies) {
            System.out.println("\n   Running: " + strategy.getStrategyName());
            
            AnalysisResult partialResult = strategy.analyze(conversation);
            
            // Merge results
            if (partialResult.getTopics() != null && !partialResult.getTopics().isEmpty()) {
                allTopics.addAll(partialResult.getTopics());
            }
            
            if (partialResult.getActionItems() != null && !partialResult.getActionItems().isEmpty()) {
                allActionItems.addAll(partialResult.getActionItems());
            }
            
            if (partialResult.getQuestions() != null && !partialResult.getQuestions().isEmpty()) {
                allQuestions.addAll(partialResult.getQuestions());
            }
            
            if (partialResult.getStatistics() != null && !partialResult.getStatistics().isEmpty()) {
                allStatistics.putAll(partialResult.getStatistics());
            }
        }
        
        // Create comprehensive result
        AnalysisResult finalResult = new AnalysisResult(
            allTopics, 
            allActionItems, 
            allQuestions, 
            allStatistics
        );
        
        System.out.println("\n✅ Analysis complete!");
        System.out.println("   Topics found: " + allTopics.size());
        System.out.println("   Action items: " + allActionItems.size());
        System.out.println("   Questions: " + allQuestions.size());
        System.out.println("   Statistics: " + allStatistics.size() + " metrics\n");
        
        return finalResult;
    }
    
    /**
     * Convenience method: Analyze with specific strategies
     * Allows one-time analysis with custom strategies without modifying the analyzer
     * 
     * @param conversation Conversation to analyze
     * @param customStrategies Strategies to use for this analysis only
     * @return AnalysisResult
     */
    public AnalysisResult analyzeWith(Conversation conversation, AnalysisStrategy... customStrategies) {
        // Save current strategies
        List<AnalysisStrategy> savedStrategies = new ArrayList<>(this.strategies);
        
        // Temporarily replace with custom strategies
        this.strategies = Arrays.asList(customStrategies);
        
        // Perform analysis
        AnalysisResult result = analyzeConversation(conversation);
        
        // Restore original strategies
        this.strategies = savedStrategies;
        
        return result;
    }
}
