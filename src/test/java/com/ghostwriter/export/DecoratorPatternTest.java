package com.ghostwriter.export;

import com.ghostwriter.analysis.Summary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Decorator design pattern implementation.
 * Verifies that all formatters work correctly and produce valid output.
 * 
 * @author Mihail Chitorog
 */
public class DecoratorPatternTest {
    
    private Summary testSummary;
    private static final String TEST_OUTPUT_DIR = "./test_exports";
    
    @BeforeEach
    public void setUp() {
        // Create a test summary with sample data
        List<String> topics = Arrays.asList("project", "deadline", "meeting");
        List<String> actions = Arrays.asList("Finish report by Friday", "Schedule follow-up meeting");
        List<String> questions = Arrays.asList("What time is the meeting?", "Who will present?");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_messages", 30);
        stats.put("most_active", "Alice");
        stats.put("avg_message_length", 45);
        
        testSummary = new Summary(
            "test-decorator-" + UUID.randomUUID().toString(),
            "test-user-id",
            "test-conversation-id",
            "2025-12-10",
            topics,
            actions,
            questions,
            stats
        );
        
        // Create test output directory
        new File(TEST_OUTPUT_DIR).mkdirs();
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up test files
        File dir = new File(TEST_OUTPUT_DIR);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dir.delete();
        }
    }
    
    // ========== Plain Text Formatter Tests ==========
    
    @Test
    public void testPlainTextFormatter() {
        PlainTextFormatter formatter = new PlainTextFormatter();
        
        String output = formatter.format(testSummary);
        
        assertNotNull(output);
        assertTrue(output.contains("DAILY SUMMARY"));
        assertTrue(output.contains("2025-12-10"));
        assertTrue(output.contains("MESSAGE STATISTICS"));
        assertTrue(output.contains("KEY TOPICS"));
        assertTrue(output.contains("project"));
        assertEquals(".txt", formatter.getFileExtension());
    }
    
    @Test
    public void testPlainTextFormatterWithEmptyTopics() {
        Summary emptySummary = new Summary(
            "empty-test",
            "user-id",
            "conv-id",
            "2025-12-10",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashMap<>()
        );
        
        PlainTextFormatter formatter = new PlainTextFormatter();
        String output = formatter.format(emptySummary);
        
        assertTrue(output.contains("No specific topics identified"));
    }
    
    // ========== Markdown Decorator Tests ==========
    
    @Test
    public void testMarkdownDecorator() {
        PlainTextFormatter base = new PlainTextFormatter();
        MarkdownDecorator markdown = new MarkdownDecorator(base);
        
        String output = markdown.format(testSummary);
        
        assertNotNull(output);
        assertTrue(output.contains("# Daily Summary"));
        assertTrue(output.contains("## 📊 Message Statistics"));
        assertTrue(output.contains("## 🔑 Key Topics"));
        assertTrue(output.contains("- [ ]")); // Checkbox for action items
        assertTrue(output.contains("**Total Messages:**"));
        assertEquals(".md", markdown.getFileExtension());
    }
    
    @Test
    public void testMarkdownDecoratorNullWrapper() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MarkdownDecorator(null);
        });
    }
    
    // ========== HTML Decorator Tests ==========
    
    @Test
    public void testHTMLDecorator() {
        PlainTextFormatter base = new PlainTextFormatter();
        HTMLDecorator html = new HTMLDecorator(base);
        
        String output = html.format(testSummary);
        
        assertNotNull(output);
        assertTrue(output.contains("<!DOCTYPE html>"));
        assertTrue(output.contains("<html"));
        assertTrue(output.contains("<head>"));
        assertTrue(output.contains("<body>"));
        assertTrue(output.contains("<h1>"));
        assertTrue(output.contains("<h2>"));
        assertTrue(output.contains("Daily Summary"));
        assertTrue(output.contains("</html>"));
        assertEquals(".html", html.getFileExtension());
    }
    
    @Test
    public void testHTMLDecoratorEscaping() {
        // Create summary with special HTML characters
        List<String> topics = Arrays.asList("<script>alert('test')</script>", "topic & more");
        Summary specialSummary = new Summary(
            "special-test",
            "user-id",
            "conv-id",
            "2025-12-10",
            topics,
            new ArrayList<>(),
            new ArrayList<>(),
            new HashMap<>()
        );
        
        HTMLDecorator html = new HTMLDecorator(new PlainTextFormatter());
        String output = html.format(specialSummary);
        
        // Should escape HTML special characters
        assertTrue(output.contains("&lt;script&gt;") || output.contains("&amp;"));
        assertFalse(output.contains("<script>alert"));
    }
    
    @Test
    public void testHTMLDecoratorNullWrapper() {
        assertThrows(IllegalArgumentException.class, () -> {
            new HTMLDecorator(null);
        });
    }
    
    // ========== JSON Decorator Tests ==========
    
    @Test
    public void testJSONDecorator() {
        PlainTextFormatter base = new PlainTextFormatter();
        JSONDecorator json = new JSONDecorator(base);
        
        String output = json.format(testSummary);
        
        assertNotNull(output);
        assertTrue(output.contains("\"summaryId\""));
        assertTrue(output.contains("\"date\""));
        assertTrue(output.contains("\"statistics\""));
        assertTrue(output.contains("\"keyTopics\""));
        assertTrue(output.contains("\"actionItems\""));
        assertTrue(output.contains("\"metadata\""));
        assertTrue(output.contains("2025-12-10"));
        assertEquals(".json", json.getFileExtension());
    }
    
    @Test
    public void testJSONDecoratorEscaping() {
        // Create summary with special JSON characters
        List<String> actions = Arrays.asList("Say \"hello\" to everyone", "Path: C:\\Users\\test");
        Summary specialSummary = new Summary(
            "json-special-test",
            "user-id",
            "conv-id",
            "2025-12-10",
            new ArrayList<>(),
            actions,
            new ArrayList<>(),
            new HashMap<>()
        );
        
        JSONDecorator json = new JSONDecorator(new PlainTextFormatter());
        String output = json.format(specialSummary);
        
        // Should escape special characters
        assertTrue(output.contains("\\\"") || output.contains("\\\\"));
    }
    
    @Test
    public void testJSONDecoratorNullWrapper() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JSONDecorator(null);
        });
    }
    
    // ========== Summary Exporter Tests ==========
    
    @Test
    public void testExportSingleFormat() throws IOException {
        SummaryExporter exporter = new SummaryExporter();
        PlainTextFormatter formatter = new PlainTextFormatter();
        
        String filepath = exporter.export(testSummary, formatter, TEST_OUTPUT_DIR);
        
        assertNotNull(filepath);
        File exportedFile = new File(filepath);
        assertTrue(exportedFile.exists());
        assertTrue(exportedFile.getName().endsWith(".txt"));
        
        // Verify content
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        assertTrue(content.contains("DAILY SUMMARY"));
    }
    
    @Test
    public void testExportToAllFormats() throws IOException {
        SummaryExporter exporter = new SummaryExporter();
        
        exporter.exportToAllFormats(testSummary, TEST_OUTPUT_DIR);
        
        // Verify all 4 files were created
        File dir = new File(TEST_OUTPUT_DIR);
        File[] files = dir.listFiles();
        
        assertNotNull(files);
        assertEquals(4, files.length);
        
        // Check for each format
        boolean hasTxt = false, hasMd = false, hasHtml = false, hasJson = false;
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".txt")) hasTxt = true;
            if (name.endsWith(".md")) hasMd = true;
            if (name.endsWith(".html")) hasHtml = true;
            if (name.endsWith(".json")) hasJson = true;
        }
        
        assertTrue(hasTxt, "Should have .txt file");
        assertTrue(hasMd, "Should have .md file");
        assertTrue(hasHtml, "Should have .html file");
        assertTrue(hasJson, "Should have .json file");
    }
    
    @Test
    public void testExportAsMarkdown() throws IOException {
        SummaryExporter exporter = new SummaryExporter();
        
        String filepath = exporter.exportAs(testSummary, "markdown", TEST_OUTPUT_DIR);
        
        assertNotNull(filepath);
        assertTrue(filepath.endsWith(".md"));
        
        File file = new File(filepath);
        assertTrue(file.exists());
        
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        assertTrue(content.contains("# Daily Summary"));
    }
    
    @Test
    public void testExportAsHTML() throws IOException {
        SummaryExporter exporter = new SummaryExporter();
        
        String filepath = exporter.exportAs(testSummary, "html", TEST_OUTPUT_DIR);
        
        assertNotNull(filepath);
        assertTrue(filepath.endsWith(".html"));
        
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        assertTrue(content.contains("<!DOCTYPE html>"));
    }
    
    @Test
    public void testExportAsJSON() throws IOException {
        SummaryExporter exporter = new SummaryExporter();
        
        String filepath = exporter.exportAs(testSummary, "json", TEST_OUTPUT_DIR);
        
        assertNotNull(filepath);
        assertTrue(filepath.endsWith(".json"));
        
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        assertTrue(content.contains("\"summaryId\""));
    }
    
    @Test
    public void testExportAsUnknownFormat() {
        SummaryExporter exporter = new SummaryExporter();
        
        assertThrows(IllegalArgumentException.class, () -> {
            exporter.exportAs(testSummary, "xyz", TEST_OUTPUT_DIR);
        });
    }
    
    @Test
    public void testExportNullSummary() {
        SummaryExporter exporter = new SummaryExporter();
        
        assertThrows(IllegalArgumentException.class, () -> {
            exporter.export(null, new PlainTextFormatter(), TEST_OUTPUT_DIR);
        });
    }
    
    @Test
    public void testExportNullFormatter() {
        SummaryExporter exporter = new SummaryExporter();
        
        assertThrows(IllegalArgumentException.class, () -> {
            exporter.export(testSummary, null, TEST_OUTPUT_DIR);
        });
    }
    
    @Test
    public void testExportNullPath() {
        SummaryExporter exporter = new SummaryExporter();
        
        assertThrows(IllegalArgumentException.class, () -> {
            exporter.export(testSummary, new PlainTextFormatter(), null);
        });
    }
}
