package com.ghostwriter.database;

/**
 * Constants for database column names.
 * Centralizes all column name strings to prevent typos and enable refactoring.
 * 
 * Usage:
 *   data.put(DatabaseColumns.USER_ID, userId)
 *   result.get(DatabaseColumns.USERNAME)
 *
 * @author Mihail Chitorog
 * @version 1.0
 */
public final class DatabaseColumns {

    // Prevent instantiation
    private DatabaseColumns() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    // ============================================
    // USER TABLE COLUMNS
    // ============================================

    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String CREATED_DATE = "created_date";

    // ============================================
    // ACCOUNT TABLE COLUMNS
    // ============================================

    public static final String ACCOUNT_ID = "account_id";
    public static final String PASSWORD_HASH = "password_hash";
    public static final String IS_ACTIVE = "is_active";

    // ============================================
    // SESSION TABLE COLUMNS
    // ============================================

    public static final String SESSION_ID = "session_id";
    public static final String TOKEN = "token";
    public static final String EXPIRES_AT = "expires_at";

    // ============================================
    // CONVERSATION TABLE COLUMNS
    // ============================================

    public static final String CONVERSATION_ID = "conversation_id";
    public static final String DATE = "date";
    public static final String MESSAGE_COUNT = "message_count";

    // ============================================
    // MESSAGE TABLE COLUMNS
    // ============================================

    public static final String MESSAGE_ID = "message_id";
    public static final String CONTENT = "content";
    public static final String SENDER = "sender";
    public static final String TIMESTAMP = "timestamp";

    // ============================================
    // SUMMARY TABLE COLUMNS
    // ============================================

    public static final String SUMMARY_ID = "summary_id";
    public static final String KEY_TOPICS = "key_topics";
    public static final String ACTION_ITEMS = "action_items";
    public static final String PENDING_QUESTIONS = "pending_questions";
    public static final String STATISTICS = "statistics";

    // ============================================
    // REPORT TABLE COLUMNS
    // ============================================

    public static final String REPORT_ID = "report_id";
    public static final String REPORT_TYPE = "report_type";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String TOTAL_MESSAGES = "total_messages";
    public static final String TOP_TOPICS = "top_topics";
    public static final String ACTION_ITEMS_COUNT = "action_items_count";
    public static final String MOST_ACTIVE_DAY = "most_active_day";
}
