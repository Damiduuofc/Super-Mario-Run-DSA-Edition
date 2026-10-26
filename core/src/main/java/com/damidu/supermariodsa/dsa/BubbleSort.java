package com.damidu.supermariodsa.dsa;

import com.damidu.supermariodsa.persistence.LeaderboardEntry;

/**
 * Manual implementation of the Bubble Sort algorithm for university DSA rubric.
 *
 * Requirements satisfied:
 * - Sorts leaderboard scores in DESCENDING order (highest scores first).
 * - Implemented manually without using Arrays.sort(), Collections.sort(), or streams.
 * - Early-exit flag stops unnecessary passes if the array is already sorted.
 *
 * Algorithm Complexity:
 * - Best Case: O(n) comparisons, 0 swaps (when array is already sorted).
 * - Worst / Average Case: O(n^2) comparisons and swaps.
 * - Auxiliary Space: O(1) in-place sorting.
 */
public class BubbleSort {

    public static class SortMetrics {
        public int comparisons = 0;
        public int swaps = 0;
        public int passes = 0;
    }

    /**
     * Sorts the entries array in descending order up to valid element count.
     *
     * @param entries Array of leaderboard entries.
     * @param count   Number of valid entries currently stored in the array.
     * @return SortMetrics record containing comparison and swap counts.
     */
    public static SortMetrics sort(LeaderboardEntry[] entries, int count) {
        SortMetrics metrics = new SortMetrics();
        if (entries == null || count <= 1) {
            return metrics;
        }

        int n = Math.min(count, entries.length);

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            metrics.passes++;

            for (int j = 0; j < n - i - 1; j++) {
                metrics.comparisons++;

                // Descending order: if the left entry has a LOWER score than right, swap them
                if (entries[j].getScore() < entries[j + 1].getScore()) {
                    LeaderboardEntry temp = entries[j];
                    entries[j] = entries[j + 1];
                    entries[j + 1] = temp;
                    swapped = true;
                    metrics.swaps++;
                }
            }

            // Early exit optimization: If no swaps occurred in this pass, array is fully sorted!
            if (!swapped) {
                break;
            }
        }

        return metrics;
    }
}
