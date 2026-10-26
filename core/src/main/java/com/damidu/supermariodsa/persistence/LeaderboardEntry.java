package com.damidu.supermariodsa.persistence;

/**
 * Represents an entry in the game's high score leaderboard.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private final String playerName;
    private int score;
    private int levelReached;
    private int coinsCollected;

    public LeaderboardEntry(String playerName, int score, int levelReached, int coinsCollected) {
        this.playerName = (playerName == null || playerName.trim().isEmpty()) ? "Player" : playerName.trim();
        this.score = Math.max(0, score);
        this.levelReached = Math.max(1, levelReached);
        this.coinsCollected = Math.max(0, coinsCollected);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevelReached() {
        return levelReached;
    }

    public void setLevelReached(int levelReached) {
        this.levelReached = levelReached;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    public void setCoinsCollected(int coinsCollected) {
        this.coinsCollected = coinsCollected;
    }

    /**
     * Compare primarily by score descending (higher scores come first).
     */
    @Override
    public int compareTo(LeaderboardEntry other) {
        if (other == null) return -1;
        // Descending order: other.score - this.score
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return String.format("%s - %d pts (Level %d, %d coins)", playerName, score, levelReached, coinsCollected);
    }
}
