package com.damidu.supermariodsa.dsa;

import com.damidu.supermariodsa.persistence.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual Recursive Binary Search implementation on sorted leaderboard entries.
 *
 * Precondition: Entries array must be sorted in DESCENDING order by score.
 * Demonstrates: Divide-and-conquer recursion, O(log n) time complexity.
 */
public class BinarySearch {

    public static class BinarySearchResult {
        public final boolean found;
        public final LeaderboardEntry entry;
        public final int index;
        public final int depth;
        public final String[] trace;

        public BinarySearchResult(boolean found, LeaderboardEntry entry, int index, int depth, String[] trace) {
            this.found = found;
            this.entry = entry;
            this.index = index;
            this.depth = depth;
            this.trace = trace;
        }
    }

    /**
     * Public entry point for recursive binary search.
     */
    public static BinarySearchResult searchByScore(LeaderboardEntry[] entries, int count, int targetScore) {
        List<String> trace = new ArrayList<>();
        if (entries == null || count <= 0) {
            trace.add("Search aborted: leaderboard is empty.");
            return new BinarySearchResult(false, null, -1, 0, trace.toArray(new String[0]));
        }

        trace.add(String.format("Starting Recursive Binary Search for score [%d] in range [0..%d] (Descending order)",
                targetScore, count - 1));

        int resultIndex = recursiveSearch(entries, 0, count - 1, targetScore, 1, trace);

        if (resultIndex >= 0) {
            trace.add(String.format("==> SUCCESS: Score %d found at index [%d]!", targetScore, resultIndex));
            return new BinarySearchResult(true, entries[resultIndex], resultIndex, trace.size(), trace.toArray(new String[0]));
        } else {
            trace.add(String.format("==> NOT FOUND: Score %d does not exist in leaderboard.", targetScore));
            return new BinarySearchResult(false, null, -1, trace.size(), trace.toArray(new String[0]));
        }
    }

    /**
     * Recursive search algorithm operating on descending array.
     */
    private static int recursiveSearch(LeaderboardEntry[] entries, int low, int high, int targetScore, int depth, List<String> trace) {
        // Base Case 1: Search range exhausted (item not found)
        if (low > high) {
            trace.add(String.format("[Depth %d] low (%d) > high (%d) -> Range exhausted. Base case reached.", depth, low, high));
            return -1;
        }

        int mid = low + (high - low) / 2; // Prevents integer overflow
        LeaderboardEntry midEntry = entries[mid];
        int midScore = (midEntry != null) ? midEntry.getScore() : -1;

        trace.add(String.format("[Depth %d] Inspecting mid index [%d] -> Score: %d (Player: %s, Range: [%d..%d])",
                depth, mid, midScore, midEntry != null ? midEntry.getPlayerName() : "null", low, high));

        // Base Case 2: Target found!
        if (midScore == targetScore) {
            trace.add(String.format("[Depth %d] Target score %d matches mid index [%d]! Returning index.", depth, targetScore, mid));
            return mid;
        }

        // Because array is sorted in DESCENDING order:
        // If targetScore is GREATER than midScore, target must lie in the LEFT half [low .. mid - 1]
        if (targetScore > midScore) {
            trace.add(String.format("[Depth %d] Target %d > Mid %d -> Recursing LEFT half [%d..%d]",
                    depth, targetScore, midScore, low, mid - 1));
            return recursiveSearch(entries, low, mid - 1, targetScore, depth + 1, trace);
        } else {
            // Otherwise target is in the RIGHT half [mid + 1 .. high]
            trace.add(String.format("[Depth %d] Target %d < Mid %d -> Recursing RIGHT half [%d..%d]",
                    depth, targetScore, midScore, mid + 1, high));
            return recursiveSearch(entries, mid + 1, high, targetScore, depth + 1, trace);
        }
    }
}
