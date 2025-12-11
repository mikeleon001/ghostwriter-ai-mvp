package com.ghostwriter.message;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * MessageParserTest - Tests for WhatsApp message parsing
 * 
 * Tests cover:
 * - Valid format parsing
 * - Malformed format handling
 * - Empty/null input handling
 * - Multi-line messages
 * - Special characters and emojis
 *
 * @author Mihail Chitorog
 */
public class MessageParserTest {
    
    private WhatsAppParser parser;
    
    @BeforeEach
    public void setUp() {
        parser = new WhatsAppParser();
    }
    
    // ========================================
    // Valid Format Tests
    // ========================================
    
    @Test
    public void testParseValidWhatsAppFormat() {
        String content = "[12/10/24, 2:31 PM] Alice: Hello, how are you?\n" +
                        "[12/10/24, 2:35 PM] Bob: I'm good, thanks!";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(2, messages.size());
        assertEquals("Alice", messages.get(0).getSender());
        assertEquals("Hello, how are you?", messages.get(0).getContent());
        assertEquals("Bob", messages.get(1).getSender());
        assertEquals("I'm good, thanks!", messages.get(1).getContent());
    }
    
    @Test
    public void testParseSingleMessage() {
        String content = "[12/10/24, 2:31 PM] Alice: Hello world";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(1, messages.size());
        assertEquals("Alice", messages.get(0).getSender());
        assertEquals("Hello world", messages.get(0).getContent());
    }
    
    @Test
    public void testParseMultipleMessagesFromSameSender() {
        String content = "[12/10/24, 9:00 AM] Alice: Good morning\n" +
                        "[12/10/24, 9:01 AM] Alice: How are you?\n" +
                        "[12/10/24, 9:02 AM] Alice: Ready for today?";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(3, messages.size());
        assertEquals("Alice", messages.get(0).getSender());
        assertEquals("Alice", messages.get(1).getSender());
        assertEquals("Alice", messages.get(2).getSender());
    }
    
    @Test
    public void testParseWithDifferentTimeFormats() {
        String content = "[12/10/24, 9:00 AM] Alice: Morning message\n" +
                        "[12/10/24, 2:30 PM] Bob: Afternoon message\n" +
                        "[12/10/24, 11:45 PM] Alice: Night message";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(3, messages.size());
        assertTrue(messages.get(0).getTimestamp().contains("09:00"));
        assertTrue(messages.get(1).getTimestamp().contains("14:30"));
        assertTrue(messages.get(2).getTimestamp().contains("23:45"));
    }
    
    // ========================================
    // Multi-line Message Tests
    // ========================================
    
    @Test
    public void testParseMultiLineMessage() {
        String content = "[12/10/24, 2:31 PM] Alice: This is a long message\n" +
                        "that spans multiple lines\n" +
                        "and should be combined into one message\n" +
                        "[12/10/24, 2:32 PM] Bob: Short reply";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).getContent().contains("long message"));
        assertTrue(messages.get(0).getContent().contains("multiple lines"));
        assertEquals("Short reply", messages.get(1).getContent());
    }
    
    // ========================================
    // Special Characters & Emojis
    // ========================================
    
    @Test
    public void testParseWithEmojis() {
        String content = "[12/10/24, 2:31 PM] Alice: Hello! 👋😊\n" +
                        "[12/10/24, 2:32 PM] Bob: Hi there! 🎉";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).getContent().contains("👋"));
        assertTrue(messages.get(1).getContent().contains("🎉"));
    }
    
    @Test
    public void testParseWithSpecialCharacters() {
        String content = "[12/10/24, 2:31 PM] Alice: Check out this link: https://example.com\n" +
                        "[12/10/24, 2:32 PM] Bob: What about @mentions and #hashtags?";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).getContent().contains("https://"));
        assertTrue(messages.get(1).getContent().contains("@mentions"));
    }
    
    @Test
    public void testParseWithPunctuation() {
        String content = "[12/10/24, 2:31 PM] Alice: Really?! That's amazing!!!\n" +
                        "[12/10/24, 2:32 PM] Bob: Yes... I think so.";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).getContent().contains("?!"));
        assertTrue(messages.get(1).getContent().contains("..."));
    }
    
    // ========================================
    // Edge Cases & Error Handling
    // ========================================
    
    @Test
    public void testParseEmptyString() {
        String content = "";
        
        List<Message> messages = parser.parse(content);
        
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }
    
    @Test
    public void testParseNullInput() {
        String content = null;
        
        List<Message> messages = parser.parse(content);
        
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }
    
    @Test
    public void testParseWhitespaceOnly() {
        String content = "   \n\n   \t  ";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(0, messages.size());
    }
    
    @Test
    public void testParseMalformedFormat() {
        String content = "This is not a valid WhatsApp format\n" +
                        "Just random text without proper formatting";
        
        List<Message> messages = parser.parse(content);
        
        // Should return empty list for malformed content
        assertEquals(0, messages.size());
    }
    
    @Test
    public void testParsePartiallyValidFormat() {
        String content = "Random text that doesn't match\n" +
                        "[12/10/24, 2:31 PM] Alice: Valid message\n" +
                        "More random text\n" +
                        "[12/10/24, 2:32 PM] Bob: Another valid message";
        
        List<Message> messages = parser.parse(content);
        
        // Should extract only the valid messages
        assertEquals(2, messages.size());
        assertEquals("Alice", messages.get(0).getSender());
        assertEquals("Bob", messages.get(1).getSender());
    }
    
    @Test
    public void testParseSystemMessages() {
        String content = "[12/10/24, 2:30 PM] Messages and calls are end-to-end encrypted\n" +
                        "[12/10/24, 2:31 PM] Alice: Real message\n" +
                        "[12/10/24, 2:32 PM] Alice created group \"Test Group\"";
        
        List<Message> messages = parser.parse(content);
        
        // System messages should be filtered out
        assertEquals(1, messages.size());
        assertEquals("Real message", messages.get(0).getContent());
    }
    
    // ========================================
    // Timestamp Parsing Tests
    // ========================================
    
    @Test
    public void testTimestampFormatConversion() {
        String content = "[12/10/24, 2:31 PM] Alice: Test message";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(1, messages.size());
        
        // Timestamp should be converted to standard format
        String timestamp = messages.get(0).getTimestamp();
        assertNotNull(timestamp);
        assertTrue(timestamp.contains("2024-12-10"));
        assertTrue(timestamp.contains("14:31")); // 2:31 PM = 14:31
    }
    
    @Test
    public void testMidnightTimeHandling() {
        String content = "[12/10/24, 12:00 AM] Alice: Midnight message";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).getTimestamp().contains("00:00")); // 12 AM = 00:00
    }
    
    @Test
    public void testNoonTimeHandling() {
        String content = "[12/10/24, 12:00 PM] Alice: Noon message";
        
        List<Message> messages = parser.parse(content);
        
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).getTimestamp().contains("12:00")); // 12 PM = 12:00
    }
    
    // ========================================
    // Factory Pattern Integration Test
    // ========================================
    
    @Test
    public void testFactoryCreatesWhatsAppParser() {
        MessageParser factoryParser = MessageParserFactory.getParser("whatsapp");
        
        assertNotNull(factoryParser);
        assertTrue(factoryParser instanceof WhatsAppParser);
        
        String content = "[12/10/24, 2:31 PM] Alice: Test";
        List<Message> messages = factoryParser.parse(content);
        
        assertEquals(1, messages.size());
    }
    
    @Test
    public void testFactorySupportsMultipleNames() {
        MessageParser parser1 = MessageParserFactory.getParser("whatsapp");
        MessageParser parser2 = MessageParserFactory.getParser("WhatsApp");
        MessageParser parser3 = MessageParserFactory.getParser("wa");
        
        assertNotNull(parser1);
        assertNotNull(parser2);
        assertNotNull(parser3);
    }
}
