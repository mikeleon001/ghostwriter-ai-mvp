package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthenticationTest - Core authentication functionality tests.
 * 
 * Focuses on what can be reliably tested in unit test environment.
 *
 * @author Mihail Chitorog
 */
public class AuthenticationTest {
    
    private static Database db;
    
    @BeforeAll
    public static void setupDatabase() {
        db = Database.getInstance();
        db.connect();
    }
    
    // ========================================
    // Token Generation Tests
    // ========================================
    
    @Test
    public void testGenerateToken() {
        String userId = "test-user-" + System.currentTimeMillis();
        String token = Authentication.generateToken(userId);
        
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(token.length() > 20, "Token should be reasonably long");
    }
    
    @Test
    public void testGenerateUniqueTokens() {
        String userId = "test-user-" + System.currentTimeMillis();
        
        String token1 = Authentication.generateToken(userId);
        String token2 = Authentication.generateToken(userId);
        
        assertNotEquals(token1, token2, "Each token should be unique");
    }
    
    @Test
    public void testGenerateMultipleTokensForDifferentUsers() {
        String userId1 = "user1-" + System.currentTimeMillis();
        String userId2 = "user2-" + System.currentTimeMillis();
        
        String token1 = Authentication.generateToken(userId1);
        String token2 = Authentication.generateToken(userId2);
        
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }
    
    // ========================================
    // Authentication Error Handling
    // ========================================
    
    @Test
    public void testAuthenticateNonexistentUser() {
        String fakeUsername = "nonexistent_user_" + System.currentTimeMillis();
        
        Exception exception = assertThrows(Exception.class, () -> {
            Authentication.authenticate(fakeUsername, "anypassword");
        });
        
        assertTrue(exception.getMessage().contains("not found") || 
                   exception.getMessage().contains("Invalid"),
                   "Should fail with user not found or invalid credentials");
    }
    
    // ========================================
    // Session Cleanup
    // ========================================
    
    @Test
    public void testCleanupExpiredSessions() {
        // This should run without errors
        assertDoesNotThrow(() -> {
            int cleaned = Authentication.cleanupExpiredSessions();
            assertTrue(cleaned >= 0, "Cleanup should return non-negative count");
        });
    }
}
