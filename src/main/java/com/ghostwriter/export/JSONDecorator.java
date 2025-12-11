package com.ghostwriter.export;

import com.ghostwriter.analysis.Summary;
import java.util.Map;

/**
 * Concrete Decorator that formats summaries as JSON.
 * Wraps a SummaryFormatter and produces JSON output suitable for APIs,
 * data exchange, or programmatic processing.
 * 
 * Features:
 * - Valid JSON structure
 * - Properly escaped strings
 * - Human-readable formatting (pretty-printed)
 * - All summary data included
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class JSONDecorator extends SummaryFormatterDecorator {
    
    /**
     * Constructs a JSON decorator.
     * 
     * @param formatter The formatter to wrap
     */
    public JSONDecorator(SummaryFormatter formatter) {
        super(formatter);
    }
    
    /**
     * Format summary as JSON.
     * 
     * @param summary The summary to format
     * @return JSON-formatted string
     */
    @Override
    public String format(Summary summary) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("{\n");
        
        // Basic info
        sb.append("  \"summaryId\": ").append(toJsonString(summary.getSummaryId())).append(",\n");
        sb.append("  \"userId\": ").append(toJsonString(summary.getUserId())).append(",\n");
        sb.append("  \"conversationId\": ").append(toJsonString(summary.getConversationId())).append(",\n");
        sb.append("  \"date\": ").append(toJsonString(summary.getDate())).append(",\n");
        
        // Statistics
        sb.append("  \"statistics\": {\n");
        Map<String, Object> stats = summary.getStatistics();
        
        Object totalMessages = stats.getOrDefault("total_messages", 0);
        sb.append("    \"totalMessages\": ").append(totalMessages);
        
        if (stats.containsKey("most_active")) {
            sb.append(",\n");
            sb.append("    \"mostActive\": ").append(toJsonString(stats.get("most_active").toString()));
        }
        
        if (stats.containsKey("avg_message_length")) {
            sb.append(",\n");
            sb.append("    \"avgMessageLength\": ").append(stats.get("avg_message_length"));
        }
        
        sb.append("\n  },\n");
        
        // Key Topics
        sb.append("  \"keyTopics\": [\n");
        for (int i = 0; i < summary.getKeyTopics().size(); i++) {
            sb.append("    ").append(toJsonString(summary.getKeyTopics().get(i)));
            if (i < summary.getKeyTopics().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n");
        
        // Action Items
        sb.append("  \"actionItems\": [\n");
        for (int i = 0; i < summary.getActionItems().size(); i++) {
            sb.append("    ").append(toJsonString(summary.getActionItems().get(i)));
            if (i < summary.getActionItems().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n");
        
        // Pending Questions
        sb.append("  \"pendingQuestions\": [\n");
        for (int i = 0; i < summary.getPendingQuestions().size(); i++) {
            sb.append("    ").append(toJsonString(summary.getPendingQuestions().get(i)));
            if (i < summary.getPendingQuestions().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n");
        
        // Metadata
        sb.append("  \"metadata\": {\n");
        sb.append("    \"generatedBy\": \"GhostWriter AI\",\n");
        sb.append("    \"version\": \"1.0\",\n");
        sb.append("    \"format\": \"json\"\n");
        sb.append("  }\n");
        
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Convert a string to JSON-safe format with proper escaping.
     * Handles null values and special characters.
     * 
     * @param str The string to convert
     * @return JSON-safe quoted string or "null"
     */
    private String toJsonString(String str) {
        if (str == null) {
            return "null";
        }
        
        // Escape special JSON characters
        String escaped = str.replace("\\", "\\\\")  // Backslash must be first
                            .replace("\"", "\\\"")   // Quote
                            .replace("\n", "\\n")    // Newline
                            .replace("\r", "\\r")    // Carriage return
                            .replace("\t", "\\t")    // Tab
                            .replace("\b", "\\b")    // Backspace
                            .replace("\f", "\\f");   // Form feed
        
        return "\"" + escaped + "\"";
    }
    
    /**
     * Get file extension for JSON format.
     * 
     * @return ".json"
     */
    @Override
    public String getFileExtension() {
        return ".json";
    }
}
