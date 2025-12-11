package com.ghostwriter.analysis;

/**
 * Constants for statistics map keys.
 * Centralizes all statistics key names to prevent typos and enable refactoring.
 * 
 * Usage:
 *   stats.get(StatisticsKeys.TOTAL_MESSAGES)
 *   stats.put(StatisticsKeys.MOST_ACTIVE, "Alice")
 *
 * @author Mihail Chitorog
 * @version 1.0
 */
public final class StatisticsKeys {

    // Prevent instantiation
    private StatisticsKeys() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // ============================================
    // MESSAGE STATISTICS
    // ============================================

    /** Total number of messages in the conversation. */
    public static final String TOTAL_MESSAGES = "total_messages";

    /** Average length of messages in characters. */
    public static final String AVG_MESSAGE_LENGTH = "avg_message_length";

    /** The most active participant in the conversation. */
    public static final String MOST_ACTIVE = "most_active";

    /** Breakdown of messages per sender. */
    public static final String SENDER_BREAKDOWN = "sender_breakdown";

    // ============================================
    // TIME-BASED STATISTICS
    // ============================================

    /** Most active day of the week. */
    public static final String MOST_ACTIVE_DAY = "most_active_day";

    /** Peak activity hour. */
    public static final String PEAK_HOUR = "peak_hour";

    /** Activity by day of week. */
    public static final String DAY_OF_WEEK_ACTIVITY = "day_of_week_activity";

    // ============================================
    // CONTENT STATISTICS
    // ============================================

    /** Number of questions in the conversation. */
    public static final String QUESTION_COUNT = "question_count";

    /** Number of action items identified. */
    public static final String ACTION_ITEMS_COUNT = "action_items_count";

    /** Top topics discussed. */
    public static final String TOP_TOPICS = "top_topics";
}
