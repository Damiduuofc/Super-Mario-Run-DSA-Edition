package com.damidu.supermariodsa.dsa;

import com.damidu.supermariodsa.persistence.LeaderboardEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BubbleSortTest {

    @Test
    @DisplayName("Bubble Sort orders scores in descending sequence")
    void testUnsortedScores() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("Mario", 400, 1, 10),
                new LeaderboardEntry("Luigi", 850, 3, 20),
                new LeaderboardEntry("Peach", 650, 2, 15),
                new LeaderboardEntry("Toad", 950, 3, 25)
        };

        BubbleSort.SortMetrics metrics = BubbleSort.sort(entries, 4);

        assertEquals(950, entries[0].getScore());
        assertEquals("Toad", entries[0].getPlayerName());
        assertEquals(850, entries[1].getScore());
        assertEquals(650, entries[2].getScore());
        assertEquals(400, entries[3].getScore());

        assertTrue(metrics.swaps > 0);
        assertTrue(metrics.comparisons > 0);
    }

    @Test
    @DisplayName("Bubble Sort early-exit terminates in 1 pass for already sorted arrays (O(n) best-case)")
    void testAlreadySortedEarlyExit() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("A", 300, 1, 5),
                new LeaderboardEntry("B", 200, 1, 5),
                new LeaderboardEntry("C", 100, 1, 5)
        };

        BubbleSort.SortMetrics metrics = BubbleSort.sort(entries, 3);

        assertEquals(0, metrics.swaps, "Already sorted array requires 0 swaps");
        assertEquals(1, metrics.passes, "Early exit flag ensures only 1 pass is executed");
    }

    @Test
    @DisplayName("Bubble Sort handles duplicates stability and edge cases")
    void testDuplicateScores() {
        LeaderboardEntry[] entries = new LeaderboardEntry[]{
                new LeaderboardEntry("A", 500, 1, 0),
                new LeaderboardEntry("B", 500, 1, 0),
                new LeaderboardEntry("C", 200, 1, 0)
        };

        BubbleSort.sort(entries, 3);

        assertEquals(500, entries[0].getScore());
        assertEquals(500, entries[1].getScore());
        assertEquals(200, entries[2].getScore());
    }
}
