package com.ghostwriter.notification;

import com.ghostwriter.analysis.Summary;

/**
 * Observer interface for the Observer design pattern.
 * Observers implement this interface to be notified when a daily summary is generated.
 * 
 * Design Pattern: Observer (Behavioral Pattern)
 * Purpose: Allows multiple notification handlers (console, email, log) to react 
 *          to summary generation events without tight coupling.
 * Benefits:
 *  - Decouples Summary class from notification logic
 *  - Easy to add new notification types without modifying Summary
 *  - Follows Open/Closed Principle
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public interface SummaryObserver {
    
    /**
     * Called when a new daily summary has been generated and saved.
     * Implementing classes should handle their specific notification logic here.
     * 
     * @param summary The newly generated summary
     */
    void onSummaryGenerated(Summary summary);
}
