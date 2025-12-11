package com.ghostwriter.notification;

import com.ghostwriter.analysis.StatisticsKeys;
import com.ghostwriter.analysis.Summary;
import java.util.Map;

/**
 * Concrete Observer that simulates sending email notifications.
 * Part of the Observer design pattern implementation.
 * 
 * In a production system, this would integrate with an email service
 * like SendGrid, AWS SES, or JavaMail API.
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class EmailNotifier implements SummaryObserver {
    
    private final String emailAddress;
    
    /**
     * Creates an email notifier for the specified email address.
     * 
     * @param emailAddress The email address to send notifications to
     * @throws IllegalArgumentException if email address is null or empty
     */
    public EmailNotifier(String emailAddress) {
        if (emailAddress == null || emailAddress.isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be null or empty");
        }
        this.emailAddress = emailAddress;
    }
    
    /**
     * Simulates sending an email notification when a summary is generated.
     * 
     * @param summary The generated summary
     */
    @Override
    public void onSummaryGenerated(Summary summary) {
        Map<String, Object> stats = summary.getStatistics();
        Object totalMessages = stats.getOrDefault(StatisticsKeys.TOTAL_MESSAGES, 0);
        
        System.out.println("\n📧 EMAIL NOTIFICATION");
        System.out.println("━".repeat(60));
        System.out.println("To: " + emailAddress);
        System.out.println("Subject: Your Daily Summary for " + summary.getDate());
        System.out.println("\nBody:");
        System.out.println("  Hello!");
        System.out.println();
        System.out.println("  Your daily conversation summary is ready:");
        System.out.println("  • " + totalMessages + " messages analyzed");
        System.out.println("  • " + summary.getKeyTopics().size() + " key topics identified");
        System.out.println("  • " + summary.getActionItems().size() + " action items found");
        
        if (!summary.getPendingQuestions().isEmpty()) {
            System.out.println("  • " + summary.getPendingQuestions().size() + " pending questions");
        }
        
        System.out.println();
        System.out.println("  Open GhostWriter AI to view your complete summary.");
        System.out.println();
        System.out.println("  Best regards,");
        System.out.println("  GhostWriter AI Team");
        System.out.println("━".repeat(60));
    }
    
    /**
     * Gets the email address this notifier sends to.
     * 
     * @return The email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }
}
