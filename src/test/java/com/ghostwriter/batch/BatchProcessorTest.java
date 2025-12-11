package com.ghostwriter.batch;

import com.ghostwriter.analysis.Analyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BatchProcessor class.
 * 
 * BatchProcessor handles processing of multiple conversation files.
 * These tests verify file validation, processing logic, and error handling.
 * 
 * @author Mihail Chitorog
 */
@DisplayName("BatchProcessor Tests")
public class BatchProcessorTest {

    private BatchProcessor processor;
    private static final String TEST_USER_ID = "test-user-123";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        processor = new BatchProcessor(TEST_USER_ID);
    }

    // ============================================
    // CONSTRUCTOR TESTS
    // ============================================

    @Test
    @DisplayName("Constructor should set user ID")
    void testConstructor_SetsUserId() {
        BatchProcessor bp = new BatchProcessor("user-abc");
        assertEquals("user-abc", bp.getUserId());
    }

    @Test
    @DisplayName("Constructor should create default analyzer")
    void testConstructor_CreatesDefaultAnalyzer() {
        assertNotNull(processor.getAnalyzer());
    }

    @Test
    @DisplayName("Constructor with custom analyzer should use that analyzer")
    void testConstructor_WithCustomAnalyzer() {
        Analyzer customAnalyzer = new Analyzer();
        BatchProcessor bp = new BatchProcessor("user", customAnalyzer);
        
        assertSame(customAnalyzer, bp.getAnalyzer());
    }

    // ============================================
    // PROCESS FILES - EMPTY/NULL INPUT TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should handle null input")
    void testProcessFiles_HandlesNullInput() {
        BatchResult result = processor.processFiles(null);
        
        assertNotNull(result);
        assertEquals(0, result.getTotalFiles());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("processFiles should handle empty list")
    void testProcessFiles_HandlesEmptyList() {
        BatchResult result = processor.processFiles(new ArrayList<>());
        
        assertNotNull(result);
        assertEquals(0, result.getTotalFiles());
        assertTrue(result.isSuccess());
    }

    // ============================================
    // FILE VALIDATION TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should fail for non-existent file")
    void testProcessFiles_FailsForNonExistentFile() {
        File nonExistent = new File("/path/to/nonexistent.txt");
        List<File> files = Arrays.asList(nonExistent);
        
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getFailureCount());
        assertEquals(0, result.getSuccessCount());
        assertTrue(result.getFailedFiles().containsKey("nonexistent.txt"));
    }

    @Test
    @DisplayName("processFiles should fail for unsupported file extension")
    void testProcessFiles_FailsForUnsupportedExtension() throws IOException {
        // Create a .pdf file (unsupported)
        Path pdfFile = tempDir.resolve("document.pdf");
        Files.write(pdfFile, "PDF content".getBytes());
        
        List<File> files = Arrays.asList(pdfFile.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailedFiles().get("document.pdf").contains("Unsupported"));
    }

    @Test
    @DisplayName("processFiles should fail for empty file")
    void testProcessFiles_FailsForEmptyFile() throws IOException {
        // Create an empty .txt file
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, new byte[0]);
        
        List<File> files = Arrays.asList(emptyFile.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailedFiles().get("empty.txt").contains("empty"));
    }

    @Test
    @DisplayName("processFiles should fail for directory instead of file")
    void testProcessFiles_FailsForDirectory() throws IOException {
        // Create a directory
        Path directory = tempDir.resolve("subdir");
        Files.createDirectory(directory);
        
        List<File> files = Arrays.asList(directory.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailedFiles().get("subdir").contains("Not a file"));
    }

    // ============================================
    // VALID FILE PROCESSING TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should process valid WhatsApp file")
    void testProcessFiles_ProcessesValidWhatsAppFile() throws IOException {
        // Create a valid WhatsApp format file
        String whatsappContent = 
            "[12/10/24, 2:31 PM] Alice: Hello there!\n" +
            "[12/10/24, 2:32 PM] Bob: Hi Alice!\n" +
            "[12/10/24, 2:33 PM] Alice: How are you?\n";
        
        Path chatFile = tempDir.resolve("chat.txt");
        Files.write(chatFile, whatsappContent.getBytes());
        
        List<File> files = Arrays.asList(chatFile.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(1, result.getSummariesGenerated().size());
    }

    @Test
    @DisplayName("processFiles should process multiple valid files")
    void testProcessFiles_ProcessesMultipleFiles() throws IOException {
        // Create multiple valid WhatsApp files
        String content1 = "[12/10/24, 2:31 PM] Mom: Call me later\n";
        String content2 = "[12/11/24, 3:00 PM] Boss: Meeting at 5\n";
        
        Path file1 = tempDir.resolve("chat_mom.txt");
        Path file2 = tempDir.resolve("chat_boss.txt");
        
        Files.write(file1, content1.getBytes());
        Files.write(file2, content2.getBytes());
        
        List<File> files = Arrays.asList(file1.toFile(), file2.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(2, result.getSummariesGenerated().size());
    }

    @Test
    @DisplayName("processFiles should handle mixed valid and invalid files")
    void testProcessFiles_HandlesMixedFiles() throws IOException {
        // Create one valid and one invalid file
        String validContent = "[12/10/24, 2:31 PM] Alice: Hello!\n";
        
        Path validFile = tempDir.resolve("valid.txt");
        Path invalidFile = tempDir.resolve("invalid.pdf");
        
        Files.write(validFile, validContent.getBytes());
        Files.write(invalidFile, "PDF content".getBytes());
        
        List<File> files = Arrays.asList(validFile.toFile(), invalidFile.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(50.0, result.getSuccessRate(), 0.01);
    }

    // ============================================
    // CONTENT VALIDATION TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should fail for file with no valid messages")
    void testProcessFiles_FailsForNoValidMessages() throws IOException {
        // Create a file with content that doesn't match WhatsApp format
        String invalidContent = "This is just plain text\nNo message format here\n";
        
        Path file = tempDir.resolve("plain.txt");
        Files.write(file, invalidContent.getBytes());
        
        List<File> files = Arrays.asList(file.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getFailureCount());
        assertTrue(result.getFailedFiles().get("plain.txt").contains("No valid messages"));
    }

    // ============================================
    // DATE EXTRACTION TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should extract date from filename with date")
    void testProcessFiles_ExtractsDateFromFilename() throws IOException {
        String content = "[12/10/24, 2:31 PM] Alice: Hello!\n";
        
        // Filename contains date
        Path file = tempDir.resolve("chat_2025-12-10.txt");
        Files.write(file, content.getBytes());
        
        List<File> files = Arrays.asList(file.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getSuccessCount());
        // The summary should use the date from filename
    }

    // ============================================
    // BATCH RESULT COMPLETENESS TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should mark batch as complete")
    void testProcessFiles_MarksBatchComplete() throws IOException {
        String content = "[12/10/24, 2:31 PM] Alice: Hello!\n";
        Path file = tempDir.resolve("chat.txt");
        Files.write(file, content.getBytes());
        
        List<File> files = Arrays.asList(file.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertNotNull(result.getEndTime());
        assertTrue(result.getDuration() >= 0);
    }

    @Test
    @DisplayName("processFiles should record all filenames in result")
    void testProcessFiles_RecordsAllFilenames() throws IOException {
        String content = "[12/10/24, 2:31 PM] Alice: Hello!\n";
        
        Path file1 = tempDir.resolve("chat1.txt");
        Path file2 = tempDir.resolve("chat2.txt");
        Files.write(file1, content.getBytes());
        Files.write(file2, content.getBytes());
        
        List<File> files = Arrays.asList(file1.toFile(), file2.toFile());
        BatchResult result = processor.processFiles(files);
        
        List<String> processed = result.getProcessedFiles();
        assertTrue(processed.contains("chat1.txt"));
        assertTrue(processed.contains("chat2.txt"));
    }

    // ============================================
    // SPECIAL CHARACTERS TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should handle messages with emojis")
    void testProcessFiles_HandlesEmojis() throws IOException {
        String content = "[12/10/24, 2:31 PM] Alice: Hello! 😀🎉\n" +
                        "[12/10/24, 2:32 PM] Bob: Great! 👍\n";
        
        Path file = tempDir.resolve("emoji_chat.txt");
        Files.write(file, content.getBytes());
        
        List<File> files = Arrays.asList(file.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getSuccessCount());
    }

    @Test
    @DisplayName("processFiles should handle messages with special characters")
    void testProcessFiles_HandlesSpecialCharacters() throws IOException {
        String content = "[12/10/24, 2:31 PM] Alice: Price is $50 & tax!\n" +
                        "[12/10/24, 2:32 PM] Bob: That's <great> news!\n";
        
        Path file = tempDir.resolve("special_chat.txt");
        Files.write(file, content.getBytes());
        
        List<File> files = Arrays.asList(file.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getSuccessCount());
    }

    // ============================================
    // MULTI-LINE MESSAGE TESTS
    // ============================================

    @Test
    @DisplayName("processFiles should handle multi-line messages")
    void testProcessFiles_HandlesMultiLineMessages() throws IOException {
        String content = "[12/10/24, 2:31 PM] Alice: This is a long message\n" +
                        "that spans multiple lines\n" +
                        "and keeps going\n" +
                        "[12/10/24, 2:32 PM] Bob: Got it!\n";
        
        Path file = tempDir.resolve("multiline.txt");
        Files.write(file, content.getBytes());
        
        List<File> files = Arrays.asList(file.toFile());
        BatchResult result = processor.processFiles(files);
        
        assertEquals(1, result.getSuccessCount());
    }

    // ============================================
    // GETTER TESTS
    // ============================================

    @Test
    @DisplayName("getUserId should return correct user ID")
    void testGetUserId_ReturnsCorrectId() {
        assertEquals(TEST_USER_ID, processor.getUserId());
    }

    @Test
    @DisplayName("getAnalyzer should return non-null analyzer")
    void testGetAnalyzer_ReturnsNonNull() {
        assertNotNull(processor.getAnalyzer());
    }
}
