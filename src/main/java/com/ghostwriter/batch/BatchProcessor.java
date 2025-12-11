package com.ghostwriter.batch;

import com.ghostwriter.analysis.Analyzer;
import com.ghostwriter.analysis.AnalysisResult;
import com.ghostwriter.analysis.Summary;
import com.ghostwriter.message.Conversation;
import com.ghostwriter.message.Message;
import com.ghostwriter.message.MessageParser;
import com.ghostwriter.message.MessageParserFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * BatchProcessor handles processing of multiple conversation files at once.
 * 
 * This class coordinates the batch processing workflow:
 * 1. Validates each file
 * 2. Parses messages using Factory Pattern (MessageParserFactory)
 * 3. Analyzes conversations using Strategy Pattern (Analyzer)
 * 4. Generates summaries for each conversation
 * 5. Tracks results in a BatchResult
 * 
 * Reuses existing design patterns:
 * - Factory Pattern: MessageParserFactory to create appropriate parsers
 * - Strategy Pattern: Analyzer with multiple analysis strategies
 * - Singleton Pattern: Database for storing results
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class BatchProcessor {

    private final String userId;
    private final Analyzer analyzer;
    
    // Supported file extensions
    private static final String[] SUPPORTED_EXTENSIONS = {".txt"};
    
    // Maximum file size (10 MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * Creates a new BatchProcessor for the specified user.
     *
     * @param userId The ID of the user performing the batch processing
     */
    public BatchProcessor(String userId) {
        this.userId = userId;
        this.analyzer = new Analyzer(); // Uses default strategies
    }

    /**
     * Creates a new BatchProcessor with a custom analyzer.
     * Useful for testing or custom analysis configurations.
     *
     * @param userId The ID of the user performing the batch processing
     * @param analyzer Custom analyzer with specific strategies
     */
    public BatchProcessor(String userId, Analyzer analyzer) {
        this.userId = userId;
        this.analyzer = analyzer;
    }

    /**
     * Process multiple conversation files.
     * 
     * This is the main method that orchestrates batch processing:
     * - Creates a BatchResult to track progress
     * - Iterates through each file
     * - Validates, parses, analyzes, and generates summaries
     * - Records successes and failures
     *
     * @param files List of files to process
     * @return BatchResult containing processing results
     */
    public BatchResult processFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            BatchResult emptyResult = new BatchResult(0);
            emptyResult.markComplete();
            return emptyResult;
        }

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║   📦 BATCH PROCESSING STARTED                  ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("Files to process: " + files.size());
        System.out.println();

        BatchResult result = new BatchResult(files.size());

        int fileNumber = 1;
        for (File file : files) {
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Processing file " + fileNumber + "/" + files.size() + ": " + file.getName());
            
            processFileWithTracking(file, result);
            
            fileNumber++;
        }

        result.markComplete();

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║   ✅ BATCH PROCESSING COMPLETE                 ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println(result.formatResult());

        return result;
    }

    /**
     * Process a single file and track the result.
     *
     * @param file The file to process
     * @param result The BatchResult to track success/failure
     */
    private void processFileWithTracking(File file, BatchResult result) {
        try {
            // Validate the file first
            validateFile(file);

            // Process the file and generate summary
            Summary summary = processSingleFile(file);

            // Record success
            result.addSuccess(file.getName(), summary);
            System.out.println("✅ Successfully processed: " + file.getName());

        } catch (IllegalArgumentException e) {
            // Validation error
            result.addFailure(file.getName(), "Validation failed: " + e.getMessage());
            System.out.println("❌ Validation failed: " + e.getMessage());

        } catch (IOException e) {
            // File reading error
            result.addFailure(file.getName(), "Could not read file: " + e.getMessage());
            System.out.println("❌ Read error: " + e.getMessage());

        } catch (Exception e) {
            // Any other error
            result.addFailure(file.getName(), "Processing error: " + e.getMessage());
            System.out.println("❌ Processing error: " + e.getMessage());
        }
    }

    /**
     * Process a single file and return the generated summary.
     *
     * @param file The file to process
     * @return The generated Summary
     * @throws IOException If the file cannot be read
     */
    private Summary processSingleFile(File file) throws IOException {
        // Step 1: Read file content
        String content = readFileContent(file);
        System.out.println("   📄 Read " + content.length() + " characters");

        // Step 2: Get appropriate parser (Factory Pattern)
        MessageParser parser = MessageParserFactory.getDefaultParser();
        
        // Step 3: Parse messages
        List<Message> messages = parser.parse(content);
        System.out.println("   📝 Parsed " + messages.size() + " messages");

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("No valid messages found in file");
        }

        // Step 4: Create conversation
        String date = extractDateFromFile(file);
        Conversation conversation = Conversation.create(userId, date);
        
        for (Message message : messages) {
            conversation.addMessage(message);
        }

        // Step 5: Save conversation to database
        conversation.save();

        // Step 6: Analyze conversation (Strategy Pattern)
        AnalysisResult analysisResult = analyzer.analyzeConversation(conversation);

        // Step 7: Generate and save summary
        Summary summary = Summary.generate(
            userId,
            conversation.getConversationId(),
            date,
            analysisResult
        );
        summary.saveSummary();

        return summary;
    }

    /**
     * Validate that a file is suitable for processing.
     *
     * @param file The file to validate
     * @throws IllegalArgumentException If validation fails
     */
    private void validateFile(File file) {
        // Check if file is null
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        // Check if file exists
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + file.getName());
        }

        // Check if it's a file (not a directory)
        if (!file.isFile()) {
            throw new IllegalArgumentException("Not a file: " + file.getName());
        }

        // Check if file is readable
        if (!file.canRead()) {
            throw new IllegalArgumentException("File is not readable: " + file.getName());
        }

        // Check file extension
        if (!hasValidExtension(file.getName())) {
            throw new IllegalArgumentException(
                "Unsupported file type. Supported: .txt"
            );
        }

        // Check file size
        if (file.length() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "File too large. Maximum size: 10 MB"
            );
        }

        // Check if file is empty
        if (file.length() == 0) {
            throw new IllegalArgumentException("File is empty");
        }
    }

    /**
     * Check if the filename has a supported extension.
     *
     * @param filename The filename to check
     * @return true if the extension is supported
     */
    private boolean hasValidExtension(String filename) {
        String lowerName = filename.toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Read the content of a file as a string.
     *
     * @param file The file to read
     * @return The file content as a string
     * @throws IOException If the file cannot be read
     */
    private String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    /**
     * Extract a date string from the file.
     * Uses the file's last modified date if no date is in the filename.
     *
     * @param file The file to extract date from
     * @return A date string in yyyy-MM-dd format
     */
    private String extractDateFromFile(File file) {
        // Try to extract date from filename (e.g., "chat_2025-12-10.txt")
        String filename = file.getName();
        
        // Simple pattern matching for dates in filename
        if (filename.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
            int startIndex = filename.indexOf("20"); // Assumes years starting with 20
            if (startIndex >= 0 && startIndex + 10 <= filename.length()) {
                String possibleDate = filename.substring(startIndex, startIndex + 10);
                if (possibleDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return possibleDate;
                }
            }
        }

        // Fallback: use file's last modified date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(file.lastModified()));
    }

    /**
     * Get the user ID associated with this processor.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Get the analyzer being used by this processor.
     *
     * @return The Analyzer instance
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
