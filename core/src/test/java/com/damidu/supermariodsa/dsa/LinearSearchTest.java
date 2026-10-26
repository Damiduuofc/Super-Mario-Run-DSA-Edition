package com.damidu.supermariodsa.dsa;

import com.damidu.supermariodsa.persistence.LeaderboardEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinearSearchTest {

    @Test
    @DisplayName("Linear Search finds existing player by name case-insensitively")
    void testExistingPlayerFound() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("Mario", 900, 3, 20),
                new LeaderboardEntry("Luigi", 800, 2, 15),
                new LeaderboardEntry("Damidu", 700, 2, 10)
        };

        LinearSearch.SearchResult result = LinearSearch.searchByName(entries, 3, "damidu");

        assertTrue(result.found);
        assertEquals(2, result.index);
        assertEquals("Damidu", result.entry.getPlayerName());
        assertEquals(3, result.comparisons);
        assertTrue(result.trace.length > 0);
    }

    @Test
    @DisplayName("Linear Search correctly reports missing player after scanning full array")
    void testPlayerNotFound() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("Mario", 900, 3, 20),
                new LeaderboardEntry("Luigi", 800, 2, 15)
        };

        LinearSearch.SearchResult result = LinearSearch.searchByName(entries, 2, "Bowser");

        assertFalse(result.found);
        assertEquals(-1, result.index);
        assertEquals(2, result.comparisons);
    }
}
