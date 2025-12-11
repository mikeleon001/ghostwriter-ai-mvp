package com.ghostwriter.batch;

import com.ghostwriter.analysis.Summary;
import com.ghostwriter.database.Database;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * MonthlyReport aggregates 30 days of daily summaries with trend analysis.
 * 
 * This class provides:
 * - Combined statistics for the entire month
 * - Week-by-week breakdown showing trends
 * - Top topics discussed across all days
 * - Pattern identification (most active day of week, etc.)
 * - Average messages per day
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class MonthlyReport {

    private final String reportId;
    private final String userId;
    private final String month;
    private final Date startDate;
    private final Date endDate;
    private final List<Summary> summaries;
    private int totalMessages;
    private Map<Integer, Integer> weeklyBreakdown;
    private List<String> topTopics;
    private List<String> allActionItems;
    private int completedActionItems;
    private int mostActiveWeek;
    private String mostActiveDay;
    private double averageMessagesPerDay;
    private Map<String, Integer> dayOfWeekActivity;
    private final Date generatedAt;

    /**
     * Private constructor - use generate() factory method instead.
     */
    private MonthlyReport(String userId, String month, Date startDate, Date endDate) {
        this.reportId = UUID.randomUUID().toString();
        this.userId = userId;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.summaries = new ArrayList<>();
        this.totalMessages = 0;
        this.weeklyBreakdown = new HashMap<>();
        this.topTopics = new ArrayList<>();
        this.allActionItems = new ArrayList<>();
        this.completedActionItems = 0;
        this.mostActiveWeek = 0;
        this.mostActiveDay = "N/A";
        this.averageMessagesPerDay = 0.0;
        this.dayOfWeekActivity = new HashMap<>();
        this.generatedAt = new Date();
    }

    /**
     * Generate a monthly report for the specified user, month, and year.
     *
     * @param userId The user to generate the report for
     * @param month The month (1-12)
     * @param year The year (e.g., 2025)
     * @return A new MonthlyReport with aggregated data
     */
    public static MonthlyReport generate(String userId, int month, int year) {
        // Validate month
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        // Calculate start and end dates for the month
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1, 0, 0, 0); // Month is 0-indexed in Calendar
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        // Format month name
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        String monthName = monthFormat.format(startDate);

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║   📅 GENERATING MONTHLY REPORT                 ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("Month: " + monthName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Period: " + sdf.format(startDate) + " to " + sdf.format(endDate));

        MonthlyReport report = new MonthlyReport(userId, monthName, startDate, endDate);

        // Load summaries from database
        report.loadSummaries();

        // Aggregate the data
        report.aggregateStatistics();

        // Calculate weekly breakdown
        report.calculateWeeklyBreakdown();

        // Identify trends
        report.identifyTrends();

        System.out.println("✅ Monthly report generated successfully!");

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

        for (Summary summary : summaries) {
            // Aggregate topics
            for (String topic : summary.getKeyTopics()) {
                topicFrequency.merge(topic, 1, Integer::sum);
            }

            // Collect action items
            allActionItems.addAll(summary.getActionItems());

            // Count messages
            int dayMessages = getMessageCount(summary);
            totalMessages += dayMessages;
        }

        // Calculate average
        if (summaries.size() > 0) {
            averageMessagesPerDay = (double) totalMessages / summaries.size();
        }

        // Find top topics (sorted by frequency)
        topTopics = findTopTopics(topicFrequency, 10);

        System.out.println("   Total messages this month: " + totalMessages);
        System.out.println("   Average per day: " + String.format("%.1f", averageMessagesPerDay));
        System.out.println("   Top topics: " + topTopics.size());
        System.out.println("   Action items: " + allActionItems.size());
    }

    /**
     * Calculate week-by-week breakdown of messages.
     */
    private void calculateWeeklyBreakdown() {
        if (summaries.isEmpty()) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        int maxWeekMessages = 0;

        for (Summary summary : summaries) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date summaryDate = sdf.parse(summary.getDate());
                cal.setTime(summaryDate);

                // Get week of month (1-5)
                int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);

                int dayMessages = getMessageCount(summary);
                weeklyBreakdown.merge(weekOfMonth, dayMessages, Integer::sum);

            } catch (Exception e) {
                // Skip if date parsing fails
            }
        }

        // Find most active week
        for (Map.Entry<Integer, Integer> entry : weeklyBreakdown.entrySet()) {
            if (entry.getValue() > maxWeekMessages) {
                maxWeekMessages = entry.getValue();
                mostActiveWeek = entry.getKey();
            }
        }

        System.out.println("   Weekly breakdown calculated: " + weeklyBreakdown.size() + " weeks");
        System.out.println("   Most active week: Week " + mostActiveWeek);
    }

    /**
     * Identify patterns and trends in the data.
     */
    private void identifyTrends() {
        if (summaries.isEmpty()) {
            return;
        }

        // Initialize day of week counters
        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", 
                            "Thursday", "Friday", "Saturday"};
        for (String day : dayNames) {
            dayOfWeekActivity.put(day, 0);
        }

        Calendar cal = Calendar.getInstance();

        for (Summary summary : summaries) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date summaryDate = sdf.parse(summary.getDate());
                cal.setTime(summaryDate);

                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                String dayName = dayNames[dayOfWeek - 1];

                int dayMessages = getMessageCount(summary);
                dayOfWeekActivity.merge(dayName, dayMessages, Integer::sum);

            } catch (Exception e) {
                // Skip if date parsing fails
            }
        }

        // Find most active day of week
        int maxActivity = 0;
        for (Map.Entry<String, Integer> entry : dayOfWeekActivity.entrySet()) {
            if (entry.getValue() > maxActivity) {
                maxActivity = entry.getValue();
                mostActiveDay = entry.getKey();
            }
        }

        System.out.println("   Most active day of week: " + mostActiveDay);
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
     * Format the monthly report as a readable string.
     *
     * @return Formatted report text
     */
    public String formatReport() {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("   📅 MONTHLY REPORT: ").append(month).append("\n");
        sb.append("═══════════════════════════════════════════════════\n\n");

        // Statistics
        sb.append("📊 MONTHLY STATISTICS\n");
        sb.append("─────────────────────────────────────────────────\n");
        sb.append("Total Messages: ").append(String.format("%,d", totalMessages)).append("\n");
        sb.append("Days Covered: ").append(summaries.size()).append("\n");
        sb.append("Average per Day: ").append(String.format("%.1f", averageMessagesPerDay));
        sb.append(" messages\n\n");

        // Weekly Breakdown
        sb.append("📈 WEEKLY BREAKDOWN\n");
        sb.append("─────────────────────────────────────────────────\n");

        int maxWeeklyMessages = weeklyBreakdown.values().stream()
                .max(Integer::compare).orElse(1);

        for (int week = 1; week <= 5; week++) {
            int messages = weeklyBreakdown.getOrDefault(week, 0);
            if (messages > 0 || week <= 4) {
                String bar = createProgressBar(messages, maxWeeklyMessages);
                String marker = (week == mostActiveWeek) ? " ← Most Active" : "";
                sb.append("Week ").append(week).append(": ");
                sb.append(bar).append(" ").append(messages).append(" msgs");
                sb.append(marker).append("\n");
            }
        }
        sb.append("\n");

        // Day of Week Patterns
        sb.append("📆 PATTERNS IDENTIFIED\n");
        sb.append("─────────────────────────────────────────────────\n");
        sb.append("• Most active day of week: ").append(mostActiveDay).append("\n");
        sb.append("• Most active week: Week ").append(mostActiveWeek).append("\n");

        // Calculate trend description
        String trend = calculateTrendDescription();
        sb.append("• Trend: ").append(trend).append("\n\n");

        // Day of Week Activity
        sb.append("📊 ACTIVITY BY DAY OF WEEK\n");
        sb.append("─────────────────────────────────────────────────\n");

        int maxDayActivity = dayOfWeekActivity.values().stream()
                .max(Integer::compare).orElse(1);

        String[] orderedDays = {"Monday", "Tuesday", "Wednesday", "Thursday", 
                               "Friday", "Saturday", "Sunday"};
        for (String day : orderedDays) {
            int activity = dayOfWeekActivity.getOrDefault(day, 0);
            String bar = createProgressBar(activity, maxDayActivity);
            String shortDay = day.substring(0, 3);
            sb.append(shortDay).append(": ").append(bar);
            sb.append(" ").append(activity).append("\n");
        }
        sb.append("\n");

        // Top Topics
        sb.append("🔑 TOP TOPICS THIS MONTH\n");
        sb.append("─────────────────────────────────────────────────\n");
        if (topTopics.isEmpty()) {
            sb.append("No topics identified\n");
        } else {
            for (int i = 0; i < topTopics.size(); i++) {
                sb.append((i + 1)).append(". ").append(topTopics.get(i)).append("\n");
            }
        }
        sb.append("\n");

        // Action Items Summary
        sb.append("⚡ ACTION ITEMS\n");
        sb.append("─────────────────────────────────────────────────\n");
        sb.append("Total identified: ").append(allActionItems.size()).append("\n");
        if (!allActionItems.isEmpty()) {
            sb.append("\nRecent action items:\n");
            int limit = Math.min(5, allActionItems.size());
            for (int i = 0; i < limit; i++) {
                sb.append("☐ ").append(allActionItems.get(i)).append("\n");
            }
            if (allActionItems.size() > 5) {
                sb.append("... and ").append(allActionItems.size() - 5).append(" more\n");
            }
        }
        sb.append("\n");

        // Footer
        sb.append("═══════════════════════════════════════════════════\n");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sb.append("Generated: ").append(timestampFormat.format(generatedAt)).append("\n");
        sb.append("GhostWriter AI Monthly Report\n");
        sb.append("═══════════════════════════════════════════════════\n");

        return sb.toString();
    }

    /**
     * Calculate a description of the monthly trend.
     */
    private String calculateTrendDescription() {
        if (weeklyBreakdown.size() < 2) {
            return "Insufficient data for trend analysis";
        }

        // Get first and last week values
        int firstWeekMessages = weeklyBreakdown.getOrDefault(1, 0);
        int lastWeek = Collections.max(weeklyBreakdown.keySet());
        int lastWeekMessages = weeklyBreakdown.getOrDefault(lastWeek, 0);

        // Determine trend
        if (lastWeekMessages > firstWeekMessages * 1.2) {
            return "Activity increased throughout the month";
        } else if (lastWeekMessages < firstWeekMessages * 0.8) {
            return "Activity decreased throughout the month";
        } else if (mostActiveWeek == 2 || mostActiveWeek == 3) {
            return "Activity peaked mid-month";
        } else {
            return "Activity remained relatively stable";
        }
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
     * Save the monthly report to the database.
     */
    public void saveReport() {
        Database db = Database.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, String> data = new HashMap<>();
        data.put("report_id", reportId);
        data.put("user_id", userId);
        data.put("report_type", "monthly");
        data.put("month", month);
        data.put("start_date", sdf.format(startDate));
        data.put("end_date", sdf.format(endDate));
        data.put("total_messages", String.valueOf(totalMessages));
        data.put("average_per_day", String.format("%.2f", averageMessagesPerDay));
        data.put("top_topics", String.join("|||", topTopics));
        data.put("action_items_count", String.valueOf(allActionItems.size()));
        data.put("most_active_week", String.valueOf(mostActiveWeek));
        data.put("most_active_day", mostActiveDay);
        data.put("report_text", formatReport());
        data.put("created_at", timestamp.format(generatedAt));

        db.store("reports", data);

        System.out.println("💾 Monthly report saved to database");
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

    public String getMonth() {
        return month;
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

    public Map<Integer, Integer> getWeeklyBreakdown() {
        return new HashMap<>(weeklyBreakdown);
    }

    public List<String> getTopTopics() {
        return new ArrayList<>(topTopics);
    }

    public List<String> getAllActionItems() {
        return new ArrayList<>(allActionItems);
    }

    public int getMostActiveWeek() {
        return mostActiveWeek;
    }

    public String getMostActiveDay() {
        return mostActiveDay;
    }

    public double getAverageMessagesPerDay() {
        return averageMessagesPerDay;
    }

    public Map<String, Integer> getDayOfWeekActivity() {
        return new HashMap<>(dayOfWeekActivity);
    }

    public Date getGeneratedAt() {
        return new Date(generatedAt.getTime());
    }

    public int getDaysCovered() {
        return summaries.size();
    }

    @Override
    public String toString() {
        return String.format("MonthlyReport[%s, %d messages, %d days]",
                month, totalMessages, summaries.size());
    }
}
