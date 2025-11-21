// Account.java - Account Management with Secure Password Hashing

package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

/**
 * Account class handles user account creation and management
 * Implements secure password hashing using BCrypt
 */
public class Account {
    
    private String accountId;
    private String userId;
    private String username;
    private String passwordHash;  // NEVER store plain text password!
    private String email;
    private boolean isActive;
    
    // Constructor
    public Account(String accountId, String userId, String username, 
                   String passwordHash, String email, boolean isActive) {
        this.accountId = accountId;
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.isActive = isActive;
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
        
        // Return Account object
        return new Account(accountId, userId, username, passwordHash, email, true);
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
     * Load account from database by username
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
        
        return new Account(
            data.get("account_id"),
            data.get("user_id"),
            data.get("username"),
            data.get("password_hash"),
            data.get("email"),
            data.get("is_active").equals("1")
        );
    }
    
    /**
     * Load account from database by account ID
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
        
        return new Account(
            data.get("account_id"),
            data.get("user_id"),
            data.get("username"),
            data.get("password_hash"),
            data.get("email"),
            data.get("is_active").equals("1")
        );
    }
    
    /**
     * Validate account data
     * Checks if all required fields are present and valid
     * 
     * @return true if valid
     */
    public boolean validate() {
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
