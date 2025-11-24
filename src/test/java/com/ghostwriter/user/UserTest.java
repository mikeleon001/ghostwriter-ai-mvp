package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserTest - User registration and validation
 */
public class UserTest {
    
    private static Database db;
    
    @BeforeAll
    public static void setupDatabase() {
        db = Database.getInstance();
        db.connect();
    }
    
    @Test
    public void testUserRegistration() throws Exception {
        String username = "test_user_" + System.currentTimeMillis();
        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "TestPassword123!";
        
        User user = User.register(username, email, password);
        
        assertNotNull(user, "User should not be null");
        assertNotNull(user.getUserId(), "User ID should be generated");
        assertEquals(username, user.getUsername(), "Username should match");
        assertEquals(email, user.getEmail(), "Email should match");
    }
    
    @Test
    public void testUserRegistrationWithInvalidEmail() {
        String username = "testuser" + System.currentTimeMillis();
        String invalidEmail = "notanemail";
        String password = "TestPassword123!";
        
        assertThrows(Exception.class, () -> {
            User.register(username, invalidEmail, password);
        }, "Registration with invalid email should throw exception");
    }
}
