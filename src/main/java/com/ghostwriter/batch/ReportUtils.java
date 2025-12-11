package com.ghostwriter.batch;

import com.ghostwriter.analysis.StatisticsKeys;
import com.ghostwriter.analysis.Summary;

import java.util.*;

/**
 * Utility class for shared report functionality.
 * Eliminates code duplication between WeeklyReport and MonthlyReport.
 * 
 * Contains:
 * - Formatting constants for consistent report styling
 * - Helper methods for extracting statistics from summaries
 * - Common data processing utilities
 *
 * @author Mihail Chitorog
 * @version 1.0
 */
public final class ReportUtils {

    // Prevent instantiation
    private ReportUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ============================================
    // FORMATTING CONSTANTS
    // ============================================

    /** Header line for report sections (double line). */
    public static final String HEADER_LINE = 
        "═══════════════════════════════════════════════════\n";

    /** Section divider line (single line). */
    public static final String SECTION_LINE = 
        "─────────────────────────────────────────────────\n";

    // ============================================
    // STATISTICS EXTRACTION METHODS
    // ============================================

    /**
     * Extract message count from a summary's statistics.
     * Handles both Integer and String representations safely.
     *
     * @param summary The summary to extract message count from
     * @return The message count, or 0 if not available
     */
    public static int getMessageCount(Summary summary) {
        if (summary == null) {
            return 0;
        }

        Map<String, Object> stats = summary.getStatistics();
        if (stats == null) {
            return 0;
        }

        Object count = stats.get(StatisticsKeys.TOTAL_MESSAGES);
        if (count == null) {
            return 0;
        }

        if (count instanceof Integer) {
            return (Integer) count;
        } else if (count instanceof String) {
            try {
                return Integer.parseInt((String) count);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // ============================================
    // DATA PROCESSING METHODS
    // ============================================

    /**
     * Find the top N topics by frequency from a frequency map.
     * Returns formatted strings with mention counts.
     *
     * @param topicFrequency Map of topic to frequency count
     * @param limit Maximum number of topics to return
     * @return List of formatted topic strings (e.g., "Project (5 mentions)")
     */
    public static List<String> findTopTopics(Map<String, Integer> topicFrequency, int limit) {
        if (topicFrequency == null || topicFrequency.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(topicFrequency.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            result.add(entry.getKey() + " (" + entry.getValue() + " mentions)");
        }
        return result;
    }

    /**
     * Find the entry with the maximum value in a map.
     * Useful for finding most active day/week.
     *
     * @param counts Map of key to count
     * @return The key with the highest count, or null if empty
     */
    public static String findMaxEntry(Map<String, Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            return null;
        }

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Get the maximum value from a counts map.
     *
     * @param counts Map of key to count
     * @return The highest count, or 0 if empty
     */
    public static int getMaxCount(Map<String, Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            return 0;
        }

        return counts.values().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }
}
