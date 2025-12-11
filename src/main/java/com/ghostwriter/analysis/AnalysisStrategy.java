// AnalysisStrategy.java - STRATEGY PATTERN INTERFACE

package com.ghostwriter.analysis;

import com.ghostwriter.message.Conversation;

/**
 * Strategy Pattern Interface
 * Defines contract for different analysis algorithms
 * 
 * Design Pattern: Strategy
 * Purpose: Allow different analysis algorithms to be selected at runtime
 * 
 * Each concrete strategy implements a specific type of analysis:
 * - Topic extraction
 * - Action item identification
 * - Question detection
 * - Statistics calculation
 */
public interface AnalysisStrategy {
    
    /**
     * Analyze a conversation and return partial results
     * Each strategy focuses on one aspect of analysis
     * 
     * @param conversation The conversation to analyze
     * @return AnalysisResult with specific analysis data
     */
    AnalysisResult analyze(Conversation conversation);
    
    /**
     * Get the name of this strategy (for logging/debugging)
     * 
     * @return Strategy name
     */
    String getStrategyName();
}
