package com.ghostwriter.analysis;

import com.ghostwriter.message.*;
import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class AnalyzerTest {
    
    @BeforeAll
    public static void setup() {
        Database.getInstance().connect();
    }
    
    @Test
    public void testAnalyzeConversation() {
        // Create test messages
        List<Message> messages = createTestMessages();
        
        // Create conversation
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Analyze
        Analyzer analyzer = new Analyzer();
        AnalysisResult result = analyzer.analyzeConversation(conv);
        
        // Verify results
        assertNotNull(result);
        assertTrue(result.hasTopics());
        assertTrue(result.getTopicCount() > 0);
        
        System.out.println("Topics found: " + result.getTopics());
        System.out.println("Action items: " + result.getActionItems());
    }
    
    @Test
    public void testSummaryGeneration() {
        // Create test data
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Analyze and generate summary
        Analyzer analyzer = new Analyzer();
        AnalysisResult result = analyzer.analyzeConversation(conv);
        
        Summary summary = Summary.generate("test-user", conv.getConversationId(), 
                                          "2024-12-10", result);
        
        assertNotNull(summary);
        assertNotNull(summary.formatSummary());
        
        // Print summary
        System.out.println("\n" + summary.formatSummary());
        
        // Save and verify
        summary.saveSummary();
        assertNotNull(summary.getSummaryId());
    }
    
    private List<Message> createTestMessages() {
        List<Message> messages = new ArrayList<>();
        
        messages.add(new Message(UUID.randomUUID().toString(), 
            "Hey! How's the project going?", "Alice", "2024-12-10 09:00:00"));
        
        messages.add(new Message(UUID.randomUUID().toString(), 
            "Going well! We need to finish the report by Friday", "Bob", "2024-12-10 09:05:00"));
        
        messages.add(new Message(UUID.randomUUID().toString(), 
            "Great! Can you send me the latest draft?", "Alice", "2024-12-10 09:10:00"));
        
        messages.add(new Message(UUID.randomUUID().toString(), 
            "Sure, I'll email it to you this afternoon. Don't forget we have a meeting tomorrow", 
            "Bob", "2024-12-10 09:15:00"));
        
        messages.add(new Message(UUID.randomUUID().toString(), 
            "Perfect! What time is the meeting?", "Alice", "2024-12-10 09:20:00"));
        
        messages.add(new Message(UUID.randomUUID().toString(), 
            "2 PM in the conference room. We should review the report together", 
            "Bob", "2024-12-10 09:25:00"));
        
        return messages;
    }
}
