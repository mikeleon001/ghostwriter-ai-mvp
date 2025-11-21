// Database.java - SINGLETON PATTERN IMPLEMENTATION

package com.ghostwriter.database;

import java.sql.*;
import java.util.*;

/**
 * Database class implementing SINGLETON PATTERN
 * Ensures only ONE database connection exists throughout the application
 * 
 * Design Pattern: Singleton
 * Purpose: Prevent multiple database connections that could cause conflicts
 */
public class Database {
    
    // SINGLETON: Private static instance
    private static Database instance = null;
    
    // Database connection
    private Connection connection = null;
    
    // Database file path
    private static final String DB_PATH = "ghostwriter.db";
    
    /**
     * Private constructor - prevents external instantiation
     * This is KEY to Singleton pattern!
     */
    private Database() {
        // Constructor is private - can only be called from getInstance()
    }
    
    /**
     * SINGLETON PATTERN: Get the single instance
     * Thread-safe implementation with double-checked locking
     * 
     * @return The single Database instance
     */
    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }
    
    /**
     * Connect to SQLite database
     * Creates database file if it doesn't exist
     */
    public void connect() {
        try {
            // SQLite connection string
            String url = "jdbc:sqlite:" + DB_PATH;
            connection = DriverManager.getConnection(url);
            
            System.out.println("Database connection established (Singleton instance)");
            
            // Initialize tables if they don't exist
            initializeTables();
            
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize database tables
     * Creates all required tables if they don't exist
     */
    private void initializeTables() {
        try {
            Statement stmt = connection.createStatement();
            
            // Users table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT PRIMARY KEY, " +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "created_date TEXT NOT NULL" +
                ")"
            );
            
            // Accounts table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_id TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "is_active INTEGER DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")"
            );
            
            // Sessions table (for authentication tokens)
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS sessions (" +
                "session_id TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "token TEXT UNIQUE NOT NULL, " +
                "expires_at TEXT NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")"
            );
            
            // Conversations table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS conversations (" +
                "conversation_id TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "message_count INTEGER DEFAULT 0, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ")"
            );
            
            // Messages table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS messages (" +
                "message_id TEXT PRIMARY KEY, " +
                "conversation_id TEXT NOT NULL, " +
                "sender TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "timestamp TEXT NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY (conversation_id) REFERENCES conversations(conversation_id)" +
                ")"
            );
            
            // Summaries table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS summaries (" +
                "summary_id TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "conversation_id TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "key_topics TEXT, " +
                "action_items TEXT, " +
                "statistics TEXT, " +
                "summary_text TEXT, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                "FOREIGN KEY (conversation_id) REFERENCES conversations(conversation_id)" +
                ")"
            );
            
            System.out.println("Database tables initialized successfully");
            
        } catch (SQLException e) {
            System.out.println("Failed to initialize tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Store data in database
     * Generic method for INSERT operations
     * 
     * @param table Table name
     * @param data Map of column names to values
     * @return ID of inserted record
     */
    public String store(String table, Map<String, String> data) {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<String> values = new ArrayList<>();
        
        // Build SQL query dynamically
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(entry.getKey());
            placeholders.append("?");
            values.add(entry.getValue());
        }
        
        String sql = String.format(
            "INSERT INTO %s (%s) VALUES (%s)",
            table, columns.toString(), placeholders.toString()
        );
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            
            // Set values
            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }
            
            pstmt.executeUpdate();
            
            // Return the ID (assumes first column is ID)
            return values.get(0);
            
        } catch (SQLException e) {
            System.out.println("Store failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Retrieve data from database
     * Generic method for SELECT operations
     * 
     * @param table Table name
     * @param key Column name to search by
     * @param value Value to search for
     * @return Map of column names to values, or null if not found
     */
    public Map<String, String> retrieve(String table, String key, String value) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", table, key);
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, value);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Map<String, String> result = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                
                // Get all columns
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String columnName = meta.getColumnName(i);
                    String columnValue = rs.getString(i);
                    result.put(columnName, columnValue);
                }
                
                return result;
            }
            
            return null;
            
        } catch (SQLException e) {
            System.out.println("Retrieve failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Query database with custom SQL
     * For complex queries
     * 
     * @param sql SQL query string
     * @param params Query parameters
     * @return List of result maps
     */
    public List<Map<String, String>> query(String sql, String... params) {
        List<Map<String, String>> results = new ArrayList<>();
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            
            // Process all rows
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String columnName = meta.getColumnName(i);
                    String columnValue = rs.getString(i);
                    row.put(columnName, columnValue);
                }
                results.add(row);
            }
            
            return results;
            
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            e.printStackTrace();
            return results;
        }
    }
    
    /**
     * Update data in database
     * 
     * @param table Table name
     * @param key Column name to identify record
     * @param keyValue Value to identify record
     * @param data Map of columns to update
     * @return true if successful
     */
    public boolean update(String table, String key, String keyValue, Map<String, String> data) {
        StringBuilder setClause = new StringBuilder();
        List<String> values = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            values.add(entry.getValue());
        }
        
        String sql = String.format(
            "UPDATE %s SET %s WHERE %s = ?",
            table, setClause.toString(), key
        );
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            
            // Set update values
            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }
            
            // Set WHERE value
            pstmt.setString(values.size() + 1, keyValue);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete data from database
     * 
     * @param table Table name
     * @param key Column name
     * @param value Value to match
     * @return true if successful
     */
    public boolean delete(String table, String key, String value) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", table, key);
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, value);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }
    
    /**
     * Get connection (for advanced operations)
     * Use sparingly - prefer the helper methods above
     */
    public Connection getConnection() {
        return connection;
    }
}
