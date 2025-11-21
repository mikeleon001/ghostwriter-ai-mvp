// Summary.java - Summary Generation and Formatting

package com.ghostwriter.analysis;

import com.ghostwriter.database.Database;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Summary class generates and formats daily conversation summaries
 * Stores summaries in database for later retrieval
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
    private String summaryText;
    private Date createdAt;

    // Constructor
    public Summary() {
        this.keyTopics = new ArrayList<>();
        this.actionItems = new ArrayList<>();
        this.pendingQuestions = new ArrayList<>();
        this.statistics = new HashMap<>();
        this.createdAt = new Date();
    }

    // Full constructor
    public Summary(String summaryId, String userId, String conversationId, String date,
                   List<String> keyTopics, List<String> actionItems,
                   List<String> pendingQuestions, Map<String, Object> statistics) {
        this.summaryId = summaryId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.date = date;
        this.keyTopics = keyTopics;
        this.actionItems = actionItems;
        this.pendingQuestions = pendingQuestions;
        this.statistics = statistics;
        this.createdAt = new Date();
    }

    /**
     * Generate summary from analysis result
     */
    public static Summary generate(String userId, String conversationId,
                                   String date, AnalysisResult analysis) {

        String summaryId = UUID.randomUUID().toString();

        Summary summary = new Summary(
                summaryId,
                userId,
                conversationId,
                date,
                analysis.getTopics(),
                analysis.getActionItems(),
                analysis.getPendingQuestions(),
                analysis.getStatistics()
        );

        summary.summaryText = summary.formatSummary();

        System.out.println("📝 Summary generated for " + date);

        return summary;
    }

    /**
     * Format summary as readable text
     */
    public String formatSummary() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("   📅 DAILY SUMMARY - ").append(date).append("\n");
        sb.append("═══════════════════════════════════════════════════\n\n");

        // Message Statistics
        sb.append("📊 MESSAGE STATISTICS\n");
        sb.append("─────────────────────────────────────────────────\n");
        sb.append("Total Messages: ").append(statistics.getOrDefault("total_messages", 0)).append("\n");

        // Sender breakdown
        @SuppressWarnings("unchecked")
        Map<String, Integer> senderBreakdown = (Map<String, Integer>) statistics.get("sender_breakdown");
        if (senderBreakdown != null) {
            sb.append("Participants:\n");
            for (Map.Entry<String, Integer> entry : senderBreakdown.entrySet()) {
                sb.append("  • ").append(entry.getKey())
                        .append(": ").append(entry.getValue()).append(" messages\n");
            }
        }

        sb.append("Most Active: ").append(statistics.getOrDefault("most_active", "N/A")).append("\n");

        if (statistics.containsKey("avg_message_length")) {
            sb.append("Avg Message Length: ").append(statistics.get("avg_message_length")).append(" characters\n");
        }

        sb.append("\n");

        // Key Topics
        if (!keyTopics.isEmpty()) {
            sb.append("🔑 KEY TOPICS DISCUSSED\n");
            sb.append("─────────────────────────────────────────────────\n");
            for (int i = 0; i < keyTopics.size(); i++) {
                sb.append((i + 1)).append(". ").append(keyTopics.get(i)).append("\n");
            }
            sb.append("\n");
        } else {
            sb.append("🔑 KEY TOPICS DISCUSSED\n");
            sb.append("─────────────────────────────────────────────────\n");
            sb.append("No specific topics identified\n\n");
        }

        // Action Items
        if (!actionItems.isEmpty()) {
            sb.append("⚡ ACTION ITEMS\n");
            sb.append("─────────────────────────────────────────────────\n");
            for (String item : actionItems) {
                sb.append("☐ ").append(item).append("\n");
            }
            sb.append("\n");
        }

        // Pending Questions
        if (!pendingQuestions.isEmpty()) {
            sb.append("❓ PENDING QUESTIONS\n");
            sb.append("─────────────────────────────────────────────────\n");
            for (String question : pendingQuestions) {
                sb.append("? ").append(question).append("\n");
            }
            sb.append("\n");
        }

        // Footer
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("Generated by GhostWriter AI\n");
        sb.append("═══════════════════════════════════════════════════\n");

        return sb.toString();
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
    }

    /**
     * Load summary from database
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

        Summary summary = new Summary(summaryId, userId, conversationId, date,
                topics, actions, questions, stats);

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