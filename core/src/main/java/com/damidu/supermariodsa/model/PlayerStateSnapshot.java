package com.damidu.supermariodsa.model;

/**
 * State snapshot of the player and game parameters stored inside CustomStack
 * for the movement undo feature (DSA Requirement C).
 */
public class PlayerStateSnapshot {

    public final float x;
    public final float y;
    public final float vx;
    public final float vy;
    public final boolean onGround;
    public final boolean facingRight;
    public final int health;
    public final int score;
    public final int coins;
    public final float invincibilityTimer;
    public final long timestamp;

    public PlayerStateSnapshot(float x, float y, float vx, float vy,
                               boolean onGround, boolean facingRight,
                               int health, int score, int coins,
                               float invincibilityTimer) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.onGround = onGround;
        this.facingRight = facingRight;
        this.health = health;
        this.score = score;
        this.coins = coins;
        this.invincibilityTimer = invincibilityTimer;
        this.timestamp = System.currentTimeMillis();
    }
}
