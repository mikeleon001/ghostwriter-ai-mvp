package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    
    @BeforeAll
    public static void setup() {
        Database db = Database.getInstance();
        db.connect();
    }
    
    @Test
    public void testPasswordHashing() {
        String password = "MySecurePassword123!";
        String hash = Account.hashPassword(password);
        
        // Hash should not equal plain password
        assertNotEquals(password, hash);
        
        // Hash should be 60 characters (BCrypt standard)
        assertEquals(60, hash.length());
        
        // Should start with $2a$ (BCrypt identifier)
        assertTrue(hash.startsWith("$2a$"));
    }
    
    @Test
    public void testPasswordVerification() {
        String password = "TestPassword123!";
        String hash = Account.hashPassword(password);
        
        // Correct password should verify
        assertTrue(Account.verifyPassword(password, hash));
        
        // Wrong password should not verify
        assertFalse(Account.verifyPassword("WrongPassword", hash));
    }
}
