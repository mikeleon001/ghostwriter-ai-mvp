package com.ghostwriter.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeeklyReport class.
 * 
 * WeeklyReport aggregates 7 days of daily summaries into one report.
 * These tests verify date calculations, aggregation, and formatting.
 * 
 * @author Mihail Chitorog
 */
@DisplayName("WeeklyReport Tests")
public class WeeklyReportTest {

    private static final String TEST_USER_ID = "test-user-123";

    // ============================================
    // GENERATE METHOD TESTS
    // ============================================

    @Test
    @DisplayName("generate should create report with correct user ID")
    void testGenerate_CreatesReportWithCorrectUserId() {
        Date endDate = new Date();
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, endDate);
        
        assertEquals(TEST_USER_ID, report.getUserId());
    }

    @Test
    @DisplayName("generate should create unique report ID")
    void testGenerate_CreatesUniqueReportId() {
        Date endDate = new Date();
        WeeklyReport report1 = WeeklyReport.generate(TEST_USER_ID, endDate);
        WeeklyReport report2 = WeeklyReport.generate(TEST_USER_ID, endDate);
        
        assertNotEquals(report1.getReportId(), report2.getReportId());
    }

    @Test
    @DisplayName("generate should calculate correct 7-day date range")
    void testGenerate_CalculatesCorrectDateRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.DECEMBER, 10);
        Date endDate = cal.getTime();
        
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, endDate);
        
        // Verify 7-day span
        long diffMs = report.getEndDate().getTime() - report.getStartDate().getTime();
        long diffDays = diffMs / (1000 * 60 * 60 * 24);
        
        assertEquals(6, diffDays); // 6 days difference = 7 days inclusive
    }

    @Test
    @DisplayName("generate should record generation time")
    void testGenerate_RecordsGenerationTime() {
        Date beforeGeneration = new Date();
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        
        assertNotNull(report.getGeneratedAt());
        assertTrue(report.getGeneratedAt().getTime() >= beforeGeneration.getTime());
    }

    // ============================================
    // INITIAL STATE TESTS
    // ============================================

    @Test
    @DisplayName("Report should initialize with non-null summaries list")
    void testReport_InitializesWithNonNullSummaries() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        assertNotNull(report.getSummaries());
    }

    @Test
    @DisplayName("Report should initialize with non-null topics list")
    void testReport_InitializesWithNonNullTopics() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        assertNotNull(report.getTopTopics());
    }

    @Test
    @DisplayName("Report should initialize with non-null action items list")
    void testReport_InitializesWithNonNullActionItems() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        assertNotNull(report.getAllActionItems());
    }

    @Test
    @DisplayName("Report should initialize with non-null pending questions list")
    void testReport_InitializesWithNonNullPendingQuestions() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        assertNotNull(report.getAllPendingQuestions());
    }

    // ============================================
    // FORMAT REPORT TESTS
    // ============================================

    @Test
    @DisplayName("formatReport should include header with WEEKLY REPORT")
    void testFormatReport_IncludesHeader() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("WEEKLY REPORT"));
    }

    @Test
    @DisplayName("formatReport should include statistics section")
    void testFormatReport_IncludesStatisticsSection() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("WEEKLY STATISTICS"));
        assertTrue(formatted.contains("Total Messages:"));
        assertTrue(formatted.contains("Days Covered:"));
    }

    @Test
    @DisplayName("formatReport should include topics section")
    void testFormatReport_IncludesTopicsSection() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("TOP TOPICS THIS WEEK"));
    }

    @Test
    @DisplayName("formatReport should include action items section")
    void testFormatReport_IncludesActionItemsSection() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("ACTION ITEMS"));
    }

    @Test
    @DisplayName("formatReport should include daily breakdown section")
    void testFormatReport_IncludesDailyBreakdown() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("DAILY BREAKDOWN"));
    }

    @Test
    @DisplayName("formatReport should include generation timestamp")
    void testFormatReport_IncludesGenerationTimestamp() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("Generated:"));
    }

    @Test
    @DisplayName("formatReport should include GhostWriter branding")
    void testFormatReport_IncludesBranding() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String formatted = report.formatReport();
        
        assertTrue(formatted.contains("GhostWriter AI"));
    }

    // ============================================
    // GETTER TESTS
    // ============================================

    @Test
    @DisplayName("getTotalMessages should return non-negative value")
    void testGetTotalMessages_ReturnsNonNegative() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        assertTrue(report.getTotalMessages() >= 0);
    }

    @Test
    @DisplayName("getDaysCovered should return value between 0 and 7")
    void testGetDaysCovered_ReturnsValidRange() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        int days = report.getDaysCovered();
        
        assertTrue(days >= 0 && days <= 7);
    }

    @Test
    @DisplayName("getMostActiveDay should return non-null value")
    void testGetMostActiveDay_ReturnsNonNull() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        assertNotNull(report.getMostActiveDay());
    }

    @Test
    @DisplayName("getStartDate should return defensive copy")
    void testGetStartDate_ReturnsDefensiveCopy() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        Date date1 = report.getStartDate();
        Date date2 = report.getStartDate();
        
        assertNotSame(date1, date2);
        assertEquals(date1, date2);
    }

    @Test
    @DisplayName("getEndDate should return defensive copy")
    void testGetEndDate_ReturnsDefensiveCopy() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        Date date1 = report.getEndDate();
        Date date2 = report.getEndDate();
        
        assertNotSame(date1, date2);
        assertEquals(date1, date2);
    }

    @Test
    @DisplayName("getSummaries should return defensive copy")
    void testGetSummaries_ReturnsDefensiveCopy() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        var list1 = report.getSummaries();
        var list2 = report.getSummaries();
        
        assertNotSame(list1, list2);
    }

    // ============================================
    // TO STRING TESTS
    // ============================================

    @Test
    @DisplayName("toString should include date range")
    void testToString_IncludesDateRange() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String str = report.toString();
        
        assertTrue(str.contains("WeeklyReport"));
        assertTrue(str.contains("to"));
    }

    @Test
    @DisplayName("toString should include message count")
    void testToString_IncludesMessageCount() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String str = report.toString();
        
        assertTrue(str.contains("messages"));
    }

    @Test
    @DisplayName("toString should include days count")
    void testToString_IncludesDaysCount() {
        WeeklyReport report = WeeklyReport.generate(TEST_USER_ID, new Date());
        String str = report.toString();
        
        assertTrue(str.contains("days"));
    }
}
