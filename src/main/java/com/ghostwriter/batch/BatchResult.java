package com.ghostwriter.batch;

import com.ghostwriter.analysis.Summary;

import java.util.*;

/**
 * BatchResult holds the results of a batch processing operation.
 * 
 * This class acts as a "receipt" that tracks:
 * - How many files were processed
 * - Which files succeeded or failed
 * - The summaries that were generated
 * - How long the processing took
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class BatchResult {

    private final String batchId;
    private final int totalFiles;
    private int successCount;
    private int failureCount;
    private final List<String> processedFiles;
    private final Map<String, String> failedFiles;
    private final List<Summary> summariesGenerated;
    private final Date startTime;
    private Date endTime;

    /**
     * Creates a new BatchResult for tracking batch processing.
     *
     * @param totalFiles The total number of files to be processed
     */
    public BatchResult(int totalFiles) {
        this.batchId = UUID.randomUUID().toString();
        this.totalFiles = totalFiles;
        this.successCount = 0;
        this.failureCount = 0;
        this.processedFiles = new ArrayList<>();
        this.failedFiles = new HashMap<>();
        this.summariesGenerated = new ArrayList<>();
        this.startTime = new Date();
        this.endTime = null;
    }

    /**
     * Records a successfully processed file.
     *
     * @param filename The name of the file that was processed
     * @param summary The summary generated from the file
     */
    public void addSuccess(String filename, Summary summary) {
        processedFiles.add(filename);
        summariesGenerated.add(summary);
        successCount++;
    }

    /**
     * Records a failed file processing attempt.
     *
     * @param filename The name of the file that failed
     * @param reason The reason for the failure
     */
    public void addFailure(String filename, String reason) {
        failedFiles.put(filename, reason);
        failureCount++;
    }

    /**
     * Marks the batch processing as complete.
     * Call this when all files have been processed.
     */
    public void markComplete() {
        this.endTime = new Date();
    }

    /**
     * Checks if all files were processed successfully.
     *
     * @return true if no failures occurred, false otherwise
     */
    public boolean isSuccess() {
        return failureCount == 0 && successCount == totalFiles;
    }

    /**
     * Calculates the success rate as a percentage.
     *
     * @return The percentage of files processed successfully (0.0 to 100.0)
     */
    public double getSuccessRate() {
        if (totalFiles == 0) {
            return 0.0;
        }
        return (successCount * 100.0) / totalFiles;
    }

    /**
     * Gets the duration of the batch processing in milliseconds.
     *
     * @return Duration in milliseconds, or -1 if not yet complete
     */
    public long getDuration() {
        if (endTime == null) {
            return -1;
        }
        return endTime.getTime() - startTime.getTime();
    }

    /**
     * Gets the duration formatted as a human-readable string.
     *
     * @return Duration string like "2.5 seconds" or "1 minute 30 seconds"
     */
    public String getFormattedDuration() {
        long durationMs = getDuration();
        if (durationMs < 0) {
            return "In progress...";
        }
        
        long seconds = durationMs / 1000;
        long millis = durationMs % 1000;
        
        if (seconds < 60) {
            return String.format("%d.%d seconds", seconds, millis / 100);
        } else {
            long minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%d minute(s) %d seconds", minutes, seconds);
        }
    }

    /**
     * Generates a formatted summary of the batch result.
     *
     * @return A human-readable summary string
     */
    public String formatResult() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("   📦 BATCH PROCESSING RESULT\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        sb.append("📊 STATISTICS\n");
        sb.append("─────────────────────────────────────────────────\n");
        sb.append("Batch ID: ").append(batchId.substring(0, 8)).append("...\n");
        sb.append("Total Files: ").append(totalFiles).append("\n");
        sb.append("Successful: ").append(successCount).append("\n");
        sb.append("Failed: ").append(failureCount).append("\n");
        sb.append("Success Rate: ").append(String.format("%.1f%%", getSuccessRate())).append("\n");
        sb.append("Duration: ").append(getFormattedDuration()).append("\n\n");
        
        if (!processedFiles.isEmpty()) {
            sb.append("✅ PROCESSED FILES\n");
            sb.append("─────────────────────────────────────────────────\n");
            for (String file : processedFiles) {
                sb.append("  • ").append(file).append("\n");
            }
            sb.append("\n");
        }
        
        if (!failedFiles.isEmpty()) {
            sb.append("❌ FAILED FILES\n");
            sb.append("─────────────────────────────────────────────────\n");
            for (Map.Entry<String, String> entry : failedFiles.entrySet()) {
                sb.append("  • ").append(entry.getKey())
                  .append(": ").append(entry.getValue()).append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("═══════════════════════════════════════════════════\n");
        
        return sb.toString();
    }

    // ============================================
    // GETTERS
    // ============================================

    public String getBatchId() {
        return batchId;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public List<String> getProcessedFiles() {
        return new ArrayList<>(processedFiles);
    }

    public Map<String, String> getFailedFiles() {
        return new HashMap<>(failedFiles);
    }

    public List<Summary> getSummariesGenerated() {
        return new ArrayList<>(summariesGenerated);
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
    }

    public Date getEndTime() {
        return endTime != null ? new Date(endTime.getTime()) : null;
    }

    @Override
    public String toString() {
        return String.format("BatchResult[id=%s, total=%d, success=%d, failed=%d]",
                batchId.substring(0, 8), totalFiles, successCount, failureCount);
    }
}
