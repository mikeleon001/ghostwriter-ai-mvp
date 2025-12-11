// User.java - User Management and Core Operations


package com.ghostwriter.user;

import com.ghostwriter.database.Database;
import com.ghostwriter.message.*;
import com.ghostwriter.analysis.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User class handles all user-related operations.
 * Registration, login, file uploads, and summary viewing.
 * 
 * Design Pattern: BUILDER PATTERN
 * Purpose: Simplify object construction with many optional parameters.
 * Usage:
 *   User user = new User.Builder(username)
 *       .userId(id)
 *       .email(email)
 *       .createdDate(date)
 *       .build();
 *
 * @author Mihail Chitorog
 * @version 2.0
 */
public class User {
    
    private String userId;
    private String username;
    private String email;
    private Date createdDate;

    // ============================================
    // BUILDER PATTERN IMPLEMENTATION
    // ============================================

    /**
     * Builder class for constructing User objects.
     * Implements the Builder design pattern to handle multiple optional parameters.
     */
    public static class Builder {
        // Required parameters
        private final String username;

        // Optional parameters with defaults
        private String userId = UUID.randomUUID().toString();
        private String email = "";
        private Date createdDate = new Date();

        /**
         * Create a Builder with required parameters.
         *
         * @param username The username (required)
         */
        public Builder(String username) {
            this.username = username;
        }

        /**
         * Set custom user ID (optional).
         */
        public Builder userId(String userId) {
            this.userId = userId;
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
         * Set created date (optional, defaults to now).
         */
        public Builder createdDate(Date createdDate) {
            this.createdDate = createdDate != null ? createdDate : new Date();
            return this;
        }

        /**
         * Build the User object.
         *
         * @return A new User instance
         */
        public User build() {
            return new User(this);
        }
    }

    /**
     * Private constructor used by Builder.
     */
    private User(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.email = builder.email;
        this.createdDate = builder.createdDate;
    }

    /**
     * Constructor for loading user by username from database.
     */
    public User(String username) {
        this.username = username;
        // Load user data from database
        loadUserData();
    }
    
    /**
     * Register new user
     * Creates both User and Account records
     * 
     * @param username Username
     * @param email Email address
     * @param password Plain text password (will be hashed)
     * @return User object
     * @throws Exception if registration fails
     */
    public static User register(String username, String email, String password) throws Exception {
        
        // Validate inputs
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
        
        // Generate unique user ID
        String userId = UUID.randomUUID().toString();
        
        // Create User record
        Database db = Database.getInstance();
        Map<String, String> userData = new HashMap<>();
        userData.put("user_id", userId);
        userData.put("username", username);
        userData.put("email", email);
        userData.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        db.store("users", userData);
        
        // Create Account record (with hashed password)
        Account.create(userId, username, email, password);
        
        System.out.println("✅ User registered successfully: " + username);
        
        // Return User object using Builder pattern
        return new User.Builder(username)
                .userId(userId)
                .email(email)
                .createdDate(new Date())
                .build();
    }
    
    /**
     * Login user
     * Validates credentials and generates authentication token
     * 
     * @param username Username
     * @param password Plain text password
     * @return Authentication token
     * @throws Exception if login fails
     */
    public static String login(String username, String password) throws Exception {
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        // Load account
        Account account = Account.loadByUsername(username);
        
        if (account == null) {
            throw new Exception("User not found");
        }
        
        // Verify password
        if (!Account.verifyPassword(password, account.getPasswordHash())) {
            throw new Exception("Invalid password");
        }
        
        // Check if account is active
        if (!account.isActive()) {
            throw new Exception("Account is deactivated");
        }
        
        // Generate authentication token
        String token = Authentication.authenticate(username, password);
        
        System.out.println("✅ Login successful: " + username);
        
        return token;
    }
    
    /**
     * Upload conversations from file
     * Parses file and stores messages in database
     * 
     * @param file File to upload
     * @return Number of messages processed
     * @throws Exception if upload fails
     */
    public int uploadConversations(File file) throws Exception {
        
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File not found");
        }
        
        if (file.length() == 0) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.length() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File exceeds 10MB limit");
        }
        
        System.out.println("📤 Uploading conversations from: " + file.getName());
        
        // Read file content
        String content = readFileContent(file);
        
        // Determine file type and get appropriate parser
        String fileName = file.getName().toLowerCase();
        MessageParser parser;
        
        if (fileName.contains("whatsapp") || fileName.contains("chat")) {
            parser = MessageParserFactory.getParser("whatsapp");
        } else {
            // Default to WhatsApp format
            parser = MessageParserFactory.getParser("whatsapp");
        }
        
        // Parse messages
        List<Message> messages = parser.parse(content);
        
        if (messages.isEmpty()) {
            throw new Exception("No messages found in file");
        }
        
        // Get today's date
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // Create or get conversation for today
        Conversation conversation = getOrCreateConversation(today);
        
        // Add all messages to conversation
        for (Message message : messages) {
            conversation.addMessage(message);
        }

        conversation.save();

        System.out.println("✅ Processed " + messages.size() + " messages");
        
        return messages.size();
    }
    
    /**
     * Generate summary for today's conversations
     * 
     * @return Summary object
     * @throws Exception if generation fails
     */
    public Summary generateSummaryForToday() throws Exception {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return generateSummaryForDate(today);
    }
    
    /**
     * Generate summary for specific date
     * 
     * @param date Date string (yyyy-MM-dd)
     * @return Summary object
     * @throws Exception if generation fails
     */
    public Summary generateSummaryForDate(String date) throws Exception {
        
        // Get conversation for this date
        Conversation conversation = getConversation(date);
        
        if (conversation == null) {
            throw new Exception("No conversations found for " + date);
        }
        
        List<Message> messages = conversation.getMessages();
        
        if (messages.isEmpty()) {
            throw new Exception("No messages found for " + date);
        }
        
        System.out.println("📊 Analyzing " + messages.size() + " messages...");
        
        // Analyze conversation
        Analyzer analyzer = new Analyzer();
        AnalysisResult analysis = analyzer.analyzeConversation(conversation);
        
        // Generate summary
        Summary summary = Summary.generate(this.userId, conversation.getConversationId(), 
                                          date, analysis);
        
        // Save summary
        summary.saveSummary();
        
        System.out.println("✅ Summary generated successfully!");
        
        return summary;
    }
    
    /**
     * View summary for specific date
     * 
     * @param date Date to view
     * @return Summary object or null if not found
     */
    public Summary viewSummary(Date date) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        
        Database db = Database.getInstance();
        
        // Query for summary
        String sql = "SELECT * FROM summaries WHERE user_id = ? AND date = ?";
        List<Map<String, String>> results = db.query(sql, this.userId, dateStr);
        
        if (results.isEmpty()) {
            return null;
        }
        
        Map<String, String> data = results.get(0);
        
        // Reconstruct Summary object
        return Summary.fromDatabase(data);
    }
    
    /**
     * Get or create conversation for specific date
     */
    private Conversation getOrCreateConversation(String date) {
        Conversation conv = getConversation(date);
        if (conv == null) {
            conv = Conversation.create(this.userId, date);
        }
        return conv;
    }
    
    /**
     * Get conversation for specific date
     */
    private Conversation getConversation(String date) {
        Database db = Database.getInstance();
        
        String sql = "SELECT * FROM conversations WHERE user_id = ? AND date = ?";
        List<Map<String, String>> results = db.query(sql, this.userId, date);
        
        if (results.isEmpty()) {
            return null;
        }
        
        Map<String, String> data = results.get(0);
        return Conversation.fromDatabase(data);
    }
    
    /**
     * Read file content as string
     */
    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        return content.toString();
    }
    
    /**
     * Load user data from database
     */
    private void loadUserData() {
        Database db = Database.getInstance();
        Map<String, String> userData = db.retrieve("users", "username", this.username);
        
        if (userData != null) {
            this.userId = userData.get("user_id");
            this.email = userData.get("email");
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.createdDate = sdf.parse(userData.get("created_date"));
            } catch (Exception e) {
                this.createdDate = new Date();
            }
        }
    }
    
    // Validation methods
    private static void validateUsername(String username) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        
        if (username.length() > 50) {
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
    }
    
    private static void validateEmail(String email) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    private static void validatePassword(String password) throws IllegalArgumentException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
    
    // Getters
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
