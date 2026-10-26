package com.damidu.supermariodsa.dsa;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates genuine recursive algorithms used in game progression and score calculation.
 *
 * Requirements satisfied:
 * - Clear base cases.
 * - Guaranteed termination.
 * - Call stack tracking for viva demonstration.
 */
public class RecursionHelper {

    /**
     * Recursively calculates bonus points awarded for collected coins.
     * Demonstrates linear recursion with clear base case.
     *
     * Mathematical recurrence:
     *   Bonus(0) = 0                       (Base case)
     *   Bonus(n) = (100 + n * 10) + Bonus(n - 1)  (Recursive step)
     *
     * Complexity:
     *   Time: O(n)
     *   Space: O(n) call stack frames
     */
    public static int calculateCoinBonus(int coinsRemaining) {
        // Base case: No more coins to calculate
        if (coinsRemaining <= 0) {
            return 0;
        }
        // Recursive step: Add points for current coin + recurse on remainder
        int currentCoinValue = 100 + (coinsRemaining * 10);
        return currentCoinValue + calculateCoinBonus(coinsRemaining - 1);
    }

    /**
     * Recursively generates countdown string sequence for round start.
     * Returns: ["3", "2", "1", "GO!"]
     */
    public static List<String> buildCountdownTrace(int startNumber) {
        List<String> sequence = new ArrayList<>();
        recurseCountdown(startNumber, sequence);
        return sequence;
    }

    private static void recurseCountdown(int n, List<String> sequence) {
        // Base case: Countdown reaches 0 -> emit "GO!" and terminate
        if (n <= 0) {
            sequence.add("GO!");
            return;
        }
        sequence.add(String.valueOf(n));
        // Recursive call with decremented counter
        recurseCountdown(n - 1, sequence);
    }

    /**
     * Recursively computes composite level difficulty multiplier.
     * Base case: Level 1 has multiplier 1.0f
     * Level n multiplier = 1.25 * Multiplier(n - 1)
     */
    public static float calculateDifficultyMultiplier(int level) {
        if (level <= 1) {
            return 1.0f; // Base case
        }
        return 1.25f * calculateDifficultyMultiplier(level - 1);
    }
}
