package com.damidu.supermariodsa.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Encapsulates player kinematics, health, lives, scoring, power-up states,
 * and state snapshotting for the Stack Undo mechanic.
 */
public class Player {

    public static final float BASE_WIDTH = 26f;
    public static final float BASE_HEIGHT = 42f;
    public static final float WIDTH = BASE_WIDTH;
    public static final float HEIGHT = BASE_HEIGHT;

    // Movement physics parameters (in pixels / second)
    public static final float MAX_SPEED = 240f;
    public static final float ACCELERATION = 1200f;
    public static final float FRICTION = 1000f;
    public static final float GRAVITY = -1200f;
    public static final float JUMP_VELOCITY = 520f;
    public static final float MAX_FALL_SPEED = -800f;

    private final Vector2 position;
    private final Vector2 velocity;
    private final Vector2 spawnPosition;
    private final Rectangle bounds;

    private boolean onGround;
    private boolean facingRight;
    private float stateTime;
    private float jumpBufferTimer;

    // Gameplay Attributes (v1.1)
    private int health;
    public static final int MAX_HEALTH = 3;
    private int lives;
    private int score;
    private int coins;

    private float invincibilityTimer;
    private boolean starPower;
    private float starTimer;
    private boolean superSize;

    public Player(float startX, float startY) {
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0f, 0f);
        this.spawnPosition = new Vector2(startX, startY);
        this.bounds = new Rectangle(startX, startY, BASE_WIDTH, BASE_HEIGHT);

        this.onGround = false;
        this.facingRight = true;
        this.stateTime = 0f;
        this.jumpBufferTimer = 0f;

        this.health = MAX_HEALTH;
        this.lives = 3;
        this.score = 0;
        this.coins = 0;

        this.invincibilityTimer = 0f;
        this.starPower = false;
        this.starTimer = 0f;
        this.superSize = false;
    }

    public void handleInput(float delta) {
        boolean moveLeft = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        boolean moveRight = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
        boolean jumpPressed = Gdx.input.isKeyJustPressed(Keys.W)
                || Gdx.input.isKeyJustPressed(Keys.UP)
                || Gdx.input.isKeyJustPressed(Keys.SPACE);

        if (jumpPressed) {
            jumpBufferTimer = 0.12f;
        } else if (jumpBufferTimer > 0f) {
            jumpBufferTimer -= delta;
        }

        float currentSpeed = starPower ? MAX_SPEED * 1.35f : MAX_SPEED;

        // Horizontal acceleration
        if (moveLeft && !moveRight) {
            velocity.x -= ACCELERATION * delta;
            if (velocity.x < -currentSpeed) {
                velocity.x = -currentSpeed;
            }
            facingRight = false;
        } else if (moveRight && !moveLeft) {
            velocity.x += ACCELERATION * delta;
            if (velocity.x > currentSpeed) {
                velocity.x = currentSpeed;
            }
            facingRight = true;
        } else {
            // Friction deceleration
            if (velocity.x > 0f) {
                velocity.x = Math.max(0f, velocity.x - FRICTION * delta);
            } else if (velocity.x < 0f) {
                velocity.x = Math.min(0f, velocity.x + FRICTION * delta);
            }
        }

        // Jump impulse: only allowed when on solid ground
        if (jumpBufferTimer > 0f && onGround) {
            float jumpImpulse = superSize ? JUMP_VELOCITY * 1.08f : JUMP_VELOCITY;
            velocity.y = jumpImpulse;
            onGround = false;
            jumpBufferTimer = 0f;
        }
    }

    public void applyGravity(float delta) {
        if (!onGround) {
            velocity.y += GRAVITY * delta;
            if (velocity.y < MAX_FALL_SPEED) {
                velocity.y = MAX_FALL_SPEED;
            }
        }
    }

    public void update(float delta) {
        if (Math.abs(velocity.x) > 10f && onGround) {
            stateTime += delta * (Math.abs(velocity.x) / MAX_SPEED) * 8f;
        } else if (!onGround) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        // Star power countdown
        if (starPower) {
            starTimer -= delta;
            if (starTimer <= 0f) {
                starPower = false;
                starTimer = 0f;
            }
        }

        // Damage invincibility cooldown
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= delta;
        }

        float h = superSize ? BASE_HEIGHT * 1.25f : BASE_HEIGHT;
        bounds.set(position.x, position.y, BASE_WIDTH, h);
    }

    /**
     * Applies damage to the player unless protected by star power or invulnerability frames.
     * Returns true if damage was taken.
     */
    public boolean takeDamage(int amount) {
        if (starPower || invincibilityTimer > 0f) {
            return false;
        }

        health -= amount;
        invincibilityTimer = 1.8f; // 1.8 seconds of invulnerability frames

        if (health <= 0) {
            lives--;
            if (lives > 0) {
                health = MAX_HEALTH;
                respawn();
            }
        }
        return true;
    }

    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount);
    }

    public void addScore(int points) {
        score = Math.max(0, score + points);
    }

    public void addCoin() {
        coins++;
        addScore(200);
        if (coins >= 100) {
            coins -= 100;
            lives++;
        }
    }

    public void activateStar(float duration) {
        starPower = true;
        starTimer = duration;
        addScore(1000);
    }

    public void activateSuperMushroom() {
        superSize = true;
        heal(1);
        addScore(1000);
    }

    public void bounceOffEnemy() {
        velocity.y = JUMP_VELOCITY * 0.75f;
        onGround = false;
    }

    public void respawn() {
        position.set(spawnPosition);
        velocity.set(0f, 0f);
        onGround = false;
        facingRight = true;
        invincibilityTimer = 1.5f;
        bounds.setPosition(position.x, position.y);
    }

    public void setSpawnPoint(float x, float y) {
        spawnPosition.set(x, y);
    }

    // --- DSA STACK SNAPSHOT OPERATIONS ---

    public PlayerStateSnapshot createSnapshot() {
        return new PlayerStateSnapshot(
                position.x, position.y,
                velocity.x, velocity.y,
                onGround, facingRight,
                health, score, coins,
                invincibilityTimer
        );
    }

    public void restoreSnapshot(PlayerStateSnapshot snapshot) {
        if (snapshot == null) return;
        position.set(snapshot.x, snapshot.y);
        velocity.set(snapshot.vx, snapshot.vy);
        onGround = snapshot.onGround;
        facingRight = snapshot.facingRight;
        health = snapshot.health;
        score = snapshot.score;
        coins = snapshot.coins;
        invincibilityTimer = snapshot.invincibilityTimer;
        bounds.setPosition(position.x, position.y);
    }

    // --- RENDERING ---

    public void render(ShapeRenderer renderer) {
        // Flicker during invulnerability
        if (invincibilityTimer > 0f && !starPower) {
            if (((int) (invincibilityTimer * 14f)) % 2 == 0) {
                return; // Skip drawing this frame to create retro blink
            }
        }

        float px = position.x;
        float py = position.y;
        float currentHeight = superSize ? BASE_HEIGHT * 1.25f : BASE_HEIGHT;
        float scale = superSize ? 1.25f : 1.0f;

        renderer.set(ShapeRenderer.ShapeType.Filled);

        // Star power rainbow glow aura
        if (starPower) {
            float hue = (System.currentTimeMillis() % 1000) / 1000f;
            renderer.setColor(Color.WHITE.cpy().fromHsv(hue * 360f, 0.8f, 1.0f));
            renderer.rect(px - 4, py - 2, BASE_WIDTH + 8, currentHeight + 4);
        }

        // 1. Boots (Dark Brown)
        renderer.setColor(0.35f, 0.18f, 0.05f, 1f);
        float legSwing = (onGround && Math.abs(velocity.x) > 10f) ? (float) Math.sin(stateTime * 2.5f) * 4f * scale : 0f;

        if (!onGround) {
            renderer.rect(px + 2 * scale, py + 3 * scale, 9 * scale, 6 * scale);
            renderer.rect(px + BASE_WIDTH - 11 * scale, py + 5 * scale, 9 * scale, 6 * scale);
        } else {
            renderer.rect(px + (1 * scale) + legSwing, py, 9 * scale, 7 * scale);
            renderer.rect(px + BASE_WIDTH - (10 * scale) - legSwing, py, 9 * scale, 7 * scale);
        }

        // 2. Overalls (Classic Denim Blue)
        renderer.setColor(0.12f, 0.35f, 0.88f, 1f);
        renderer.rect(px + 3 * scale, py + 7 * scale, BASE_WIDTH - 6 * scale, 14 * scale);

        // Straps & buttons
        renderer.rect(px + 4 * scale, py + 21 * scale, 5 * scale, 6 * scale);
        renderer.rect(px + BASE_WIDTH - 9 * scale, py + 21 * scale, 5 * scale, 6 * scale);
        renderer.setColor(0.98f, 0.85f, 0.15f, 1f);
        renderer.rect(px + 5 * scale, py + 18 * scale, 3 * scale, 3 * scale);
        renderer.rect(px + BASE_WIDTH - 8 * scale, py + 18 * scale, 3 * scale, 3 * scale);

        // 3. Shirt (Bright Red)
        renderer.setColor(0.90f, 0.15f, 0.12f, 1f);
        renderer.rect(px + 9 * scale, py + 19 * scale, BASE_WIDTH - 18 * scale, 8 * scale);
        renderer.rect(px + 1 * scale, py + 19 * scale, 4 * scale, 8 * scale);
        renderer.rect(px + BASE_WIDTH - 5 * scale, py + 19 * scale, 4 * scale, 8 * scale);

        // White gloves
        renderer.setColor(Color.WHITE);
        float gloveY = (!onGround) ? py + 24 * scale : py + 15 * scale;
        renderer.rect(px, gloveY, 4 * scale, 5 * scale);
        renderer.rect(px + BASE_WIDTH - 4 * scale, gloveY, 4 * scale, 5 * scale);

        // 4. Face (Warm Peach)
        renderer.setColor(0.99f, 0.82f, 0.65f, 1f);
        renderer.rect(px + 4 * scale, py + 27 * scale, BASE_WIDTH - 8 * scale, 9 * scale);
        float noseX = facingRight ? px + BASE_WIDTH - 4 * scale : px;
        renderer.rect(noseX, py + 29 * scale, 4 * scale, 4 * scale);

        // Eye & Moustache
        renderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        float eyeX = facingRight ? px + BASE_WIDTH - 10 * scale : px + 7 * scale;
        renderer.rect(eyeX, py + 32 * scale, 3 * scale, 3 * scale);

        renderer.setColor(0.35f, 0.18f, 0.05f, 1f);
        float moustacheX = facingRight ? px + BASE_WIDTH - 12 * scale : px + 2 * scale;
        renderer.rect(moustacheX, py + 28 * scale, 10 * scale, 4 * scale);

        // 5. Cap (Classic Red)
        renderer.setColor(0.90f, 0.15f, 0.12f, 1f);
        renderer.rect(px + 3 * scale, py + 36 * scale, BASE_WIDTH - 6 * scale, 6 * scale);
        float brimX = facingRight ? px + 6 * scale : px - 2 * scale;
        renderer.rect(brimX, py + 35 * scale, BASE_WIDTH - 4 * scale, 3 * scale);
    }

    // Getters and Setters
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getBounds() { return bounds; }
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public boolean isFacingRight() { return facingRight; }
    public float getX() { return position.x; }
    public void setX(float x) { this.position.x = x; this.bounds.x = x; }
    public float getY() { return position.y; }
    public void setY(float y) { this.position.y = y; this.bounds.y = y; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public boolean hasStarPower() { return starPower; }
    public boolean isAlive() { return lives > 0; }
}
