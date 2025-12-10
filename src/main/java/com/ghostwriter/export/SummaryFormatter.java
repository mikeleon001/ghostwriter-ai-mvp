package com.ghostwriter.export;

import com.ghostwriter.analysis.Summary;

/**
 * Component interface for the Decorator design pattern.
 * Defines the contract for formatting summaries in various output formats.
 * 
 * Design Pattern: Decorator (Structural Pattern)
 * Purpose: Allows flexible addition of formatting behaviors without modifying
 *          the Summary class. New formats can be added by creating new decorators.
 * Benefits:
 *  - Open/Closed Principle: Open for extension, closed for modification
 *  - Single Responsibility: Each formatter handles one format
 *  - Flexible composition: Can stack decorators if needed
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public interface SummaryFormatter {
    
    /**
     * Format a summary according to this formatter's style.
     * 
     * @param summary The summary to format
     * @return Formatted string representation
     */
    String format(Summary summary);
    
    /**
     * Get the file extension appropriate for this format.
     * 
     * @return File extension including the dot (e.g., ".txt", ".md", ".html")
     */
    String getFileExtension();
}
