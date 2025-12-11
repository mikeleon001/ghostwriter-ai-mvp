package com.ghostwriter.notification;

import com.ghostwriter.analysis.Summary;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Concrete Observer that logs summary generation events.
 * Part of the Observer design pattern implementation.
 * 
 * In a production system, this would write to actual log files using
 * a logging framework like Log4j or SLF4J.
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class LogFileNotifier implements SummaryObserver {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs a summary generation event with timestamp and details.
     * 
     * @param summary The generated summary
     */
    @Override
    public void onSummaryGenerated(Summary summary) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        Map<String, Object> stats = summary.getStatistics();
        Object totalMessages = stats.getOrDefault("total_messages", 0);
        
        String logEntry = String.format(
            "[%s] SUMMARY_GENERATED | Date: %s | Messages: %s | Topics: %d | Actions: %d",
            timestamp,
            summary.getDate(),
            totalMessages,
            summary.getKeyTopics().size(),
            summary.getActionItems().size()
        );
        
        System.out.println("\n📝 LOG ENTRY: " + logEntry);
    }
}
