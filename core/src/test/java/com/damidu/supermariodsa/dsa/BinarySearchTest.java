package com.damidu.supermariodsa.dsa;

import com.damidu.supermariodsa.persistence.LeaderboardEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BinarySearchTest {

    @Test
    @DisplayName("Recursive Binary Search finds target score in descending array")
    void testScoreFound() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("Top", 1000, 3, 30),
                new LeaderboardEntry("Second", 850, 3, 25),
                new LeaderboardEntry("Third", 650, 2, 15),
                new LeaderboardEntry("Fourth", 400, 1, 10)
        };

        BinarySearch.BinarySearchResult result = BinarySearch.searchByScore(entries, 4, 850);

        assertTrue(result.found);
        assertEquals(1, result.index);
        assertEquals(850, result.entry.getScore());
        assertTrue(result.depth >= 1);
    }

    @Test
    @DisplayName("Recursive Binary Search finds extreme endpoints (first and last indices)")
    void testEndpoints() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("Highest", 1500, 3, 40),
                new LeaderboardEntry("Mid", 900, 2, 20),
                new LeaderboardEntry("Lowest", 300, 1, 5)
        };

        // First index
        BinarySearch.BinarySearchResult rFirst = BinarySearch.searchByScore(entries, 3, 1500);
        assertTrue(rFirst.found);
        assertEquals(0, rFirst.index);

        // Last index
        BinarySearch.BinarySearchResult rLast = BinarySearch.searchByScore(entries, 3, 300);
        assertTrue(rLast.found);
        assertEquals(2, rLast.index);
    }

    @Test
    @DisplayName("Recursive Binary Search correctly reports missing score")
    void testScoreNotFound() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("A", 1000, 3, 10),
                new LeaderboardEntry("B", 800, 2, 10)
        };

        BinarySearch.BinarySearchResult result = BinarySearch.searchByScore(entries, 2, 999);
        assertFalse(result.found);
        assertEquals(-1, result.index);
    }
}
