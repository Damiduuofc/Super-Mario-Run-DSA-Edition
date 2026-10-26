package com.damidu.supermariodsa.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Handles persistence for game session progress, player profiles, and settings.
 * Enables explicit and automatic Save and Resume features from any point in the game.
 */
public class SaveManager {

    private static final String PROGRESS_FILE = "save_progress.txt";
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String PROFILE_FILE = "active_profile.txt";

    public static final String[] AVAILABLE_PROFILES = {"Damidu", "Amal", "Nimal", "Kasun", "Mario"};
    private static String cachedActivePlayerName = "Damidu";

    public static class GameProgress {
        public String playerName = "Damidu";
        public int currentLevel = 1;
        public int lives = 3;
        public int health = 3;
        public int score = 0;
        public int coins = 0;
        public float checkpointX = 40f;
        public float checkpointY = 100f;
        public long timestamp = System.currentTimeMillis();

        public GameProgress() {}

        public GameProgress(String playerName, int currentLevel, int lives, int health, int score, int coins, float checkpointX, float checkpointY) {
            this.playerName = (playerName == null || playerName.trim().isEmpty()) ? "Damidu" : playerName.trim();
            this.currentLevel = Math.max(1, currentLevel);
            this.lives = Math.max(1, lives);
            this.health = Math.max(1, health);
            this.score = Math.max(0, score);
            this.coins = Math.max(0, coins);
            this.checkpointX = checkpointX;
            this.checkpointY = checkpointY;
            this.timestamp = System.currentTimeMillis();
        }

        public String getFormattedSummary() {
            return String.format("%s (World 1-%d | %d Lives | %d pts)", playerName, currentLevel, lives, score);
        }
    }

    /**
     * Complete progress saving with player name, health, and checkpoint coordinates.
     */
    public static boolean saveProgress(String playerName, int level, int lives, int health, int score, int coins, float checkpointX, float checkpointY) {
        String cleanName = (playerName == null || playerName.trim().isEmpty()) ? getActivePlayerName() : playerName.trim();
        setActivePlayerName(cleanName);

        if (Gdx.files == null) return true;

        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            String data = String.format("%s;%d;%d;%d;%d;%d;%.2f;%.2f;%d",
                    cleanName, level, lives, health, score, coins, checkpointX, checkpointY, System.currentTimeMillis());
            file.writeString(data, false);
            return true;
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error saving progress: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveProgress(int level, int lives, int score, int coins) {
        return saveProgress(getActivePlayerName(), level, lives, 3, score, coins, 40f, 100f);
    }

    public static GameProgress loadProgress() {
        if (Gdx.files == null) return null;

        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            if (!file.exists()) return null;
            String data = file.readString().trim();
            if (data.isEmpty()) return null;

            String[] parts = data.split(";");
            if (parts.length >= 8) {
                GameProgress progress = new GameProgress(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]),
                        Float.parseFloat(parts[6]),
                        Float.parseFloat(parts[7])
                );
                if (parts.length >= 9) {
                    progress.timestamp = Long.parseLong(parts[8]);
                }
                setActivePlayerName(progress.playerName);
                return progress;
            } else if (parts.length >= 4) {
                return new GameProgress(
                        getActivePlayerName(),
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        3,
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        40f, 100f
                );
            }
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error loading progress: " + e.getMessage());
        }
        return null;
    }

    public static boolean hasSavedProgress() {
        if (Gdx.files == null) return false;
        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            return file.exists() && file.length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static void clearProgress() {
        if (Gdx.files == null) return;
        try {
            FileHandle file = Gdx.files.local(PROGRESS_FILE);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error clearing progress: " + e.getMessage());
        }
    }

    // --- PLAYER NAME / PROFILE SYSTEM ---

    public static String getActivePlayerName() {
        if (Gdx.files != null) {
            try {
                FileHandle file = Gdx.files.local(PROFILE_FILE);
                if (file.exists()) {
                    String name = file.readString().trim();
                    if (!name.isEmpty()) {
                        cachedActivePlayerName = name;
                        return name;
                    }
                }
            } catch (Exception e) {
                // Fallback to cache
            }
        }
        return cachedActivePlayerName;
    }

    public static void setActivePlayerName(String name) {
        String cleanName = (name == null || name.trim().isEmpty()) ? "Damidu" : name.trim();
        cachedActivePlayerName = cleanName;
        if (Gdx.files != null) {
            try {
                FileHandle file = Gdx.files.local(PROFILE_FILE);
                file.writeString(cleanName, false);
            } catch (Exception e) {
                Gdx.app.log("SaveManager", "Error saving active profile name: " + e.getMessage());
            }
        }
    }

    public static String cycleNextProfile(String current) {
        int currentIndex = 0;
        for (int i = 0; i < AVAILABLE_PROFILES.length; i++) {
            if (AVAILABLE_PROFILES[i].equalsIgnoreCase(current)) {
                currentIndex = i;
                break;
            }
        }
        int nextIndex = (currentIndex + 1) % AVAILABLE_PROFILES.length;
        String nextName = AVAILABLE_PROFILES[nextIndex];
        setActivePlayerName(nextName);
        return nextName;
    }

    // --- AUDIO SETTINGS ---

    public static void saveSettings(boolean muted, float volume) {
        if (Gdx.files == null) return;
        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            file.writeString(String.format("%b;%.2f", muted, volume), false);
        } catch (Exception e) {
            Gdx.app.log("SaveManager", "Error saving settings: " + e.getMessage());
        }
    }

    public static boolean loadMuteSetting() {
        if (Gdx.files == null) return false;
        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            if (file.exists()) {
                String[] parts = file.readString().trim().split(";");
                if (parts.length >= 1) {
                    return Boolean.parseBoolean(parts[0]);
                }
            }
        } catch (Exception e) {
            // Default to unmuted
        }
        return false;
    }
}
