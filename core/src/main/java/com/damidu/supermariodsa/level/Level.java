package com.damidu.supermariodsa.level;

import com.badlogic.gdx.graphics.Color;
import com.damidu.supermariodsa.model.Coin;
import com.damidu.supermariodsa.model.Enemy;
import com.damidu.supermariodsa.model.GoalFlag;
import com.damidu.supermariodsa.model.Platform;
import com.damidu.supermariodsa.model.PowerUp;

/**
 * Encapsulates the static and dynamic entities belonging to a single level stage.
 * Stored in plain arrays to demonstrate DSA array operations.
 */
public class Level {

    private final int levelNumber;
    private final String name;
    private final float width;
    private final float height;
    private final Color skyColor;

    private final Platform[] platforms;
    private final Coin[] coins;
    private final Enemy[] enemies;
    private final PowerUp[] powerUps;
    private final GoalFlag goalFlag;
    private final float spawnX;
    private final float spawnY;
    private final float checkpointX;

    public Level(int levelNumber, String name, float width, float height, Color skyColor,
                 Platform[] platforms, Coin[] coins, Enemy[] enemies, PowerUp[] powerUps,
                 GoalFlag goalFlag, float spawnX, float spawnY, float checkpointX) {
        this.levelNumber = levelNumber;
        this.name = name;
        this.width = width;
        this.height = height;
        this.skyColor = skyColor;
        this.platforms = platforms;
        this.coins = coins;
        this.enemies = enemies;
        this.powerUps = powerUps;
        this.goalFlag = goalFlag;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.checkpointX = checkpointX;
    }

    public int getLevelNumber() { return levelNumber; }
    public String getName() { return name; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Color getSkyColor() { return skyColor; }
    public Platform[] getPlatforms() { return platforms; }
    public Coin[] getCoins() { return coins; }
    public Enemy[] getEnemies() { return enemies; }
    public PowerUp[] getPowerUps() { return powerUps; }
    public GoalFlag getGoalFlag() { return goalFlag; }
    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }
    public float getCheckpointX() { return checkpointX; }
}
