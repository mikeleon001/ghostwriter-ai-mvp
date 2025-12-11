package com.ghostwriter.analysis;

import com.ghostwriter.message.*;
import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * AnalyzerTest - Updated to test Strategy Pattern implementation
 * 
 * Tests include:
 * 1. Original tests (backward compatibility)
 * 2. Individual strategy tests
 * 3. Custom strategy composition tests
 *
 * @author Mihail Chitorog
 */
public class AnalyzerTest {
    
    @BeforeAll
    public static void setup() {
        Database.getInstance().connect();
    }
    
    // ========================================
    // ORIGINAL TESTS (Backward Compatibility)
    // ========================================
    
    @Test
    public void testAnalyzeConversation() {
        // Create test messages
        List<Message> messages = createTestMessages();
        
        // Create conversation
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Analyze with default strategies
        Analyzer analyzer = new Analyzer();
        AnalysisResult result = analyzer.analyzeConversation(conv);
        
        // Verify results
        assertNotNull(result);
        assertTrue(result.hasTopics());
        assertTrue(result.getTopicCount() > 0);
        
        System.out.println("\n=== Full Analysis Results ===");
        System.out.println("Topics found: " + result.getTopics());
        System.out.println("Action items: " + result.getActionItems());
        System.out.println("Questions: " + result.getQuestions());
        System.out.println("Statistics: " + result.getStatistics());
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
        System.out.println("\n=== Generated Summary ===");
        System.out.println(summary.formatSummary());
        
        // Save and verify
        summary.saveSummary();
        assertNotNull(summary.getSummaryId());
    }
    
    // ========================================
    // STRATEGY PATTERN TESTS (New)
    // ========================================
    
    @Test
    public void testTopicExtractionStrategy() {
        System.out.println("\n=== Testing TopicExtractionStrategy ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Test strategy individually
        TopicExtractionStrategy strategy = new TopicExtractionStrategy();
        AnalysisResult result = strategy.analyze(conv);
        
        // Should have topics, but not action items or questions
        assertNotNull(result.getTopics());
        assertTrue(result.getTopics().size() > 0);
        assertTrue(result.getActionItems().isEmpty());
        assertTrue(result.getQuestions().isEmpty());
        assertTrue(result.getStatistics().isEmpty());
        
        System.out.println("Strategy name: " + strategy.getStrategyName());
        System.out.println("Topics extracted: " + result.getTopics());
    }
    
    @Test
    public void testActionItemStrategy() {
        System.out.println("\n=== Testing ActionItemStrategy ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Test strategy individually
        ActionItemStrategy strategy = new ActionItemStrategy();
        AnalysisResult result = strategy.analyze(conv);
        
        // Should have action items, but not topics or questions
        assertNotNull(result.getActionItems());
        assertTrue(result.getActionItems().size() > 0);
        assertTrue(result.getTopics().isEmpty());
        assertTrue(result.getQuestions().isEmpty());
        
        System.out.println("Strategy name: " + strategy.getStrategyName());
        System.out.println("Action items found: " + result.getActionItems());
    }
    
    @Test
    public void testQuestionDetectionStrategy() {
        System.out.println("\n=== Testing QuestionDetectionStrategy ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Test strategy individually
        QuestionDetectionStrategy strategy = new QuestionDetectionStrategy();
        AnalysisResult result = strategy.analyze(conv);
        
        // Should have questions, but not topics or action items
        assertNotNull(result.getQuestions());
        assertTrue(result.getQuestions().size() > 0);
        assertTrue(result.getTopics().isEmpty());
        assertTrue(result.getActionItems().isEmpty());
        
        System.out.println("Strategy name: " + strategy.getStrategyName());
        System.out.println("Questions found: " + result.getQuestions());
    }
    
    @Test
    public void testStatisticsStrategy() {
        System.out.println("\n=== Testing StatisticsStrategy ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Test strategy individually
        StatisticsStrategy strategy = new StatisticsStrategy();
        AnalysisResult result = strategy.analyze(conv);
        
        // Should have statistics, but not topics, action items, or questions
        assertNotNull(result.getStatistics());
        assertTrue(result.getStatistics().size() > 0);
        assertTrue(result.getTopics().isEmpty());
        assertTrue(result.getActionItems().isEmpty());
        assertTrue(result.getQuestions().isEmpty());
        
        System.out.println("Strategy name: " + strategy.getStrategyName());
        System.out.println("Statistics: " + result.getStatistics());
    }
    
    @Test
    public void testCustomStrategySelection() {
        System.out.println("\n=== Testing Custom Strategy Selection ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Create analyzer with only specific strategies
        List<AnalysisStrategy> customStrategies = new ArrayList<>();
        customStrategies.add(new TopicExtractionStrategy());
        customStrategies.add(new StatisticsStrategy());
        
        Analyzer analyzer = new Analyzer(customStrategies);
        AnalysisResult result = analyzer.analyzeConversation(conv);
        
        // Should only have topics and statistics
        assertFalse(result.getTopics().isEmpty());
        assertFalse(result.getStatistics().isEmpty());
        assertTrue(result.getActionItems().isEmpty());
        assertTrue(result.getQuestions().isEmpty());
        
        System.out.println("Topics: " + result.getTopics());
        System.out.println("Statistics: " + result.getStatistics());
        System.out.println("Action items (should be empty): " + result.getActionItems());
    }
    
    @Test
    public void testDynamicStrategyAddition() {
        System.out.println("\n=== Testing Dynamic Strategy Addition ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Start with empty analyzer
        Analyzer analyzer = new Analyzer(new ArrayList<>());
        
        // Add strategies dynamically
        analyzer.addStrategy(new TopicExtractionStrategy());
        analyzer.addStrategy(new ActionItemStrategy());
        
        AnalysisResult result = analyzer.analyzeConversation(conv);
        
        // Should have topics and action items only
        assertFalse(result.getTopics().isEmpty());
        assertFalse(result.getActionItems().isEmpty());
        assertTrue(result.getQuestions().isEmpty());
        assertTrue(result.getStatistics().isEmpty());
        
        System.out.println("Strategies added: " + analyzer.getStrategies().size());
        System.out.println("Topics: " + result.getTopics());
        System.out.println("Action items: " + result.getActionItems());
    }
    
    @Test
    public void testAnalyzeWithTemporaryStrategies() {
        System.out.println("\n=== Testing analyzeWith() Method ===");
        
        List<Message> messages = createTestMessages();
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        for (Message msg : messages) {
            conv.addMessage(msg);
        }
        
        // Create analyzer with all default strategies
        Analyzer analyzer = new Analyzer();
        
        // Temporarily analyze with only specific strategies
        AnalysisResult result = analyzer.analyzeWith(conv,
            new TopicExtractionStrategy(),
            new QuestionDetectionStrategy()
        );
        
        // Should only have topics and questions
        assertFalse(result.getTopics().isEmpty());
        assertFalse(result.getQuestions().isEmpty());
        assertTrue(result.getActionItems().isEmpty());
        
        System.out.println("Temporary analysis - Topics: " + result.getTopics());
        System.out.println("Temporary analysis - Questions: " + result.getQuestions());
        
        // Analyzer should still have all original strategies
        assertEquals(4, analyzer.getStrategies().size());
    }
    
    @Test
    public void testEmptyConversationHandling() {
        System.out.println("\n=== Testing Empty Conversation Handling ===");
        
        Conversation emptyConv = Conversation.create("test-user", "2024-12-10");
        
        Analyzer analyzer = new Analyzer();
        
        // Should throw exception for empty conversation
        assertThrows(IllegalArgumentException.class, () -> {
            analyzer.analyzeConversation(emptyConv);
        });
    }
    
    @Test
    public void testNullConversationHandling() {
        System.out.println("\n=== Testing Null Conversation Handling ===");
        
        Analyzer analyzer = new Analyzer();
        
        // Should throw exception for null conversation
        assertThrows(IllegalArgumentException.class, () -> {
            analyzer.analyzeConversation(null);
        });
    }
    
    @Test
    public void testStrategyGetters() {
        System.out.println("\n=== Testing Strategy Management ===");
        
        Analyzer analyzer = new Analyzer();
        
        // Should have 4 default strategies
        List<AnalysisStrategy> strategies = analyzer.getStrategies();
        assertEquals(4, strategies.size());
        
        System.out.println("Default strategies:");
        for (AnalysisStrategy strategy : strategies) {
            System.out.println("  - " + strategy.getStrategyName());
        }
        
        // Test setStrategies
        List<AnalysisStrategy> newStrategies = new ArrayList<>();
        newStrategies.add(new TopicExtractionStrategy());
        analyzer.setStrategies(newStrategies);
        
        assertEquals(1, analyzer.getStrategies().size());
    }
    
    // ========================================
    // HELPER METHOD
    // ========================================
    
    private List<Message> createTestMessages() {
        List<Message> messages = new ArrayList<>();
        
        messages.add(new Message.Builder("Hey! How's the project going?", "Alice")
                .timestamp("2024-12-10 09:00:00").build());
        
        messages.add(new Message.Builder("Going well! We need to finish the report by Friday", "Bob")
                .timestamp("2024-12-10 09:05:00").build());
        
        messages.add(new Message.Builder("Great! Can you send me the latest draft?", "Alice")
                .timestamp("2024-12-10 09:10:00").build());
        
        messages.add(new Message.Builder("Sure, I'll email it to you this afternoon. Don't forget we have a meeting tomorrow", "Bob")
                .timestamp("2024-12-10 09:15:00").build());
        
        messages.add(new Message.Builder("Perfect! What time is the meeting?", "Alice")
                .timestamp("2024-12-10 09:20:00").build());
        
        messages.add(new Message.Builder("2 PM in the conference room. We should review the report together", "Bob")
                .timestamp("2024-12-10 09:25:00").build());
        
        return messages;
    }
}
