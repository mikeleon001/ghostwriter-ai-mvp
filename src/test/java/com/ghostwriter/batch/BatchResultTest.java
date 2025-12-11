package com.ghostwriter.batch;

import com.ghostwriter.analysis.Summary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BatchResult class.
 * 
 * BatchResult is a data container that tracks the results of batch processing.
 * These tests verify that it correctly tracks successes, failures, and statistics.
 * 
 * @author Mihail Chitorog
 */
@DisplayName("BatchResult Tests")
public class BatchResultTest {

    private BatchResult batchResult;

    @BeforeEach
    void setUp() {
        // Create a fresh BatchResult for each test
        batchResult = new BatchResult(5);
    }

    // ============================================
    // CONSTRUCTOR TESTS
    // ============================================

    @Test
    @DisplayName("Constructor should initialize with correct total files")
    void testConstructor_InitializesTotalFiles() {
        BatchResult result = new BatchResult(10);
        assertEquals(10, result.getTotalFiles());
    }

    @Test
    @DisplayName("Constructor should initialize counts to zero")
    void testConstructor_InitializesCountsToZero() {
        assertEquals(0, batchResult.getSuccessCount());
        assertEquals(0, batchResult.getFailureCount());
    }

    @Test
    @DisplayName("Constructor should generate unique batch ID")
    void testConstructor_GeneratesUniqueBatchId() {
        BatchResult result1 = new BatchResult(5);
        BatchResult result2 = new BatchResult(5);
        
        assertNotNull(result1.getBatchId());
        assertNotNull(result2.getBatchId());
        assertNotEquals(result1.getBatchId(), result2.getBatchId());
    }

    @Test
    @DisplayName("Constructor should initialize empty lists and maps")
    void testConstructor_InitializesEmptyCollections() {
        assertTrue(batchResult.getProcessedFiles().isEmpty());
        assertTrue(batchResult.getFailedFiles().isEmpty());
        assertTrue(batchResult.getSummariesGenerated().isEmpty());
    }

    @Test
    @DisplayName("Constructor should record start time")
    void testConstructor_RecordsStartTime() {
        assertNotNull(batchResult.getStartTime());
    }

    // ============================================
    // ADD SUCCESS TESTS
    // ============================================

    @Test
    @DisplayName("addSuccess should increment success count")
    void testAddSuccess_IncrementsCount() {
        batchResult.addSuccess("file1.txt", createMockSummary());
        assertEquals(1, batchResult.getSuccessCount());
        
        batchResult.addSuccess("file2.txt", createMockSummary());
        assertEquals(2, batchResult.getSuccessCount());
    }

    @Test
    @DisplayName("addSuccess should add filename to processed files")
    void testAddSuccess_AddsToProcessedFiles() {
        batchResult.addSuccess("chat_mom.txt", createMockSummary());
        
        List<String> processed = batchResult.getProcessedFiles();
        assertEquals(1, processed.size());
        assertTrue(processed.contains("chat_mom.txt"));
    }

    @Test
    @DisplayName("addSuccess should add summary to list")
    void testAddSuccess_AddsSummaryToList() {
        Summary summary = createMockSummary();
        batchResult.addSuccess("file.txt", summary);
        
        List<Summary> summaries = batchResult.getSummariesGenerated();
        assertEquals(1, summaries.size());
    }

    // ============================================
    // ADD FAILURE TESTS
    // ============================================

    @Test
    @DisplayName("addFailure should increment failure count")
    void testAddFailure_IncrementsCount() {
        batchResult.addFailure("bad_file.txt", "Invalid format");
        assertEquals(1, batchResult.getFailureCount());
        
        batchResult.addFailure("another_bad.txt", "File too large");
        assertEquals(2, batchResult.getFailureCount());
    }

    @Test
    @DisplayName("addFailure should record filename and reason")
    void testAddFailure_RecordsFilenameAndReason() {
        batchResult.addFailure("corrupt.txt", "File corrupted");
        
        Map<String, String> failed = batchResult.getFailedFiles();
        assertEquals(1, failed.size());
        assertEquals("File corrupted", failed.get("corrupt.txt"));
    }

    // ============================================
    // IS SUCCESS TESTS
    // ============================================

    @Test
    @DisplayName("isSuccess should return true when all files processed successfully")
    void testIsSuccess_TrueWhenAllSuccessful() {
        BatchResult result = new BatchResult(2);
        result.addSuccess("file1.txt", createMockSummary());
        result.addSuccess("file2.txt", createMockSummary());
        
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess should return false when any file fails")
    void testIsSuccess_FalseWhenAnyFailed() {
        BatchResult result = new BatchResult(3);
        result.addSuccess("file1.txt", createMockSummary());
        result.addSuccess("file2.txt", createMockSummary());
        result.addFailure("file3.txt", "Error");
        
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess should return false when not all files processed")
    void testIsSuccess_FalseWhenIncomplete() {
        BatchResult result = new BatchResult(5);
        result.addSuccess("file1.txt", createMockSummary());
        result.addSuccess("file2.txt", createMockSummary());
        // Only 2 out of 5 processed
        
        assertFalse(result.isSuccess());
    }

    // ============================================
    // SUCCESS RATE TESTS
    // ============================================

    @Test
    @DisplayName("getSuccessRate should return 100 when all successful")
    void testGetSuccessRate_Returns100WhenAllSuccessful() {
        BatchResult result = new BatchResult(4);
        result.addSuccess("f1.txt", createMockSummary());
        result.addSuccess("f2.txt", createMockSummary());
        result.addSuccess("f3.txt", createMockSummary());
        result.addSuccess("f4.txt", createMockSummary());
        
        assertEquals(100.0, result.getSuccessRate(), 0.01);
    }

    @Test
    @DisplayName("getSuccessRate should return 0 when none successful")
    void testGetSuccessRate_Returns0WhenNoneSuccessful() {
        BatchResult result = new BatchResult(2);
        result.addFailure("f1.txt", "Error 1");
        result.addFailure("f2.txt", "Error 2");
        
        assertEquals(0.0, result.getSuccessRate(), 0.01);
    }

    @Test
    @DisplayName("getSuccessRate should return correct percentage")
    void testGetSuccessRate_ReturnsCorrectPercentage() {
        BatchResult result = new BatchResult(4);
        result.addSuccess("f1.txt", createMockSummary());
        result.addSuccess("f2.txt", createMockSummary());
        result.addFailure("f3.txt", "Error");
        result.addFailure("f4.txt", "Error");
        
        assertEquals(50.0, result.getSuccessRate(), 0.01);
    }

    @Test
    @DisplayName("getSuccessRate should return 0 when total files is 0")
    void testGetSuccessRate_Returns0WhenNoFiles() {
        BatchResult result = new BatchResult(0);
        assertEquals(0.0, result.getSuccessRate(), 0.01);
    }

    // ============================================
    // DURATION TESTS
    // ============================================

    @Test
    @DisplayName("getDuration should return -1 when not complete")
    void testGetDuration_ReturnsNegativeWhenNotComplete() {
        assertEquals(-1, batchResult.getDuration());
    }

    @Test
    @DisplayName("getDuration should return positive value when complete")
    void testGetDuration_ReturnsPositiveWhenComplete() throws InterruptedException {
        Thread.sleep(10); // Small delay
        batchResult.markComplete();
        
        assertTrue(batchResult.getDuration() >= 0);
    }

    @Test
    @DisplayName("markComplete should record end time")
    void testMarkComplete_RecordsEndTime() {
        assertNull(batchResult.getEndTime());
        
        batchResult.markComplete();
        
        assertNotNull(batchResult.getEndTime());
    }

    // ============================================
    // FORMAT TESTS
    // ============================================

    @Test
    @DisplayName("formatResult should include batch statistics")
    void testFormatResult_IncludesStatistics() {
        batchResult.addSuccess("file1.txt", createMockSummary());
        batchResult.addFailure("file2.txt", "Error");
        batchResult.markComplete();
        
        String formatted = batchResult.formatResult();
        
        assertTrue(formatted.contains("Total Files: 5"));
        assertTrue(formatted.contains("Successful: 1"));
        assertTrue(formatted.contains("Failed: 1"));
    }

    @Test
    @DisplayName("formatResult should include processed files")
    void testFormatResult_IncludesProcessedFiles() {
        batchResult.addSuccess("chat_mom.txt", createMockSummary());
        batchResult.markComplete();
        
        String formatted = batchResult.formatResult();
        
        assertTrue(formatted.contains("chat_mom.txt"));
        assertTrue(formatted.contains("PROCESSED FILES"));
    }

    @Test
    @DisplayName("formatResult should include failed files with reasons")
    void testFormatResult_IncludesFailedFilesWithReasons() {
        batchResult.addFailure("bad.txt", "Invalid format");
        batchResult.markComplete();
        
        String formatted = batchResult.formatResult();
        
        assertTrue(formatted.contains("bad.txt"));
        assertTrue(formatted.contains("Invalid format"));
        assertTrue(formatted.contains("FAILED FILES"));
    }

    @Test
    @DisplayName("getFormattedDuration should show 'In progress' when not complete")
    void testGetFormattedDuration_ShowsInProgressWhenNotComplete() {
        String duration = batchResult.getFormattedDuration();
        assertEquals("In progress...", duration);
    }

    // ============================================
    // GETTER IMMUTABILITY TESTS
    // ============================================

    @Test
    @DisplayName("getProcessedFiles should return a copy (immutable)")
    void testGetProcessedFiles_ReturnsCopy() {
        batchResult.addSuccess("file.txt", createMockSummary());
        
        List<String> list1 = batchResult.getProcessedFiles();
        List<String> list2 = batchResult.getProcessedFiles();
        
        assertNotSame(list1, list2);
    }

    @Test
    @DisplayName("getFailedFiles should return a copy (immutable)")
    void testGetFailedFiles_ReturnsCopy() {
        batchResult.addFailure("file.txt", "Error");
        
        Map<String, String> map1 = batchResult.getFailedFiles();
        Map<String, String> map2 = batchResult.getFailedFiles();
        
        assertNotSame(map1, map2);
    }

    // ============================================
    // TO STRING TEST
    // ============================================

    @Test
    @DisplayName("toString should include key information")
    void testToString_IncludesKeyInfo() {
        batchResult.addSuccess("file.txt", createMockSummary());
        batchResult.addFailure("bad.txt", "Error");
        
        String str = batchResult.toString();
        
        assertTrue(str.contains("total=5"));
        assertTrue(str.contains("success=1"));
        assertTrue(str.contains("failed=1"));
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Creates a mock Summary for testing.
     */
    private Summary createMockSummary() {
        return new Summary();
    }
}
