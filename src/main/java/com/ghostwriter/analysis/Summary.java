// Summary.java - Summary Generation and Formatting

package com.ghostwriter.analysis;

import com.ghostwriter.database.Database;

import com.ghostwriter.notification.SummaryObserver;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Summary class generates and formats daily conversation summaries.
 * Stores summaries in database for later retrieval.
 * 
 * Design Pattern: BUILDER PATTERN
 * Purpose: Simplify object construction with many optional parameters.
 * Usage:
 *   Summary summary = new Summary.Builder(userId, conversationId, date)
 *       .summaryId(id)
 *       .keyTopics(topics)
 *       .actionItems(items)
 *       .build();
 *
 * @author Mihail Chitorog
 * @version 2.0
 */
public class Summary {

    private String summaryId;
    private String userId;
    private String conversationId;
    private String date;
    private List<String> keyTopics;
    private List<String> actionItems;
    private List<String> pendingQuestions;
    private Map<String, Object> statistics;
    private List<SummaryObserver> observers;
    private String summaryText;
    private Date createdAt;

    // ============================================
    // FORMATTING CONSTANTS
    // ============================================
    private static final String HEADER_LINE = 
        "═══════════════════════════════════════════════════\n";
    private static final String SECTION_LINE = 
        "─────────────────────────────────────────────────\n";

    // ============================================
    // BUILDER PATTERN IMPLEMENTATION
    // ============================================
    
    /**
     * Builder class for constructing Summary objects.
     * Implements the Builder design pattern to handle multiple optional parameters.
     */
    public static class Builder {
        // Required parameters
        private final String userId;
        private final String conversationId;
        private final String date;
        
        // Optional parameters with defaults
        private String summaryId = UUID.randomUUID().toString();
        private List<String> keyTopics = new ArrayList<>();
        private List<String> actionItems = new ArrayList<>();
        private List<String> pendingQuestions = new ArrayList<>();
        private Map<String, Object> statistics = new HashMap<>();

        /**
         * Create a Builder with required parameters.
         *
         * @param userId The user ID (required)
         * @param conversationId The conversation ID (required)
         * @param date The summary date (required)
         */
        public Builder(String userId, String conversationId, String date) {
            this.userId = userId;
            this.conversationId = conversationId;
            this.date = date;
        }

        /**
         * Set custom summary ID (optional).
         */
        public Builder summaryId(String summaryId) {
            this.summaryId = summaryId;
            return this;
        }

        /**
         * Set key topics (optional).
         */
        public Builder keyTopics(List<String> keyTopics) {
            this.keyTopics = keyTopics != null ? keyTopics : new ArrayList<>();
            return this;
        }

        /**
         * Set action items (optional).
         */
        public Builder actionItems(List<String> actionItems) {
            this.actionItems = actionItems != null ? actionItems : new ArrayList<>();
            return this;
        }

        /**
         * Set pending questions (optional).
         */
        public Builder pendingQuestions(List<String> pendingQuestions) {
            this.pendingQuestions = pendingQuestions != null ? pendingQuestions : new ArrayList<>();
            return this;
        }

        /**
         * Set statistics (optional).
         */
        public Builder statistics(Map<String, Object> statistics) {
            this.statistics = statistics != null ? statistics : new HashMap<>();
            return this;
        }

        /**
         * Build the Summary object.
         *
         * @return A new Summary instance
         */
        public Summary build() {
            return new Summary(this);
        }
    }

    /**
     * Private constructor used by Builder.
     */
    private Summary(Builder builder) {
        this.summaryId = builder.summaryId;
        this.userId = builder.userId;
        this.conversationId = builder.conversationId;
        this.date = builder.date;
        this.keyTopics = builder.keyTopics;
        this.actionItems = builder.actionItems;
        this.pendingQuestions = builder.pendingQuestions;
        this.statistics = builder.statistics;
        this.observers = new ArrayList<>();
        this.createdAt = new Date();
    }

    /**
     * Default constructor for simple initialization.
     */
    public Summary() {
        this.keyTopics = new ArrayList<>();
        this.actionItems = new ArrayList<>();
        this.pendingQuestions = new ArrayList<>();
        this.statistics = new HashMap<>();
        this.observers = new ArrayList<>();
        this.createdAt = new Date();
    }

    /**
     * Generate summary from analysis result using Builder pattern.
     *
     * @param userId The user ID
     * @param conversationId The conversation ID  
     * @param date The summary date
     * @param analysis The analysis result containing topics, actions, etc.
     * @return A new Summary instance
     */
    public static Summary generate(String userId, String conversationId,
                                   String date, AnalysisResult analysis) {

        Summary summary = new Summary.Builder(userId, conversationId, date)
                .keyTopics(analysis.getTopics())
                .actionItems(analysis.getActionItems())
                .pendingQuestions(analysis.getPendingQuestions())
                .statistics(analysis.getStatistics())
                .build();

        summary.summaryText = summary.formatSummary();

        System.out.println("📝 Summary generated for " + date);

        return summary;
    }

    /**
     * Format summary as readable text.
     * Delegates to helper methods for each section (Clean Code refactoring).
     */
    public String formatSummary() {
        StringBuilder sb = new StringBuilder();

        appendHeader(sb);
        appendStatisticsSection(sb);
        appendTopicsSection(sb);
        appendActionItemsSection(sb);
        appendQuestionsSection(sb);
        appendFooter(sb);

        return sb.toString();
    }

    /**
     * Append the summary header with date.
     */
    private void appendHeader(StringBuilder sb) {
        sb.append(HEADER_LINE);
        sb.append("   📅 DAILY SUMMARY - ").append(date).append("\n");
        sb.append(HEADER_LINE).append("\n");
    }

    /**
     * Append message statistics section.
     */
    private void appendStatisticsSection(StringBuilder sb) {
        sb.append("📊 MESSAGE STATISTICS\n");
        sb.append(SECTION_LINE);
        sb.append("Total Messages: ")
          .append(statistics.getOrDefault(StatisticsKeys.TOTAL_MESSAGES, 0))
          .append("\n");

        appendSenderBreakdown(sb);

        sb.append("Most Active: ")
          .append(statistics.getOrDefault(StatisticsKeys.MOST_ACTIVE, "N/A"))
          .append("\n");

        if (statistics.containsKey(StatisticsKeys.AVG_MESSAGE_LENGTH)) {
            sb.append("Avg Message Length: ")
              .append(statistics.get(StatisticsKeys.AVG_MESSAGE_LENGTH))
              .append(" characters\n");
        }
        sb.append("\n");
    }

    /**
     * Append sender breakdown if available.
     */
    @SuppressWarnings("unchecked")
    private void appendSenderBreakdown(StringBuilder sb) {
        Map<String, Integer> senderBreakdown =
            (Map<String, Integer>) statistics.get(StatisticsKeys.SENDER_BREAKDOWN);

        if (senderBreakdown == null) {
            return;
        }

        sb.append("Participants:\n");
        for (Map.Entry<String, Integer> entry : senderBreakdown.entrySet()) {
            sb.append("  • ").append(entry.getKey())
              .append(": ").append(entry.getValue())
              .append(" messages\n");
        }
    }

    /**
     * Append key topics section.
     */
    private void appendTopicsSection(StringBuilder sb) {
        sb.append("🔑 KEY TOPICS DISCUSSED\n");
        sb.append(SECTION_LINE);

        if (keyTopics.isEmpty()) {
            sb.append("No specific topics identified\n\n");
            return;
        }

        for (int i = 0; i < keyTopics.size(); i++) {
            sb.append((i + 1)).append(". ").append(keyTopics.get(i)).append("\n");
        }
        sb.append("\n");
    }

    /**
     * Append action items section if any exist.
     */
    private void appendActionItemsSection(StringBuilder sb) {
        if (actionItems.isEmpty()) {
            return;
        }

        sb.append("⚡ ACTION ITEMS\n");
        sb.append(SECTION_LINE);
        for (String item : actionItems) {
            sb.append("☐ ").append(item).append("\n");
        }
        sb.append("\n");
    }

    /**
     * Append pending questions section if any exist.
     */
    private void appendQuestionsSection(StringBuilder sb) {
        if (pendingQuestions.isEmpty()) {
            return;
        }

        sb.append("❓ PENDING QUESTIONS\n");
        sb.append(SECTION_LINE);
        for (String question : pendingQuestions) {
            sb.append("? ").append(question).append("\n");
        }
        sb.append("\n");
    }

    /**
     * Append the summary footer.
     */
    private void appendFooter(StringBuilder sb) {
        sb.append(HEADER_LINE);
        sb.append("Generated by GhostWriter AI\n");
        sb.append(HEADER_LINE);
    }

    /**
     * Register an observer to be notified when this summary is generated.
     * Part of Observer design pattern implementation.
     *
     * @param observer The observer to register
     */
    public void addObserver(SummaryObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Unregister an observer so it no longer receives notifications.
     * Part of Observer design pattern implementation.
     *
     * @param observer The observer to unregister
     */
    public void removeObserver(SummaryObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notify all registered observers that this summary has been generated.
     * Part of Observer design pattern implementation.
     */
    private void notifyObservers() {
        for (SummaryObserver observer : observers) {
            try {
                observer.onSummaryGenerated(this);
            } catch (Exception e) {
                // Log error but don't fail if one observer has an issue
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    /**
     * Save summary to database
     */
    public void saveSummary() {
        Database db = Database.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String topicsJson = String.join("|||", keyTopics);
        String actionItemsJson = String.join("|||", actionItems);
        String statsJson = mapToString(statistics);

        Map<String, String> data = new HashMap<>();
        data.put("summary_id", summaryId);
        data.put("user_id", userId);
        data.put("conversation_id", conversationId);
        data.put("date", date);
        data.put("key_topics", topicsJson);
        data.put("action_items", actionItemsJson);
        data.put("statistics", statsJson);
        data.put("summary_text", summaryText);
        data.put("created_at", sdf.format(createdAt));

        db.store("summaries", data);

        System.out.println("💾 Summary saved to database");

        notifyObservers();
    }

    /**
     * Load summary from database using Builder pattern.
     *
     * @param data Map containing summary data from database
     * @return A Summary instance populated from database data
     */
    public static Summary fromDatabase(Map<String, String> data) {
        String summaryId = data.get("summary_id");
        String userId = data.get("user_id");
        String conversationId = data.get("conversation_id");
        String date = data.get("date");

        List<String> topics = parseList(data.get("key_topics"));
        List<String> actions = parseList(data.get("action_items"));
        List<String> questions = new ArrayList<>();
        Map<String, Object> stats = parseMap(data.get("statistics"));

        Summary summary = new Summary.Builder(userId, conversationId, date)
                .summaryId(summaryId)
                .keyTopics(topics)
                .actionItems(actions)
                .pendingQuestions(questions)
                .statistics(stats)
                .build();

        summary.summaryText = data.get("summary_text");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            summary.createdAt = sdf.parse(data.get("created_at"));
        } catch (Exception e) {
            summary.createdAt = new Date();
        }

        return summary;
    }

    // Helper methods
    private static List<String> parseList(String data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(data.split("\\|\\|\\|")));
    }

    private static Map<String, Object> parseMap(String data) {
        Map<String, Object> map = new HashMap<>();
        if (data == null || data.isEmpty()) {
            return map;
        }

        String[] pairs = data.split("\\|\\|\\|");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":::");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }

        return map;
    }

    private static String mapToString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("|||");
            }
            sb.append(entry.getKey()).append(":::").append(entry.getValue());
        }
        return sb.toString();
    }

    // Getters
    public String getSummaryId() { return summaryId; }
    public String getUserId() { return userId; }
    public String getConversationId() { return conversationId; }
    public String getDate() { return date; }
    public List<String> getKeyTopics() { return keyTopics; }
    public List<String> getActionItems() { return actionItems; }
    public List<String> getPendingQuestions() { return pendingQuestions; }
    public Map<String, Object> getStatistics() { return statistics; }
    public String getSummaryText() { return summaryText; }
    public Date getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Summary for " + date;
    }
}