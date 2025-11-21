package com.ghostwriter.analysis;

import java.util.*;

public class AnalysisResult {
    
    private List<String> topics;
    private List<String> actionItems;
    private List<String> pendingQuestions;
    private Map<String, Object> statistics;
    
    // Default constructor
    public AnalysisResult() {
        this.topics = new ArrayList<>();
        this.actionItems = new ArrayList<>();
        this.pendingQuestions = new ArrayList<>();
        this.statistics = new HashMap<>();
    }
    
    // FIXED: Full constructor with all 4 parameters
    public AnalysisResult(List<String> topics, List<String> actionItems, 
                         List<String> pendingQuestions, Map<String, Object> statistics) {
        this.topics = topics != null ? topics : new ArrayList<>();
        this.actionItems = actionItems != null ? actionItems : new ArrayList<>();
        this.pendingQuestions = pendingQuestions != null ? pendingQuestions : new ArrayList<>();
        this.statistics = statistics != null ? statistics : new HashMap<>();
    }
    
    // Getters
    public List<String> getTopics() {
        return topics;
    }
    
    public List<String> getActionItems() {
        return actionItems;
    }
    
    public List<String> getPendingQuestions() {
        return pendingQuestions;
    }
    
    public Map<String, Object> getStatistics() {
        return statistics;
    }
    
    // Utility methods
    public boolean hasTopics() {
        return !topics.isEmpty();
    }
    
    public boolean hasActionItems() {
        return !actionItems.isEmpty();
    }
    
    public boolean hasPendingQuestions() {
        return !pendingQuestions.isEmpty();
    }
    
    public int getTopicCount() {
        return topics.size();
    }
    
    public int getActionItemCount() {
        return actionItems.size();
    }
    
    public int getQuestionCount() {
        return pendingQuestions.size();
    }
    
    @Override
    public String toString() {
        return "AnalysisResult{" +
                "topics=" + topics.size() +
                ", actionItems=" + actionItems.size() +
                ", questions=" + pendingQuestions.size() +
                ", statistics=" + statistics.size() + " entries" +
                '}';
    }
}
