package com.ghostwriter.notification;

import com.ghostwriter.analysis.Summary;
import java.util.Map;

/**
 * Concrete Observer that displays summary notifications in the console.
 * Part of the Observer design pattern implementation.
 * 
 * This notifier provides immediate visual feedback when a summary is generated,
 * useful for development and debugging.
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class ConsoleNotifier implements SummaryObserver {
    
    /**
     * Displays a formatted console notification when a summary is generated.
     * 
     * @param summary The generated summary
     */
    @Override
    public void onSummaryGenerated(Summary summary) {
        System.out.println("\n" + "━".repeat(60));
        System.out.println("🔔 NOTIFICATION: Daily Summary Generated");
        System.out.println("━".repeat(60));
        System.out.println("📅 Date: " + summary.getDate());
        
        // Get message count from statistics map
        Map<String, Object> stats = summary.getStatistics();
        Object totalMessages = stats.getOrDefault("total_messages", 0);
        System.out.println("📊 Messages: " + totalMessages);
        
        System.out.println("🔑 Topics: " + summary.getKeyTopics().size());
        System.out.println("⚡ Action Items: " + summary.getActionItems().size());
        
        if (!summary.getPendingQuestions().isEmpty()) {
            System.out.println("❓ Questions: " + summary.getPendingQuestions().size());
        }
        
        System.out.println("━".repeat(60));
    }
}
