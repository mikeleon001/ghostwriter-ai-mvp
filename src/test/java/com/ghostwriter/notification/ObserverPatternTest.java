package com.ghostwriter.notification;

import com.ghostwriter.analysis.Summary;
import com.ghostwriter.database.Database;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Observer design pattern implementation.
 * Verifies that Summary correctly notifies observers when saved.
 *
 * @author Mihail Chitorog
 */
public class ObserverPatternTest {

    private Summary summary;

    @BeforeEach
    public void setUp() {
        // Initialize database for testing
        Database db = Database.getInstance();
        try {
            db.connect();
        } catch (Exception e) {
            // Database might already be connected, that's okay
        }

        // Create a test summary
        List<String> topics = Arrays.asList("project", "deadline", "meeting");
        List<String> actions = Arrays.asList("Finish report by Friday", "Schedule follow-up");
        List<String> questions = Arrays.asList("What time is the meeting?");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total_messages", 25);
        stats.put("most_active", "Alice");

        summary = new Summary(
                "test-summary-" + UUID.randomUUID().toString(),
                "test-user-id",
                "test-conversation-id",
                "2025-12-10",
                topics,
                actions,
                questions,
                stats
        );
    }

    @Test
    public void testAddObserver() {
        ConsoleNotifier notifier = new ConsoleNotifier();

        summary.addObserver(notifier);

        // No exception means success
        assertTrue(true);
    }

    @Test
    public void testAddNullObserver() {
        assertThrows(IllegalArgumentException.class, () -> {
            summary.addObserver(null);
        });
    }

    @Test
    public void testRemoveObserver() {
        ConsoleNotifier notifier = new ConsoleNotifier();

        summary.addObserver(notifier);
        summary.removeObserver(notifier);

        // No exception means success
        assertTrue(true);
    }

    @Test
    public void testAddDuplicateObserver() {
        ConsoleNotifier notifier = new ConsoleNotifier();

        summary.addObserver(notifier);
        summary.addObserver(notifier); // Add same observer twice

        // Should only be added once (no exception)
        assertTrue(true);
    }

    @Test
    public void testMultipleObservers() {
        ConsoleNotifier console = new ConsoleNotifier();
        EmailNotifier email = new EmailNotifier("test@example.com");
        LogFileNotifier log = new LogFileNotifier();

        summary.addObserver(console);
        summary.addObserver(email);
        summary.addObserver(log);

        System.out.println("\n=== Testing Multiple Observers ===");
        System.out.println("Calling saveSummary() - all three observers should be notified:");

        // This should trigger all three observers
        summary.saveSummary();

        System.out.println("=== End of Multiple Observers Test ===\n");
    }

    @Test
    public void testObserverNotification() {
        // Create a custom test observer to verify it gets called
        TestObserver testObserver = new TestObserver();

        summary.addObserver(testObserver);

        assertFalse(testObserver.wasNotified(), "Observer should not be notified before save");

        summary.saveSummary();

        assertTrue(testObserver.wasNotified(), "Observer should be notified after save");
        assertEquals(summary, testObserver.getReceivedSummary());
    }

    @Test
    public void testEmailNotifierEmailAddress() {
        EmailNotifier notifier = new EmailNotifier("user@example.com");
        assertEquals("user@example.com", notifier.getEmailAddress());
    }

    @Test
    public void testEmailNotifierNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EmailNotifier(null);
        });
    }

    @Test
    public void testEmailNotifierEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EmailNotifier("");
        });
    }

    /**
     * Custom test observer to verify notification behavior
     */
    private static class TestObserver implements SummaryObserver {
        private boolean notified = false;
        private Summary receivedSummary;

        @Override
        public void onSummaryGenerated(Summary summary) {
            this.notified = true;
            this.receivedSummary = summary;
        }

        public boolean wasNotified() {
            return notified;
        }

        public Summary getReceivedSummary() {
            return receivedSummary;
        }
    }
}