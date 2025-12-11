package com.ghostwriter.message;

import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * ConversationTest - Tests for Conversation class
 * 
 * Tests cover:
 * - Conversation creation
 * - Message addition
 * - Database persistence (save/load)
 * - Message retrieval
 *
 * @author Mihail Chitorog
 */
public class ConversationTest {
    
    private static Database db;
    
    @BeforeAll
    public static void setupDatabase() {
        db = Database.getInstance();
        db.connect();
    }
    
    @BeforeEach
    public void cleanupBefore() {
        // Clean up any test data
        // (In production, you'd use a test database)
    }
    
    // ========================================
    // Creation Tests
    // ========================================
    
    @Test
    public void testCreateConversation() {
        String userId = "test-user-" + System.currentTimeMillis();
        String date = "2024-12-10";
        
        Conversation conv = Conversation.create(userId, date);
        
        assertNotNull(conv);
        assertNotNull(conv.getConversationId());
        assertEquals(userId, conv.getUserId());
        assertEquals(date, conv.getDate());
    }
    
    @Test
    public void testConversationHasUniqueId() {
        Conversation conv1 = Conversation.create("user1", "2024-12-10");
        Conversation conv2 = Conversation.create("user1", "2024-12-10");
        
        assertNotNull(conv1.getConversationId());
        assertNotNull(conv2.getConversationId());
        assertNotEquals(conv1.getConversationId(), conv2.getConversationId());
    }
    
    // ========================================
    // Message Addition Tests
    // ========================================
    
    @Test
    public void testAddSingleMessage() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        Message msg = new Message.Builder("Test message", "Alice")
                .messageId(UUID.randomUUID().toString())
                .timestamp("2024-12-10 10:00:00")
                .build();
        
        conv.addMessage(msg);
        
        assertEquals(1, conv.getMessages().size());
        assertEquals("Test message", conv.getMessages().get(0).getContent());
    }
    
    @Test
    public void testAddMultipleMessages() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        Message msg1 = new Message.Builder("Message 1", "Alice").timestamp("2024-12-10 10:00:00").build();
        Message msg2 = new Message.Builder("Message 2", "Bob").timestamp("2024-12-10 10:01:00").build();
        Message msg3 = new Message.Builder("Message 3", "Alice").timestamp("2024-12-10 10:02:00").build();
        
        conv.addMessage(msg1);
        conv.addMessage(msg2);
        conv.addMessage(msg3);
        
        assertEquals(3, conv.getMessages().size());
        assertEquals("Message 1", conv.getMessages().get(0).getContent());
        assertEquals("Message 2", conv.getMessages().get(1).getContent());
        assertEquals("Message 3", conv.getMessages().get(2).getContent());
    }
    
    @Test
    public void testMessagesAreOrderedByAddition() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        Message msg1 = new Message.Builder("First", "Alice").timestamp("2024-12-10 10:00:00").build();
        Message msg2 = new Message.Builder("Second", "Bob").timestamp("2024-12-10 09:00:00").build();
        Message msg3 = new Message.Builder("Third", "Alice").timestamp("2024-12-10 11:00:00").build();
        
        conv.addMessage(msg1);
        conv.addMessage(msg2);
        conv.addMessage(msg3);
        
        // Messages should be in order they were added, not by timestamp
        assertEquals("First", conv.getMessages().get(0).getContent());
        assertEquals("Second", conv.getMessages().get(1).getContent());
        assertEquals("Third", conv.getMessages().get(2).getContent());
    }
    
    @Test
    public void testEmptyConversation() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        assertNotNull(conv.getMessages());
        assertEquals(0, conv.getMessages().size());
    }
    
    // ========================================
    // Database Persistence Tests
    // ========================================
    
    @Test
    public void testSaveConversation() {
        String userId = "test-user-" + System.currentTimeMillis();
        Conversation conv = Conversation.create(userId, "2024-12-10");
        
        Message msg = new Message.Builder("Test message for save", "Alice")
                .messageId(UUID.randomUUID().toString())
                .timestamp("2024-12-10 10:00:00")
                .build();
        conv.addMessage(msg);
        
        // Save should not throw exception
        assertDoesNotThrow(() -> conv.save());
        
        // Verify conversation was saved
        Map<String, String> savedConv = db.retrieve("conversations", "conversation_id", conv.getConversationId());
        assertNotNull(savedConv);
        assertEquals(userId, savedConv.get("user_id"));
    }
    
    @Test
    public void testSaveAndLoadConversation() {
        String userId = "test-user-" + System.currentTimeMillis();
        String date = "2024-12-10";
        
        // Create and save conversation
        Conversation originalConv = Conversation.create(userId, date);
        
        Message msg1 = new Message.Builder("Message 1", "Alice").timestamp("2024-12-10 10:00:00").build();
        Message msg2 = new Message.Builder("Message 2", "Bob").timestamp("2024-12-10 10:01:00").build();
        
        originalConv.addMessage(msg1);
        originalConv.addMessage(msg2);
        originalConv.save();
        
        // Load conversation from database
        Map<String, String> convData = db.retrieve("conversations", "conversation_id", originalConv.getConversationId());
        assertNotNull(convData);
        
        Conversation loadedConv = Conversation.fromDatabase(convData);
        
        // Verify loaded conversation
        assertNotNull(loadedConv);
        assertEquals(originalConv.getConversationId(), loadedConv.getConversationId());
        assertEquals(userId, loadedConv.getUserId());
        assertEquals(date, loadedConv.getDate());
        
        // Verify messages were loaded
        assertEquals(2, loadedConv.getMessages().size());
        assertEquals("Message 1", loadedConv.getMessages().get(0).getContent());
        assertEquals("Message 2", loadedConv.getMessages().get(1).getContent());
    }
    
    @Test
    public void testSaveConversationWithManyMessages() {
        String userId = "test-user-" + System.currentTimeMillis();
        Conversation conv = Conversation.create(userId, "2024-12-10");
        
        // Add 20 messages
        for (int i = 0; i < 20; i++) {
            Message msg = new Message.Builder("Message " + i, (i % 2 == 0) ? "Alice" : "Bob")
                    .messageId(UUID.randomUUID().toString())
                    .timestamp("2024-12-10 10:00:00")
                    .build();
            conv.addMessage(msg);
        }
        
        conv.save();
        
        // Load and verify
        Map<String, String> convData = db.retrieve("conversations", "conversation_id", conv.getConversationId());
        Conversation loadedConv = Conversation.fromDatabase(convData);
        
        assertEquals(20, loadedConv.getMessages().size());
    }
    
    @Test
    public void testSaveEmptyConversation() {
        String userId = "test-user-" + System.currentTimeMillis();
        Conversation conv = Conversation.create(userId, "2024-12-10");
        
        // Save conversation with no messages
        assertDoesNotThrow(() -> conv.save());
        
        // Load and verify
        Map<String, String> convData = db.retrieve("conversations", "conversation_id", conv.getConversationId());
        Conversation loadedConv = Conversation.fromDatabase(convData);
        
        assertEquals(0, loadedConv.getMessages().size());
    }
    
    // ========================================
    // Message Retrieval Tests
    // ========================================
    
    @Test
    public void testGetMessagesReturnsCorrectList() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        Message msg1 = new Message.Builder("Test 1", "Alice").timestamp("2024-12-10 10:00:00").build();
        Message msg2 = new Message.Builder("Test 2", "Bob").timestamp("2024-12-10 10:01:00").build();
        
        conv.addMessage(msg1);
        conv.addMessage(msg2);
        
        List<Message> messages = conv.getMessages();
        
        assertNotNull(messages);
        assertEquals(2, messages.size());
    }
    
    @Test
    public void testGetMessagesOnEmptyConversation() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        List<Message> messages = conv.getMessages();
        
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }
    
    // ========================================
    // Integration with Database Tests
    // ========================================
    
    @Test
    public void testMultipleConversationsSameUser() {
        String userId = "test-user-" + System.currentTimeMillis();
        
        Conversation conv1 = Conversation.create(userId, "2024-12-10");
        Conversation conv2 = Conversation.create(userId, "2024-12-11");
        
        Message msg1 = new Message.Builder("Day 1", "Alice").timestamp("2024-12-10 10:00:00").build();
        Message msg2 = new Message.Builder("Day 2", "Bob").timestamp("2024-12-11 10:00:00").build();
        
        conv1.addMessage(msg1);
        conv2.addMessage(msg2);
        
        conv1.save();
        conv2.save();
        
        // Both should be saved independently
        Map<String, String> conv1Data = db.retrieve("conversations", "conversation_id", conv1.getConversationId());
        Map<String, String> conv2Data = db.retrieve("conversations", "conversation_id", conv2.getConversationId());
        
        assertNotNull(conv1Data);
        assertNotNull(conv2Data);
        assertEquals("2024-12-10", conv1Data.get("date"));
        assertEquals("2024-12-11", conv2Data.get("date"));
    }
    
    @Test
    public void testConversationMessageCount() {
        String userId = "test-user-" + System.currentTimeMillis();
        Conversation conv = Conversation.create(userId, "2024-12-10");
        
        for (int i = 0; i < 5; i++) {
            Message msg = new Message.Builder("Message " + i, "Alice")
                    .messageId(UUID.randomUUID().toString())
                    .timestamp("2024-12-10 10:00:00")
                    .build();
            conv.addMessage(msg);
        }
        
        conv.save();
        
        // Check message count in database
        Map<String, String> convData = db.retrieve("conversations", "conversation_id", conv.getConversationId());
        assertEquals("5", convData.get("message_count"));
    }
    
    // ========================================
    // Edge Cases
    // ========================================
    
    @Test
    public void testConversationWithLongMessage() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        // Create a very long message
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("This is a long message. ");
        }
        
        Message longMsg = new Message.Builder(longContent.toString(), "Alice")
                .messageId(UUID.randomUUID().toString())
                .timestamp("2024-12-10 10:00:00")
                .build();
        
        conv.addMessage(longMsg);
        
        assertDoesNotThrow(() -> conv.save());
        
        // Load and verify
        Map<String, String> convData = db.retrieve("conversations", "conversation_id", conv.getConversationId());
        Conversation loadedConv = Conversation.fromDatabase(convData);
        
        assertEquals(1, loadedConv.getMessages().size());
        assertTrue(loadedConv.getMessages().get(0).getContent().length() > 1000);
    }
    
    @Test
    public void testConversationWithSpecialCharacters() {
        Conversation conv = Conversation.create("test-user", "2024-12-10");
        
        Message msg = new Message.Builder("Special chars: @#$%^&*()_+-=[]{}|;':\",./<>?", "Alice")
                .messageId(UUID.randomUUID().toString())
                .timestamp("2024-12-10 10:00:00")
                .build();
        
        conv.addMessage(msg);
        conv.save();
        
        Map<String, String> convData = db.retrieve("conversations", "conversation_id", conv.getConversationId());
        Conversation loadedConv = Conversation.fromDatabase(convData);
        
        assertTrue(loadedConv.getMessages().get(0).getContent().contains("@#$"));
    }
}
