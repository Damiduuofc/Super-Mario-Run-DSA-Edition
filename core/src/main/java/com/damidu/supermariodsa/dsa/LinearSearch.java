package com.damidu.supermariodsa.dsa;

import com.damidu.supermariodsa.persistence.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual Linear Search implementation with execution step trace.
 * Used for searching unsorted/arbitrary attributes like player names.
 *
 * Algorithm Complexity:
 * - Best Case: O(1) when target is at index 0.
 * - Worst / Average Case: O(n) checking all n entries.
 * - Space Complexity: O(1) auxiliary (excluding trace string history).
 */
public class LinearSearch {

    public static class SearchResult {
        public final boolean found;
        public final LeaderboardEntry entry;
        public final int index;
        public final int comparisons;
        public final String[] trace;

        public SearchResult(boolean found, LeaderboardEntry entry, int index, int comparisons, String[] trace) {
            this.found = found;
            this.entry = entry;
            this.index = index;
            this.comparisons = comparisons;
            this.trace = trace;
        }
    }

    /**
     * Linearly searches for a player by name in the leaderboard array.
     *
     * @param entries Array of leaderboard entries.
     * @param count   Number of valid entries.
     * @param query   Player name to look for.
     * @return SearchResult containing match data, comparisons count, and step trace.
     */
    public static SearchResult searchByName(LeaderboardEntry[] entries, int count, String query) {
        List<String> trace = new ArrayList<>();
        if (entries == null || count <= 0 || query == null || query.trim().isEmpty()) {
            trace.add("Search aborted: invalid array or empty search query.");
            return new SearchResult(false, null, -1, 0, trace.toArray(new String[0]));
        }

        String target = query.trim();
        int comparisons = 0;

        for (int i = 0; i < count; i++) {
            comparisons++;
            LeaderboardEntry current = entries[i];
            if (current == null) continue;

            trace.add(String.format("Step %d: Inspecting index [%d] -> \"%s\" (%d pts)",
                    comparisons, i, current.getPlayerName(), current.getScore()));

            if (current.getPlayerName().equalsIgnoreCase(target)) {
                trace.add(String.format("==> SUCCESS: Match found at index [%d] in %d comparisons!", i, comparisons));
                return new SearchResult(true, current, i, comparisons, trace.toArray(new String[0]));
            }
        }

        trace.add(String.format("==> NOT FOUND: Checked all %d entries without finding \"%s\".", comparisons, target));
        return new SearchResult(false, null, -1, comparisons, trace.toArray(new String[0]));
    }
}
