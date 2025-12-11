// Account.java - Account Management with Secure Password Hashing

package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

/**
 * Account class handles user account creation and management.
 * Implements secure password hashing using BCrypt.
 * 
 * Design Pattern: BUILDER PATTERN
 * Purpose: Simplify object construction with many optional parameters.
 * Usage:
 *   Account account = new Account.Builder(userId, username)
 *       .accountId(id)
 *       .passwordHash(hash)
 *       .email(email)
 *       .isActive(true)
 *       .build();
 *
 * @author Mihail Chitorog
 * @version 2.0
 */
public class Account {
    
    private String accountId;
    private String userId;
    private String username;
    private String passwordHash;  // NEVER store plain text password!
    private String email;
    private boolean isActive;

    // ============================================
    // BUILDER PATTERN IMPLEMENTATION
    // ============================================

    /**
     * Builder class for constructing Account objects.
     * Implements the Builder design pattern to handle multiple optional parameters.
     */
    public static class Builder {
        // Required parameters
        private final String userId;
        private final String username;

        // Optional parameters with defaults
        private String accountId = UUID.randomUUID().toString();
        private String passwordHash = "";
        private String email = "";
        private boolean isActive = true;

        /**
         * Create a Builder with required parameters.
         *
         * @param userId The user ID (required)
         * @param username The username (required)
         */
        public Builder(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        /**
         * Set custom account ID (optional).
         */
        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        /**
         * Set password hash (optional).
         */
        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        /**
         * Set email address (optional).
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Set active status (optional, defaults to true).
         */
        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        /**
         * Build the Account object.
         *
         * @return A new Account instance
         */
        public Account build() {
            return new Account(this);
        }
    }

    /**
     * Private constructor used by Builder.
     */
    private Account(Builder builder) {
        this.accountId = builder.accountId;
        this.userId = builder.userId;
        this.username = builder.username;
        this.passwordHash = builder.passwordHash;
        this.email = builder.email;
        this.isActive = builder.isActive;
    }
    
    /**
     * Create new account with hashed password
     * This is the SECURE way to store passwords!
     * 
     * @param userId User ID to link account to
     * @param username Username
     * @param email Email address
     * @param password Plain text password (will be hashed)
     * @return Account object
     * @throws Exception if account creation fails
     */
    public static Account create(String userId, String username, 
                                 String email, String password) throws Exception {
        
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        
        // Check if username already exists
        Database db = Database.getInstance();
        Map<String, String> existingAccount = db.retrieve("accounts", "username", username);
        
        if (existingAccount != null) {
            throw new Exception("Username already exists");
        }
        
        // Check if email already exists
        existingAccount = db.retrieve("accounts", "email", email);
        
        if (existingAccount != null) {
            throw new Exception("Email already registered");
        }
        
        // Generate unique account ID
        String accountId = UUID.randomUUID().toString();
        
        // HASH THE PASSWORD - This is the critical security step!
        String passwordHash = hashPassword(password);
        
        // Store account in database
        Map<String, String> accountData = new HashMap<>();
        accountData.put("account_id", accountId);
        accountData.put("user_id", userId);
        accountData.put("username", username);
        accountData.put("password_hash", passwordHash);  // Store HASH, not password!
        accountData.put("email", email);
        accountData.put("is_active", "1");
        
        db.store("accounts", accountData);
        
        System.out.println("Account created successfully for: " + username);
        
        // Return Account object using Builder pattern
        return new Account.Builder(userId, username)
                .accountId(accountId)
                .passwordHash(passwordHash)
                .email(email)
                .isActive(true)
                .build();
    }
    
    /**
     * Hash password using BCrypt
     * BCrypt automatically handles salting
     * 
     * @param plainPassword Plain text password
     * @return Hashed password
     */
    public static String hashPassword(String plainPassword) {
        // BCrypt.gensalt() generates a random salt
        // BCrypt.hashpw() combines salt + password and hashes them
        // Result is a 60-character string like: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    /**
     * Verify password against stored hash
     * Used during login
     * 
     * @param plainPassword Password user entered
     * @param hashedPassword Stored password hash
     * @return true if password matches
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // BCrypt.checkpw() hashes the plain password with the same salt
        // and compares it to the stored hash
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    /**
     * Load account from database by username.
     * 
     * @param username Username
     * @return Account object or null if not found
     */
    public static Account loadByUsername(String username) {
        Database db = Database.getInstance();
        Map<String, String> data = db.retrieve("accounts", "username", username);
        
        if (data == null) {
            return null;
        }
        
        return new Account.Builder(data.get("user_id"), data.get("username"))
                .accountId(data.get("account_id"))
                .passwordHash(data.get("password_hash"))
                .email(data.get("email"))
                .isActive(data.get("is_active").equals("1"))
                .build();
    }
    
    /**
     * Load account from database by account ID.
     * 
     * @param accountId Account ID
     * @return Account object or null if not found
     */
    public static Account loadById(String accountId) {
        Database db = Database.getInstance();
        Map<String, String> data = db.retrieve("accounts", "account_id", accountId);
        
        if (data == null) {
            return null;
        }
        
        return new Account.Builder(data.get("user_id"), data.get("username"))
                .accountId(data.get("account_id"))
                .passwordHash(data.get("password_hash"))
                .email(data.get("email"))
                .isActive(data.get("is_active").equals("1"))
                .build();
    }
    
    /**
     * Validate account data
     * Checks if all required fields are present and valid.
     * 
     * @return true if valid
     */
    public boolean isValid() {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        if (!email.contains("@")) {
            return false;
        }
        
        if (passwordHash == null || passwordHash.isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Update account information
     * 
     * @param field Field name to update
     * @param value New value
     * @return true if successful
     */
    public boolean update(String field, String value) {
        Database db = Database.getInstance();
        
        Map<String, String> updateData = new HashMap<>();
        
        if (field.equals("email")) {
            this.email = value;
            updateData.put("email", value);
        } else if (field.equals("password")) {
            // Hash new password before storing
            this.passwordHash = hashPassword(value);
            updateData.put("password_hash", this.passwordHash);
        } else {
            throw new IllegalArgumentException("Cannot update field: " + field);
        }
        
        return db.update("accounts", "account_id", accountId, updateData);
    }
    
    /**
     * Deactivate account
     * 
     * @return true if successful
     */
    public boolean deactivate() {
        Database db = Database.getInstance();
        
        Map<String, String> updateData = new HashMap<>();
        updateData.put("is_active", "0");
        
        boolean success = db.update("accounts", "account_id", accountId, updateData);
        
        if (success) {
            this.isActive = false;
        }
        
        return success;
    }
    
    // Getters
    public String getAccountId() {
        return accountId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
