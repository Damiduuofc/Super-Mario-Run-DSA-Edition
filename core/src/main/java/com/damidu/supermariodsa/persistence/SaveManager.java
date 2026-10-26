package com.damidu.supermariodsa.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Handles persistence for game session progress and player settings.
 * Enables "Continue Game" from the main menu and persistent audio preferences.
 */
public class SaveManager {

    private static final String PROGRESS_FILE = "save_progress.txt";
    private static final String SETTINGS_FILE = "settings.txt";

    public static class GameProgress {
        public int currentLevel = 1;
        public int lives = 3;
        public int score = 0;
        public int coins = 0;

        public GameProgress() {}

        public GameProgress(int currentLevel, int lives, int score, int coins) {
            this.currentLevel = currentLevel;
            this.lives = lives;
            this.score = score;
            this.coins = coins;
        }
    }

    public static void saveProgress(int level, int lives, int score, int coins) {
        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            String data = String.format("%d;%d;%d;%d", level, lives, score, coins);
            file.writeString(data, false);
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error saving progress: " + e.getMessage());
        }
    }

    public static GameProgress loadProgress() {
        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            if (!file.exists()) return null;
            String data = file.readString().trim();
            String[] parts = data.split(";");
            if (parts.length >= 4) {
                return new GameProgress(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                );
            }
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error loading progress: " + e.getMessage());
        }
        return null;
    }

    public static boolean hasSavedProgress() {
        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            return file.exists() && file.length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void clearProgress() {
        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error clearing progress: " + e.getMessage());
        }
    }

    public static void saveSettings(boolean muted, float volume) {
        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            file.writeString(String.format("%b;%.2f", muted, volume), false);
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error saving settings: " + e.getMessage());
        }
    }

    public static boolean loadMuteSetting() {
        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            if (file.exists()) {
                String[] parts = file.readString().trim().split(";");
                if (parts.length >= 1) {
                    return Boolean.parseBoolean(parts[0]);
                }
            }
        } catch (Exception e) {
            // Default to false
        }
        return false;
    }
}
