package com.ghostwriter.export;

import com.ghostwriter.analysis.Summary;
import java.util.Map;

/**
 * Concrete Decorator that formats summaries as HTML with CSS styling.
 * Wraps a SummaryFormatter and produces browser-viewable HTML output.
 * 
 * Features:
 * - Complete HTML5 document structure
 * - Embedded CSS for professional styling
 * - Responsive design
 * - Color-coded sections with emojis
 * 
 * @author Mihail Chitorog
 * @version 1.0
 */
public class HTMLDecorator extends SummaryFormatterDecorator {
    
    /**
     * Constructs an HTML decorator.
     * 
     * @param formatter The formatter to wrap
     */
    public HTMLDecorator(SummaryFormatter formatter) {
        super(formatter);
    }
    
    /**
     * Format summary as HTML with embedded CSS.
     * 
     * @param summary The summary to format
     * @return HTML-formatted string
     */
    @Override
    public String format(Summary summary) {
        StringBuilder sb = new StringBuilder();
        
        // HTML Document structure
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("    <meta charset=\"UTF-8\">\n");
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("    <title>Daily Summary - ").append(summary.getDate()).append("</title>\n");
        
        // Embedded CSS
        sb.append("    <style>\n");
        sb.append("        body {\n");
        sb.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
        sb.append("            max-width: 800px;\n");
        sb.append("            margin: 50px auto;\n");
        sb.append("            padding: 20px;\n");
        sb.append("            background-color: #f5f5f5;\n");
        sb.append("            line-height: 1.6;\n");
        sb.append("        }\n");
        sb.append("        .container {\n");
        sb.append("            background: white;\n");
        sb.append("            padding: 30px;\n");
        sb.append("            border-radius: 10px;\n");
        sb.append("            box-shadow: 0 2px 10px rgba(0,0,0,0.1);\n");
        sb.append("        }\n");
        sb.append("        h1 {\n");
        sb.append("            color: #2c3e50;\n");
        sb.append("            border-bottom: 3px solid #3498db;\n");
        sb.append("            padding-bottom: 10px;\n");
        sb.append("        }\n");
        sb.append("        h2 {\n");
        sb.append("            color: #34495e;\n");
        sb.append("            margin-top: 30px;\n");
        sb.append("            font-size: 1.3em;\n");
        sb.append("        }\n");
        sb.append("        .stat-box {\n");
        sb.append("            background: #ecf0f1;\n");
        sb.append("            padding: 15px;\n");
        sb.append("            border-radius: 5px;\n");
        sb.append("            margin: 10px 0;\n");
        sb.append("        }\n");
        sb.append("        ul {\n");
        sb.append("            list-style-type: none;\n");
        sb.append("            padding-left: 0;\n");
        sb.append("        }\n");
        sb.append("        li {\n");
        sb.append("            padding: 8px 0;\n");
        sb.append("            border-bottom: 1px solid #ecf0f1;\n");
        sb.append("        }\n");
        sb.append("        li:last-child {\n");
        sb.append("            border-bottom: none;\n");
        sb.append("        }\n");
        sb.append("        .topic { color: #2980b9; }\n");
        sb.append("        .action { color: #27ae60; }\n");
        sb.append("        .question { color: #e67e22; }\n");
        sb.append("        .footer {\n");
        sb.append("            text-align: center;\n");
        sb.append("            margin-top: 40px;\n");
        sb.append("            padding-top: 20px;\n");
        sb.append("            border-top: 2px solid #ecf0f1;\n");
        sb.append("            color: #7f8c8d;\n");
        sb.append("            font-style: italic;\n");
        sb.append("        }\n");
        sb.append("    </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("    <div class=\"container\">\n");
        
        // Header
        sb.append("        <h1>📅 Daily Summary - ").append(summary.getDate()).append("</h1>\n\n");
        
        // Message Statistics
        sb.append("        <h2>📊 Message Statistics</h2>\n");
        sb.append("        <div class=\"stat-box\">\n");
        
        Map<String, Object> stats = summary.getStatistics();
        Object totalMessages = stats.getOrDefault("total_messages", 0);
        sb.append("            <p><strong>Total Messages:</strong> ").append(totalMessages).append("</p>\n");
        
        if (stats.containsKey("most_active")) {
            sb.append("            <p><strong>Most Active:</strong> ").append(stats.get("most_active")).append("</p>\n");
        }
        
        sb.append("        </div>\n\n");
        
        // Key Topics
        sb.append("        <h2>🔑 Key Topics Discussed</h2>\n");
        if (summary.getKeyTopics().isEmpty()) {
            sb.append("        <p><em>No specific topics identified</em></p>\n");
        } else {
            sb.append("        <ul>\n");
            for (String topic : summary.getKeyTopics()) {
                sb.append("            <li class=\"topic\">• ").append(escapeHtml(topic)).append("</li>\n");
            }
            sb.append("        </ul>\n");
        }
        sb.append("\n");
        
        // Action Items
        if (!summary.getActionItems().isEmpty()) {
            sb.append("        <h2>⚡ Action Items</h2>\n");
            sb.append("        <ul>\n");
            for (String item : summary.getActionItems()) {
                sb.append("            <li class=\"action\">☐ ").append(escapeHtml(item)).append("</li>\n");
            }
            sb.append("        </ul>\n\n");
        }
        
        // Pending Questions
        if (!summary.getPendingQuestions().isEmpty()) {
            sb.append("        <h2>❓ Pending Questions</h2>\n");
            sb.append("        <ul>\n");
            for (String question : summary.getPendingQuestions()) {
                sb.append("            <li class=\"question\">? ").append(escapeHtml(question)).append("</li>\n");
            }
            sb.append("        </ul>\n\n");
        }
        
        // Footer
        sb.append("        <div class=\"footer\">\n");
        sb.append("            Generated by GhostWriter AI\n");
        sb.append("        </div>\n");
        
        sb.append("    </div>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        
        return sb.toString();
    }
    
    /**
     * Escape HTML special characters to prevent XSS and formatting issues.
     * 
     * @param text The text to escape
     * @return HTML-safe text
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * Get file extension for HTML format.
     * 
     * @return ".html"
     */
    @Override
    public String getFileExtension() {
        return ".html";
    }
}
