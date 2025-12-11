package com.ghostwriter.export;

import com.ghostwriter.analysis.Summary;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/**
 * Service class for exporting summaries in various formats.
 * Uses the Decorator pattern to support multiple output formats.
 * 
 * This class demonstrates the practical application of the Decorator pattern,
 * allowing flexible export of summaries without modifying the Summary class.
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class SummaryExporter {
    
    /**
     * Export a summary using the specified formatter.
     * 
     * @param summary The summary to export
     * @param formatter The formatter to use (determines output format)
     * @param outputPath The directory path where the file should be saved
     * @return The full path of the exported file
     * @throws IOException if file writing fails
     * @throws IllegalArgumentException if any parameter is null
     */
    public String export(Summary summary, SummaryFormatter formatter, String outputPath) 
            throws IOException {
        
        if (summary == null) {
            throw new IllegalArgumentException("Summary cannot be null");
        }
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter cannot be null");
        }
        if (outputPath == null || outputPath.isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty");
        }
        
        // Create output directory if it doesn't exist
        File directory = new File(outputPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Format the summary
        String content = formatter.format(summary);
        
        // Generate filename
        String filename = outputPath + "/summary_" + summary.getDate() + 
                         formatter.getFileExtension();
        
        // Write to file
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
        
        System.out.println("✅ Summary exported to: " + filename);
        
        return filename;
    }
    
    /**
     * Export a summary to all available formats at once.
     * Creates plain text, Markdown, HTML, and JSON versions.
     * 
     * @param summary The summary to export
     * @param outputPath The directory path where files should be saved
     * @throws IOException if file writing fails
     */
    public void exportToAllFormats(Summary summary, String outputPath) throws IOException {
        
        if (summary == null) {
            throw new IllegalArgumentException("Summary cannot be null");
        }
        
        System.out.println("\n📂 Exporting summary to multiple formats...");
        System.out.println("─".repeat(60));
        
        // Create base formatter
        PlainTextFormatter base = new PlainTextFormatter();
        
        // Export as plain text
        export(summary, base, outputPath);
        
        // Export as Markdown
        export(summary, new MarkdownDecorator(base), outputPath);
        
        // Export as HTML
        export(summary, new HTMLDecorator(base), outputPath);
        
        // Export as JSON
        export(summary, new JSONDecorator(base), outputPath);
        
        System.out.println("─".repeat(60));
        System.out.println("🎉 Summary exported in 4 formats!");
        System.out.println("\n📋 Files created:");
        System.out.println("   • summary_" + summary.getDate() + ".txt");
        System.out.println("   • summary_" + summary.getDate() + ".md");
        System.out.println("   • summary_" + summary.getDate() + ".html");
        System.out.println("   • summary_" + summary.getDate() + ".json");
        System.out.println();
    }
    
    /**
     * Export a summary in a specific format by name.
     * Convenience method for string-based format selection.
     * 
     * @param summary The summary to export
     * @param format Format name: "txt", "md", "html", or "json"
     * @param outputPath The directory path where the file should be saved
     * @return The full path of the exported file
     * @throws IOException if file writing fails
     * @throws IllegalArgumentException if format is not recognized
     */
    public String exportAs(Summary summary, String format, String outputPath) 
            throws IOException {
        
        PlainTextFormatter base = new PlainTextFormatter();
        SummaryFormatter formatter;
        
        switch (format.toLowerCase()) {
            case "txt":
            case "text":
                formatter = base;
                break;
            case "md":
            case "markdown":
                formatter = new MarkdownDecorator(base);
                break;
            case "html":
                formatter = new HTMLDecorator(base);
                break;
            case "json":
                formatter = new JSONDecorator(base);
                break;
            default:
                throw new IllegalArgumentException(
                    "Unknown format: " + format + ". Supported: txt, md, html, json");
        }
        
        return export(summary, formatter, outputPath);
    }
}
