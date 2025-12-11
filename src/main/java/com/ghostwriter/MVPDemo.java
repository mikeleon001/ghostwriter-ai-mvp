// MVPDemo.java - Main application demonstrating Daily Summary Feature

package com.ghostwriter;

import com.ghostwriter.database.Database;
import com.ghostwriter.user.*;
import com.ghostwriter.message.*;
import com.ghostwriter.analysis.*;

import java.io.*;
import java.util.*;

/**
 * MVP Demo Application - Daily Summary Feature
 * 
 * Demonstrates the complete workflow:
 * 1. User registration
 * 2. User login
 * 3. Upload conversation file
 * 4. Generate daily summary
 * 5. View summary
 */
public class MVPDemo {
    
    public static void main(String[] args) {
        
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   GhostWriter AI - Daily Summary MVP Demo      ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        try {
            // Initialize database
            System.out.println("🔧 Initializing database...");
            Database db = Database.getInstance();
            db.connect();
            System.out.println("✅ Database ready\n");
            
            // Demo workflow
            runMVPDemo();
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run complete MVP demonstration
     */
    private static void runMVPDemo() throws Exception {
        
        // Step 1: Register User
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 1: User Registration");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        String username = "demo_user_" + System.currentTimeMillis();
        String email = "demo" + System.currentTimeMillis() + "@example.com";
        String password = "DemoPass123!";
        
        User user = User.register(username, email, password);
        System.out.println("✅ Registered user: " + username + "\n");
        
        // Step 2: Login
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 2: User Login");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        String token = User.login(username, password);
        System.out.println("✅ Login successful");
        System.out.println("🔑 Token: " + token.substring(0, 20) + "...\n");
        
        // Step 3: Create Sample Conversation File
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 3: Upload Conversation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        File conversationFile = createSampleConversationFile();
        System.out.println("📄 Created sample conversation file: " + conversationFile.getName());
        
        int messageCount = user.uploadConversations(conversationFile);
        System.out.println("✅ Uploaded " + messageCount + " messages\n");
        
        // Step 4: Generate Summary
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 4: Generate Daily Summary");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        Summary summary = user.generateSummaryForToday();
        
        // Step 5: Display Summary
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("STEP 5: View Daily Summary");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        System.out.println(summary.formatSummary());
        
        // Demonstrate Strategy Pattern
        demonstrateStrategyPattern(user);
        
        // Cleanup
        conversationFile.delete();
        
        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║         MVP Demo Completed Successfully!       ║");
        System.out.println("╚════════════════════════════════════════════════╝");
    }
    
    /**
     * Demonstrate Strategy Pattern flexibility
     */
    private static void demonstrateStrategyPattern(User user) throws Exception {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("BONUS: Strategy Pattern Demonstration");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Get today's conversation
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        Database db = Database.getInstance();
        String sql = "SELECT * FROM conversations WHERE user_id = ? AND date = ?";
        List<Map<String, String>> results = db.query(sql, user.getUserId(), today);
        
        if (!results.isEmpty()) {
            Conversation conv = Conversation.fromDatabase(results.get(0));
            
            // Load messages (simplified - in real app would query messages table)
            System.out.println("🎯 Custom Analysis: Topics + Statistics Only\n");
            
            // Create analyzer with custom strategies
            List<AnalysisStrategy> customStrategies = new ArrayList<>();
            customStrategies.add(new TopicExtractionStrategy());
            customStrategies.add(new StatisticsStrategy());
            
            Analyzer customAnalyzer = new Analyzer(customStrategies);
            
            System.out.println("Selected strategies:");
            for (AnalysisStrategy strategy : customAnalyzer.getStrategies()) {
                System.out.println("  ✓ " + strategy.getStrategyName());
            }
            
            System.out.println("\nThis demonstrates the flexibility of the Strategy Pattern!");
            System.out.println("You can choose which analyses to run at runtime.\n");
        }
    }
    
    /**
     * Create sample WhatsApp conversation file for demo
     */
    private static File createSampleConversationFile() throws IOException {
        File file = new File("sample_whatsapp_chat.txt");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("[12/10/24, 9:00 AM] Alice: Good morning! How's the project coming along?");
            writer.println("[12/10/24, 9:05 AM] Bob: Morning! It's going well. We need to finish the final report by Friday.");
            writer.println("[12/10/24, 9:10 AM] Alice: That's great! Can you send me the latest draft?");
            writer.println("[12/10/24, 9:15 AM] Bob: Sure, I'll email it to you this afternoon. Don't forget we have a meeting tomorrow at 2 PM.");
            writer.println("[12/10/24, 9:20 AM] Alice: Perfect! What should we discuss in the meeting?");
            writer.println("[12/10/24, 9:25 AM] Bob: We should review the budget analysis and timeline. Also, please bring the client feedback.");
            writer.println("[12/10/24, 9:30 AM] Alice: Got it. I'll prepare the presentation slides tonight.");
            writer.println("[12/10/24, 9:35 AM] Bob: Excellent! By the way, did you check if the conference room is available?");
            writer.println("[12/10/24, 9:40 AM] Alice: Not yet, I'll call reception right now.");
            writer.println("[12/10/24, 9:45 AM] Bob: Thanks! Let me know. We might need the projector too.");
        }
        
        return file;
    }
}
