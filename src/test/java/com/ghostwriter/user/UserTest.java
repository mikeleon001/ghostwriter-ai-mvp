package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    
    private static Database db;
    
    @BeforeAll
    public static void setup() {
        db = Database.getInstance();
        db.connect();
    }
    
    @Test
    public void testUserRegistration() throws Exception {
        String username = "test_user_" + System.currentTimeMillis();
        String email = "test" + System.currentTimeMillis() + "@example.com";
        String password = "SecurePass123!";
        
        User user = User.register(username, email, password);
        
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertNotNull(user.getUserId());
    }
    
    @Test
    public void testUserLogin() throws Exception {
        // First register a user
        String username = "login_test_" + System.currentTimeMillis();
        String email = "login" + System.currentTimeMillis() + "@example.com";
        String password = "TestPass123!";
        
        User.register(username, email, password);
        
        // Now try to login
        String token = User.login(username, password);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // Validate the token
        assertTrue(Authentication.validateToken(token));
    }
    
    @Test
    public void testInvalidLogin() {
        assertThrows(Exception.class, () -> {
            User.login("nonexistent_user", "wrongpassword");
        });
    }
    
    @Test
    public void testTokenExpiration() throws Exception {
        String token = Authentication.generateToken("test-user-id");
        
        // Token should be valid immediately
        assertTrue(Authentication.validateToken(token));
        
        // Logout should invalidate token
        Authentication.logout(token);
        
        // Token should now be invalid
        assertThrows(Exception.class, () -> {
            Authentication.validateToken(token);
        });
    }
}
