package com.damidu.supermariodsa.persistence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.damidu.supermariodsa.dsa.BinarySearch;
import com.damidu.supermariodsa.dsa.BubbleSort;
import com.damidu.supermariodsa.dsa.LinearSearch;

/**
 * Manages the high score leaderboard using a plain resizable array.
 * Integrates manual Bubble Sort, Linear Search, and Binary Search algorithms.
 */
public class LeaderboardManager {

    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    private static final int INITIAL_CAPACITY = 10;

    // Fixed array that resizes dynamically when full (DSA Requirement A)
    private LeaderboardEntry[] entries;
    private int count;

    public LeaderboardManager() {
        this.entries = new LeaderboardEntry[INITIAL_CAPACITY];
        this.count = 0;
        load();
        if (count == 0) {
            initDefaultEntries();
        }
    }

    private void initDefaultEntries() {
        recordScore("Mario", 1250, 3, 35);
        recordScore("Luigi", 980, 2, 28);
        recordScore("Damidu", 850, 3, 22);
        recordScore("Peach", 620, 2, 18);
        recordScore("Toad", 410, 1, 12);
        save();
    }

    /**
     * Records a game score. If player exists, updates only if new score is higher.
     * Re-sorts using Bubble Sort immediately after modification.
     */
    public synchronized void recordScore(String playerName, int score, int levelReached, int coins) {
        String cleanName = (playerName == null || playerName.trim().isEmpty()) ? "Player" : playerName.trim();

        // Check if player already exists in leaderboard
        boolean updated = false;
        for (int i = 0; i < count; i++) {
            if (entries[i].getPlayerName().equalsIgnoreCase(cleanName)) {
                if (score > entries[i].getScore()) {
                    entries[i].setScore(score);
                    entries[i].setLevelReached(Math.max(levelReached, entries[i].getLevelReached()));
                    entries[i].setCoinsCollected(Math.max(coins, entries[i].getCoinsCollected()));
                }
                updated = true;
                break;
            }
        }

        if (!updated) {
            if (count >= entries.length) {
                resizeArray();
            }
            entries[count++] = new LeaderboardEntry(cleanName, score, levelReached, coins);
        }

        // Keep array ranked in descending order using manual Bubble Sort
        BubbleSort.sort(entries, count);
        save();
    }

    private void resizeArray() {
        LeaderboardEntry[] newArray = new LeaderboardEntry[entries.length * 2];
        System.arraycopy(entries, 0, newArray, 0, count);
        this.entries = newArray;
    }

    public BubbleSort.SortMetrics manualBubbleSort() {
        return BubbleSort.sort(entries, count);
    }

    public LinearSearch.SearchResult searchByName(String query) {
        return LinearSearch.searchByName(entries, count, query);
    }

    public BinarySearch.BinarySearchResult searchByScore(int targetScore) {
        return BinarySearch.searchByScore(entries, count, targetScore);
    }

    public LeaderboardEntry[] getEntries() {
        LeaderboardEntry[] result = new LeaderboardEntry[count];
        System.arraycopy(entries, 0, result, 0, count);
        return result;
    }

    public int getCount() {
        return count;
    }

    public void save() {
        try {
            FileHandle file = Gdx.files.local(LEADERBOARD_FILE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                LeaderboardEntry e = entries[i];
                sb.append(e.getPlayerName()).append(";")
                        .append(e.getScore()).append(";")
                        .append(e.getLevelReached()).append(";")
                        .append(e.getCoinsCollected()).append("\n");
            }
            file.writeString(sb.toString(), false);
        } catch (Exception e) {
            Gdx.app.log("LeaderboardManager", "Could not save leaderboard: " + e.getMessage());
        }
    }

    public void load() {
        try {
            FileHandle file = Gdx.files.local(LEADERBOARD_FILE);
            if (!file.exists()) return;

            String content = file.readString();
            String[] lines = content.split("\\r?\\n");
            count = 0;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int level = Integer.parseInt(parts[2]);
                    int coins = Integer.parseInt(parts[3]);
                    if (count >= entries.length) {
                        resizeArray();
                    }
                    entries[count++] = new LeaderboardEntry(name, score, level, coins);
                }
            }
            BubbleSort.sort(entries, count);
        } catch (Exception e) {
            Gdx.app.log("LeaderboardManager", "Could not load leaderboard: " + e.getMessage());
        }
    }
}
