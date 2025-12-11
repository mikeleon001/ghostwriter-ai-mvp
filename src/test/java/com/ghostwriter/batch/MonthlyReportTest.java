package com.ghostwriter.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Calendar;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MonthlyReport class.
 * 
 * MonthlyReport aggregates 30 days of summaries with trend analysis.
 * These tests verify date calculations, aggregation, and trend identification.
 * 
 * @author Mihail Chitorog
 */
@DisplayName("MonthlyReport Tests")
public class MonthlyReportTest {

    private static final String TEST_USER_ID = "test-user-123";

    // ============================================
    // GENERATE METHOD TESTS
    // ============================================

    @Test
    @DisplayName("generate should create report with correct user ID")
    void testGenerate_CreatesReportWithCorrectUserId() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertEquals(TEST_USER_ID, report.getUserId());
    }

    @Test
    @DisplayName("generate should create unique report ID")
    void testGenerate_CreatesUniqueReportId() {
        MonthlyReport report1 = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        MonthlyReport report2 = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        
        assertNotEquals(report1.getReportId(), report2.getReportId());
    }

    @Test
    @DisplayName("generate should set correct month name")
    void testGenerate_SetsCorrectMonthName() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertTrue(report.getMonth().contains("December"));
        assertTrue(report.getMonth().contains("2025"));
    }

    @Test
    @DisplayName("generate should calculate correct date range for month")
    void testGenerate_CalculatesCorrectDateRange() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(report.getStartDate());
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(report.getEndDate());
        
        // December should start on day 1
        assertEquals(1, startCal.get(Calendar.DAY_OF_MONTH));
        // December should end on day 31
        assertEquals(31, endCal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    @DisplayName("generate should handle February correctly")
    void testGenerate_HandlesFebruaryCorrectly() {
        // February 2024 (leap year)
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 2, 2024);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(report.getEndDate());
        
        // Leap year February has 29 days
        assertEquals(29, endCal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    @DisplayName("generate should record generation time")
    void testGenerate_RecordsGenerationTime() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertNotNull(report.getGeneratedAt());
    }

    // ============================================
    // INPUT VALIDATION TESTS
    // ============================================

    @Test
    @DisplayName("generate should throw exception for invalid month (0)")
    void testGenerate_ThrowsForMonthZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            MonthlyReport.generate(TEST_USER_ID, 0, 2025);
        });
    }

    @Test
    @DisplayName("generate should throw exception for invalid month (13)")
    void testGenerate_ThrowsForMonthThirteen() {
        assertThrows(IllegalArgumentException.class, () -> {
            MonthlyReport.generate(TEST_USER_ID, 13, 2025);
        });
    }

    @Test
    @DisplayName("generate should throw exception for negative month")
    void testGenerate_ThrowsForNegativeMonth() {
        assertThrows(IllegalArgumentException.class, () -> {
            MonthlyReport.generate(TEST_USER_ID, -1, 2025);
        });
    }

    @Test
    @DisplayName("generate should accept all valid months (1-12)")
    void testGenerate_AcceptsAllValidMonths() {
        for (int month = 1; month <= 12; month++) {
            MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, month, 2025);
            assertNotNull(report);
        }
    }

    // ============================================
    // INITIAL STATE TESTS
    // ============================================

    @Test
    @DisplayName("Report should initialize with non-null summaries list")
    void testReport_InitializesWithNonNullSummaries() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertNotNull(report.getSummaries());
    }

    @Test
    @DisplayName("Report should initialize with non-null topics list")
    void testReport_InitializesWithNonNullTopics() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertNotNull(report.getTopTopics());
    }

    @Test
    @DisplayName("Report should initialize with non-null weekly breakdown")
    void testReport_InitializesWithNonNullWeeklyBreakdown() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertNotNull(report.getWeeklyBreakdown());
    }

    @Test
    @DisplayName("Report should initialize with non-null day of week activity")
    void testReport_InitializesWithNonNullDayOfWeekActivity() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertNotNull(report.getDayOfWeekActivity());
    }

    // ============================================
    // FORMAT REPORT TESTS
    // ============================================

    @Test
    @DisplayName("formatReport should include header with MONTHLY REPORT")
    void testFormatReport_IncludesHeader() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("MONTHLY REPORT"));
    }

    @Test
    @DisplayName("formatReport should include month name")
    void testFormatReport_IncludesMonthName() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("December"));
    }

    @Test
    @DisplayName("formatReport should include statistics section")
    void testFormatReport_IncludesStatisticsSection() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("MONTHLY STATISTICS"));
        assertTrue(formatted.contains("Total Messages:"));
        assertTrue(formatted.contains("Days Covered:"));
        assertTrue(formatted.contains("Average per Day:"));
    }

    @Test
    @DisplayName("formatReport should include weekly breakdown section")
    void testFormatReport_IncludesWeeklyBreakdown() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("WEEKLY BREAKDOWN"));
    }

    @Test
    @DisplayName("formatReport should include patterns section")
    void testFormatReport_IncludesPatternsSection() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("PATTERNS IDENTIFIED"));
    }

    @Test
    @DisplayName("formatReport should include day of week activity section")
    void testFormatReport_IncludesDayOfWeekActivity() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("ACTIVITY BY DAY OF WEEK"));
    }

    @Test
    @DisplayName("formatReport should include topics section")
    void testFormatReport_IncludesTopicsSection() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("TOP TOPICS THIS MONTH"));
    }

    @Test
    @DisplayName("formatReport should include action items section")
    void testFormatReport_IncludesActionItemsSection() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("ACTION ITEMS"));
    }

    @Test
    @DisplayName("formatReport should include GhostWriter branding")
    void testFormatReport_IncludesBranding() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("GhostWriter AI"));
    }

    // ============================================
    // GETTER TESTS
    // ============================================

    @Test
    @DisplayName("getTotalMessages should return non-negative value")
    void testGetTotalMessages_ReturnsNonNegative() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertTrue(report.getTotalMessages() >= 0);
    }

    @Test
    @DisplayName("getAverageMessagesPerDay should return non-negative value")
    void testGetAverageMessagesPerDay_ReturnsNonNegative() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertTrue(report.getAverageMessagesPerDay() >= 0);
    }

    @Test
    @DisplayName("getDaysCovered should return value between 0 and 31")
    void testGetDaysCovered_ReturnsValidRange() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        int days = report.getDaysCovered();
        
        assertTrue(days >= 0 && days <= 31);
    }

    @Test
    @DisplayName("getMostActiveWeek should return value between 0 and 5")
    void testGetMostActiveWeek_ReturnsValidRange() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        int week = report.getMostActiveWeek();
        
        assertTrue(week >= 0 && week <= 5);
    }

    @Test
    @DisplayName("getMostActiveDay should return non-null value")
    void testGetMostActiveDay_ReturnsNonNull() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        assertNotNull(report.getMostActiveDay());
    }

    @Test
    @DisplayName("getStartDate should return defensive copy")
    void testGetStartDate_ReturnsDefensiveCopy() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        var date1 = report.getStartDate();
        var date2 = report.getStartDate();
        
        assertNotSame(date1, date2);
        assertEquals(date1, date2);
    }

    @Test
    @DisplayName("getWeeklyBreakdown should return defensive copy")
    void testGetWeeklyBreakdown_ReturnsDefensiveCopy() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        Map<Integer, Integer> map1 = report.getWeeklyBreakdown();
        Map<Integer, Integer> map2 = report.getWeeklyBreakdown();
        
        assertNotSame(map1, map2);
    }

    @Test
    @DisplayName("getDayOfWeekActivity should return defensive copy")
    void testGetDayOfWeekActivity_ReturnsDefensiveCopy() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        Map<String, Integer> map1 = report.getDayOfWeekActivity();
        Map<String, Integer> map2 = report.getDayOfWeekActivity();
        
        assertNotSame(map1, map2);
    }

    // ============================================
    // TO STRING TESTS
    // ============================================

    @Test
    @DisplayName("toString should include month name")
    void testToString_IncludesMonthName() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String str = report.toString();
        
        assertTrue(str.contains("MonthlyReport"));
        assertTrue(str.contains("December"));
    }

    @Test
    @DisplayName("toString should include message count")
    void testToString_IncludesMessageCount() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String str = report.toString();
        
        assertTrue(str.contains("messages"));
    }

    @Test
    @DisplayName("toString should include days count")
    void testToString_IncludesDaysCount() {
        MonthlyReport report = MonthlyReport.generate(TEST_USER_ID, 12, 2025);
        String str = report.toString();
        
        assertTrue(str.contains("days"));
    }
}
