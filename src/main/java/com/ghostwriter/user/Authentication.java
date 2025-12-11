// Authentication.java - Session and Token Management

package com.ghostwriter.user;

import com.ghostwriter.database.Database;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Authentication class handles user sessions and token management
 * Generates and validates authentication tokens for secure access
 */
public class Authentication {
    
    private String sessionId;
    private String userId;
    private String token;
    private Date expiresAt;
    private Date createdAt;
    
    // Token expiration time (24 hours)
    private static final long TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000;
    
    // Constructor
    public Authentication(String sessionId, String userId, String token, 
                         Date expiresAt, Date createdAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }
    
    /**
     * Authenticate user and generate token
     * 
     * @param username Username
     * @param password Plain text password
     * @return Authentication token
     * @throws Exception if authentication fails
     */
    public static String authenticate(String username, String password) throws Exception {
        
        // Load account
        Account account = Account.loadByUsername(username);
        
        if (account == null) {
            throw new Exception("Authentication failed: Invalid credentials");
        }
        
        // Verify password
        if (!Account.verifyPassword(password, account.getPasswordHash())) {
            throw new Exception("Authentication failed: Invalid credentials");
        }
        
        // Check if account is active
        if (!account.isActive()) {
            throw new Exception("Authentication failed: Account inactive");
        }
        
        // Generate token
        String token = generateToken(account.getUserId());
        
        return token;
    }
    
    /**
     * Generate authentication token for user
     * Creates a session in the database
     * 
     * @param userId User ID
     * @return Authentication token
     */
    public static String generateToken(String userId) {
        
        // Generate unique session ID
        String sessionId = UUID.randomUUID().toString();
        
        // Generate unique token (combination of UUID and timestamp for uniqueness)
        String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
        
        // Calculate expiration time (24 hours from now)
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + TOKEN_EXPIRATION_MS);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // Store session in database
        Database db = Database.getInstance();
        Map<String, String> sessionData = new HashMap<>();
        sessionData.put("session_id", sessionId);
        sessionData.put("user_id", userId);
        sessionData.put("token", token);
        sessionData.put("expires_at", sdf.format(expiresAt));
        sessionData.put("created_at", sdf.format(now));
        
        db.store("sessions", sessionData);
        
        System.out.println("🔑 Token generated for user: " + userId);
        System.out.println("⏰ Token expires at: " + sdf.format(expiresAt));
        
        return token;
    }
    
    /**
     * Validate authentication token
     * Checks if token exists and is not expired
     * 
     * @param token Authentication token
     * @return true if valid
     * @throws Exception if invalid or expired
     */
    public static boolean validateToken(String token) throws Exception {
        
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        // Load session from database
        Database db = Database.getInstance();
        Map<String, String> sessionData = db.retrieve("sessions", "token", token);
        
        if (sessionData == null) {
            throw new Exception("Invalid token");
        }
        
        // Check expiration
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
            Date expiresAt = sdf.parse(sessionData.get("expires_at"));
            Date now = new Date();
            
            if (now.after(expiresAt)) {
                // Token expired - delete it
                db.delete("sessions", "token", token);
                throw new Exception("Token expired");
            }
            
            System.out.println("✅ Token validated successfully");
            return true;
            
        } catch (java.text.ParseException e) {
            throw new Exception("Invalid token format");
        }
    }
    
    /**
     * Get user ID from token
     * 
     * @param token Authentication token
     * @return User ID
     * @throws Exception if token invalid
     */
    public static String getUserIdFromToken(String token) throws Exception {
        
        // First validate the token
        validateToken(token);
        
        // Load session
        Database db = Database.getInstance();
        Map<String, String> sessionData = db.retrieve("sessions", "token", token);
        
        if (sessionData == null) {
            throw new Exception("Invalid token");
        }
        
        return sessionData.get("user_id");
    }
    
    /**
     * Logout user by invalidating token
     * Deletes session from database
     * 
     * @param token Authentication token
     * @return true if successful
     */
    public static boolean logout(String token) {
        
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        Database db = Database.getInstance();
        boolean deleted = db.delete("sessions", "token", token);
        
        if (deleted) {
            System.out.println("👋 User logged out successfully");
        }
        
        return deleted;
    }
    
    /**
     * Clean up expired sessions
     * Should be run periodically to remove old sessions
     * 
     * @return Number of sessions deleted
     */
    public static int cleanupExpiredSessions() {
        Database db = Database.getInstance();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(new Date());
        
        // Query for expired sessions
        String sql = "SELECT session_id FROM sessions WHERE expires_at < ?";
        List<Map<String, String>> expiredSessions = db.query(sql, now);
        
        // Delete each expired session
        int deletedCount = 0;
        for (Map<String, String> session : expiredSessions) {
            boolean deleted = db.delete("sessions", "session_id", session.get("session_id"));
            if (deleted) {
                deletedCount++;
            }
        }
        
        if (deletedCount > 0) {
            System.out.println("🧹 Cleaned up " + deletedCount + " expired sessions");
        }
        
        return deletedCount;
    }
    
    /**
     * Load authentication object from database
     * 
     * @param token Token to load
     * @return Authentication object or null if not found
     */
    public static Authentication loadByToken(String token) {
        Database db = Database.getInstance();
        Map<String, String> data = db.retrieve("sessions", "token", token);
        
        if (data == null) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
            Date expiresAt = sdf.parse(data.get("expires_at"));
            Date createdAt = sdf.parse(data.get("created_at"));
            
            return new Authentication(
                data.get("session_id"),
                data.get("user_id"),
                data.get("token"),
                expiresAt,
                createdAt
            );
            
        } catch (java.text.ParseException e) {
            return null;
        }
    }
    
    /**
     * Check if session is expired
     * 
     * @return true if expired
     */
    public boolean isExpired() {
        Date now = new Date();
        return now.after(expiresAt);
    }
    
    /**
     * Extend session expiration by 24 hours
     * Useful for "remember me" functionality
     * 
     * @return true if successful
     */
    public boolean extendSession() {
        Date newExpiresAt = new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        Database db = Database.getInstance();
        Map<String, String> updateData = new HashMap<>();
        updateData.put("expires_at", sdf.format(newExpiresAt));
        
        boolean success = db.update("sessions", "session_id", sessionId, updateData);
        
        if (success) {
            this.expiresAt = newExpiresAt;
            System.out.println("⏰ Session extended to: " + sdf.format(newExpiresAt));
        }
        
        return success;
    }
    
    // Getters
    public String getSessionId() {
        return sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public Date getExpiresAt() {
        return expiresAt;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Authentication{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", expiresAt=" + sdf.format(expiresAt) +
                ", isExpired=" + isExpired() +
                '}';
    }
}
