package com.damidu.supermariodsa.dsa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecursionHelperTest {

    @Test
    @DisplayName("Recursive countdown terminates at base case with GO!")
    void testCountdownBaseCaseAndTermination() {
        List<String> sequence = RecursionHelper.buildCountdownTrace(3);

        assertEquals(4, sequence.size());
        assertEquals("3", sequence.get(0));
        assertEquals("2", sequence.get(1));
        assertEquals("1", sequence.get(2));
        assertEquals("GO!", sequence.get(3));
    }

    @Test
    @DisplayName("Recursive coin bonus calculates mathematically correct recurrence")
    void testCoinBonusRecurrence() {
        // Base case: 0 coins -> 0 bonus
        assertEquals(0, RecursionHelper.calculateCoinBonus(0));

        // 1 coin: 100 + 10 = 110
        assertEquals(110, RecursionHelper.calculateCoinBonus(1));

        // 2 coins: (100 + 20) + 110 = 230
        assertEquals(230, RecursionHelper.calculateCoinBonus(2));

        // 3 coins: (100 + 30) + 230 = 360
        assertEquals(360, RecursionHelper.calculateCoinBonus(3));
    }

    @Test
    @DisplayName("Difficulty multiplier scales recursively with level")
    void testDifficultyMultiplier() {
        assertEquals(1.0f, RecursionHelper.calculateDifficultyMultiplier(1), 0.01f);
        assertEquals(1.25f, RecursionHelper.calculateDifficultyMultiplier(2), 0.01f);
        assertEquals(1.5625f, RecursionHelper.calculateDifficultyMultiplier(3), 0.01f);
    }
}
