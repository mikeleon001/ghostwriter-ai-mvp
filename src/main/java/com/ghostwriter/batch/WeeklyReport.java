package com.ghostwriter.batch;

import com.ghostwriter.analysis.Summary;
import com.ghostwriter.database.Database;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * WeeklyReport aggregates 7 days of daily summaries into one comprehensive report.
 * 
 * This class provides:
 * - Combined statistics for the entire week
 * - Top topics discussed across all days
 * - All action items collected from the week
 * - Identification of the most active day
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class WeeklyReport {

    private final String reportId;
    private final String userId;
    private final Date startDate;
    private final Date endDate;
    private final List<Summary> summaries;
    private int totalMessages;
    private List<String> topTopics;
    private List<String> allActionItems;
    private List<String> allPendingQuestions;
    private String mostActiveDay;
    private int mostActiveDayMessageCount;
    private final Date generatedAt;

    /**
     * Private constructor - use generate() factory method instead.
     */
    private WeeklyReport(String userId, Date startDate, Date endDate) {
        this.reportId = UUID.randomUUID().toString();
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.summaries = new ArrayList<>();
        this.totalMessages = 0;
        this.topTopics = new ArrayList<>();
        this.allActionItems = new ArrayList<>();
        this.allPendingQuestions = new ArrayList<>();
        this.mostActiveDay = "N/A";
        this.mostActiveDayMessageCount = 0;
        this.generatedAt = new Date();
    }

    /**
     * Generate a weekly report for the specified user and end date.
     * The report covers 7 days ending on the specified date.
     *
     * @param userId The user to generate the report for
     * @param endDate The last day of the week (report covers 7 days ending here)
     * @return A new WeeklyReport with aggregated data
     */
    public static WeeklyReport generate(String userId, Date endDate) {
        // Calculate start date (7 days before end date)
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_MONTH, -6); // Go back 6 days (inclusive of end date = 7 days)
        Date startDate = cal.getTime();

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║   📅 GENERATING WEEKLY REPORT                  ║");
        System.out.println("╚════════════════════════════════════════════════╝");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Period: " + sdf.format(startDate) + " to " + sdf.format(endDate));

        WeeklyReport report = new WeeklyReport(userId, startDate, endDate);
        
        // Load summaries from database
        report.loadSummaries();
        
        // Aggregate the data
        report.aggregateStatistics();
        
        System.out.println("✅ Weekly report generated successfully!");
        
        return report;
    }

    /**
     * Load summaries from the database for the date range.
     */
    private void loadSummaries() {
        Database db = Database.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDateStr = sdf.format(startDate);
        String endDateStr = sdf.format(endDate);

        String sql = "SELECT * FROM summaries WHERE user_id = ? " +
                     "AND date >= ? AND date <= ? ORDER BY date";

        List<Map<String, String>> results = db.query(sql, userId, startDateStr, endDateStr);

        System.out.println("   Found " + results.size() + " daily summaries");

        for (Map<String, String> data : results) {
            Summary summary = Summary.fromDatabase(data);
            summaries.add(summary);
        }
    }

    /**
     * Aggregate statistics from all daily summaries.
     */
    private void aggregateStatistics() {
        if (summaries.isEmpty()) {
            System.out.println("   ⚠️ No summaries found for this period");
            return;
        }

        // Track topic frequencies
        Map<String, Integer> topicFrequency = new HashMap<>();
        
        // Track daily message counts to find most active day
        Map<String, Integer> dailyMessageCounts = new HashMap<>();

        for (Summary summary : summaries) {
            // Aggregate topics
            for (String topic : summary.getKeyTopics()) {
                topicFrequency.merge(topic, 1, Integer::sum);
            }

            // Collect action items
            allActionItems.addAll(summary.getActionItems());

            // Collect pending questions
            allPendingQuestions.addAll(summary.getPendingQuestions());

            // Count messages
            int dayMessages = getMessageCount(summary);
            totalMessages += dayMessages;
            dailyMessageCounts.put(summary.getDate(), dayMessages);
        }

        // Find top topics (sorted by frequency)
        topTopics = findTopTopics(topicFrequency, 10);

        // Find most active day
        findMostActiveDay(dailyMessageCounts);

        System.out.println("   Total messages this week: " + totalMessages);
        System.out.println("   Top topics: " + topTopics.size());
        System.out.println("   Action items: " + allActionItems.size());
    }

    /**
     * Extract message count from a summary's statistics.
     */
    private int getMessageCount(Summary summary) {
        Map<String, Object> stats = summary.getStatistics();
        if (stats == null) {
            return 0;
        }

        Object count = stats.get("total_messages");
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

    /**
     * Find the top N topics by frequency.
     */
    private List<String> findTopTopics(Map<String, Integer> topicFrequency, int limit) {
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
     * Find the most active day in the week.
     */
    private void findMostActiveDay(Map<String, Integer> dailyMessageCounts) {
        for (Map.Entry<String, Integer> entry : dailyMessageCounts.entrySet()) {
            if (entry.getValue() > mostActiveDayMessageCount) {
                mostActiveDayMessageCount = entry.getValue();
                mostActiveDay = entry.getKey();
            }
        }
    }

    /**
     * Format the weekly report as a readable string.
     *
     * @return Formatted report text
     */
    public String formatReport() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        // Header
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("   📅 WEEKLY REPORT\n");
        sb.append("   ").append(sdf.format(startDate));
        sb.append(" - ").append(sdf.format(endDate)).append("\n");
        sb.append("═══════════════════════════════════════════════════\n\n");

        // Statistics
        sb.append("📊 WEEKLY STATISTICS\n");
        sb.append("─────────────────────────────────────────────────\n");
        sb.append("Total Messages: ").append(totalMessages).append("\n");
        sb.append("Days Covered: ").append(summaries.size()).append("\n");
        sb.append("Most Active Day: ").append(mostActiveDay);
        sb.append(" (").append(mostActiveDayMessageCount).append(" messages)\n");
        
        if (summaries.size() > 0) {
            double avgPerDay = (double) totalMessages / summaries.size();
            sb.append("Average per Day: ").append(String.format("%.1f", avgPerDay));
            sb.append(" messages\n");
        }
        sb.append("\n");

        // Top Topics
        sb.append("🔑 TOP TOPICS THIS WEEK\n");
        sb.append("─────────────────────────────────────────────────\n");
        if (topTopics.isEmpty()) {
            sb.append("No topics identified\n");
        } else {
            for (int i = 0; i < topTopics.size(); i++) {
                sb.append((i + 1)).append(". ").append(topTopics.get(i)).append("\n");
            }
        }
        sb.append("\n");

        // Action Items
        sb.append("⚡ ACTION ITEMS (").append(allActionItems.size()).append(" total)\n");
        sb.append("─────────────────────────────────────────────────\n");
        if (allActionItems.isEmpty()) {
            sb.append("No action items this week\n");
        } else {
            int limit = Math.min(10, allActionItems.size());
            for (int i = 0; i < limit; i++) {
                sb.append("☐ ").append(allActionItems.get(i)).append("\n");
            }
            if (allActionItems.size() > 10) {
                sb.append("... and ").append(allActionItems.size() - 10).append(" more\n");
            }
        }
        sb.append("\n");

        // Pending Questions
        if (!allPendingQuestions.isEmpty()) {
            sb.append("❓ PENDING QUESTIONS (").append(allPendingQuestions.size()).append(" total)\n");
            sb.append("─────────────────────────────────────────────────\n");
            int limit = Math.min(5, allPendingQuestions.size());
            for (int i = 0; i < limit; i++) {
                sb.append("? ").append(allPendingQuestions.get(i)).append("\n");
            }
            if (allPendingQuestions.size() > 5) {
                sb.append("... and ").append(allPendingQuestions.size() - 5).append(" more\n");
            }
            sb.append("\n");
        }

        // Daily Breakdown
        sb.append("📆 DAILY BREAKDOWN\n");
        sb.append("─────────────────────────────────────────────────\n");
        for (Summary summary : summaries) {
            int count = getMessageCount(summary);
            String bar = createProgressBar(count, mostActiveDayMessageCount);
            sb.append(summary.getDate()).append(": ");
            sb.append(bar).append(" ").append(count).append(" msgs\n");
        }
        sb.append("\n");

        // Footer
        sb.append("═══════════════════════════════════════════════════\n");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("Generated: ").append(timestampFormat.format(generatedAt)).append("\n");
        sb.append("GhostWriter AI Weekly Report\n");
        sb.append("═══════════════════════════════════════════════════\n");

        return sb.toString();
    }

    /**
     * Create a simple text progress bar.
     */
    private String createProgressBar(int value, int max) {
        if (max == 0) {
            return "░░░░░░░░░░";
        }
        
        int filled = (int) ((value * 10.0) / max);
        filled = Math.min(10, Math.max(0, filled));
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        return bar.toString();
    }

    /**
     * Save the weekly report to the database.
     */
    public void saveReport() {
        Database db = Database.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, String> data = new HashMap<>();
        data.put("report_id", reportId);
        data.put("user_id", userId);
        data.put("report_type", "weekly");
        data.put("start_date", sdf.format(startDate));
        data.put("end_date", sdf.format(endDate));
        data.put("total_messages", String.valueOf(totalMessages));
        data.put("top_topics", String.join("|||", topTopics));
        data.put("action_items_count", String.valueOf(allActionItems.size()));
        data.put("most_active_day", mostActiveDay);
        data.put("report_text", formatReport());
        data.put("created_at", timestamp.format(generatedAt));

        db.store("reports", data);

        System.out.println("💾 Weekly report saved to database");
    }

    // ============================================
    // GETTERS
    // ============================================

    public String getReportId() {
        return reportId;
    }

    public String getUserId() {
        return userId;
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public Date getEndDate() {
        return new Date(endDate.getTime());
    }

    public List<Summary> getSummaries() {
        return new ArrayList<>(summaries);
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public List<String> getTopTopics() {
        return new ArrayList<>(topTopics);
    }

    public List<String> getAllActionItems() {
        return new ArrayList<>(allActionItems);
    }

    public List<String> getAllPendingQuestions() {
        return new ArrayList<>(allPendingQuestions);
    }

    public String getMostActiveDay() {
        return mostActiveDay;
    }

    public int getMostActiveDayMessageCount() {
        return mostActiveDayMessageCount;
    }

    public Date getGeneratedAt() {
        return new Date(generatedAt.getTime());
    }

    public int getDaysCovered() {
        return summaries.size();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.format("WeeklyReport[%s to %s, %d messages, %d days]",
                sdf.format(startDate), sdf.format(endDate), totalMessages, summaries.size());
    }
}
