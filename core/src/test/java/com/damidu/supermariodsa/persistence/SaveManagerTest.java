package com.damidu.supermariodsa.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SaveManagerTest {

    @Test
    @DisplayName("GameProgress stores and formats run progress accurately")
    void testGameProgressFormatting() {
        SaveManager.GameProgress progress = new SaveManager.GameProgress(
                "Damidu", 2, 3, 2, 1450, 18, 520f, 100f
        );

        assertEquals("Damidu", progress.playerName);
        assertEquals(2, progress.currentLevel);
        assertEquals(3, progress.lives);
        assertEquals(2, progress.health);
        assertEquals(1450, progress.score);
        assertEquals(18, progress.coins);
        assertEquals(520f, progress.checkpointX, 0.01f);
        assertEquals(100f, progress.checkpointY, 0.01f);

        String summary = progress.getFormattedSummary();
        assertTrue(summary.contains("Damidu"));
        assertTrue(summary.contains("World 1-2"));
        assertTrue(summary.contains("3 Lives"));
        assertTrue(summary.contains("1450 pts"));
    }

    @Test
    @DisplayName("Profile cycling cycles through predefined student names")
    void testProfileCycling() {
        String p1 = SaveManager.cycleNextProfile("Damidu");
        assertEquals("Amal", p1);

        String p2 = SaveManager.cycleNextProfile("Amal");
        assertEquals("Nimal", p2);

        String p3 = SaveManager.cycleNextProfile("Nimal");
        assertEquals("Kasun", p3);

        String p4 = SaveManager.cycleNextProfile("Kasun");
        assertEquals("Mario", p4);

        String p5 = SaveManager.cycleNextProfile("Mario");
        assertEquals("Damidu", p5, "Must wrap back to beginning");
    }

    @Test
    @DisplayName("GameProgress handles null or empty names safely with default")
    void testDefaultNameFallback() {
        SaveManager.GameProgress progress = new SaveManager.GameProgress(
                null, 1, 3, 3, 0, 0, 40f, 100f
        );
        assertEquals("Damidu", progress.playerName);

        SaveManager.GameProgress progressEmpty = new SaveManager.GameProgress(
                "   ", 1, 3, 3, 0, 0, 40f, 100f
        );
        assertEquals("Damidu", progressEmpty.playerName);
    }
}
