package com.damidu.supermariodsa.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Platformer patrol enemies and Level 3 final boss.
 * Can be defeated by jumping/stomping from above.
 */
public class Enemy {

    public enum Type {
        GOOMBA,
        KOOPA,
        BOSS_BOWSER
    }

    public enum State {
        WALKING,
        STOMPED,
        DEAD
    }

    private final Type type;
    private final Vector2 position;
    private final Vector2 velocity;
    private final Rectangle bounds;
    private final float minPatrolX;
    private final float maxPatrolX;
    private final float width;
    private final float height;

    private State state;
    private float stateTimer;
    private int bossHealth;

    public Enemy(float x, float y, Type type, float patrolDistance) {
        this.type = type;
        this.position = new Vector2(x, y);
        this.state = State.WALKING;
        this.stateTimer = 0f;

        if (type == Type.BOSS_BOWSER) {
            this.width = 64f;
            this.height = 70f;
            this.velocity = new Vector2(-45f, 0f);
            this.bossHealth = 5; // 5 hits to defeat final boss
        } else if (type == Type.KOOPA) {
            this.width = 28f;
            this.height = 38f;
            this.velocity = new Vector2(-60f, 0f);
            this.bossHealth = 1;
        } else {
            this.width = 26f;
            this.height = 26f;
            this.velocity = new Vector2(-50f, 0f);
            this.bossHealth = 1;
        }

        this.bounds = new Rectangle(x, y, width, height);
        this.minPatrolX = x - patrolDistance;
        this.maxPatrolX = x + patrolDistance;
    }

    public void update(float delta, Platform[] platforms) {
        stateTimer += delta;

        if (state == State.STOMPED) {
            if (stateTimer >= 0.4f) {
                state = State.DEAD;
            }
            return;
        }

        if (state == State.DEAD) return;

        // Boss periodic jump behavior
        if (type == Type.BOSS_BOWSER) {
            if (velocity.y == 0 && Math.random() < 0.015) {
                velocity.y = 350f; // Boss jump!
            }
            velocity.y -= 900f * delta; // Boss gravity
            position.y += velocity.y * delta;
            if (position.y <= 48f) { // Ground floor level
                position.y = 48f;
                velocity.y = 0;
            }
        }

        // Horizontal movement & patrol boundary reversal
        position.x += velocity.x * delta;

        if (position.x <= minPatrolX) {
            position.x = minPatrolX;
            velocity.x = Math.abs(velocity.x);
        } else if (position.x >= maxPatrolX) {
            position.x = maxPatrolX;
            velocity.x = -Math.abs(velocity.x);
        }

        bounds.setPosition(position.x, position.y);
    }

    /**
     * Called when the player stomps on the enemy's head from above.
     * Returns true if the enemy was completely defeated.
     */
    public boolean takeStompDamage() {
        if (type == Type.BOSS_BOWSER) {
            bossHealth--;
            if (bossHealth <= 0) {
                state = State.STOMPED;
                stateTimer = 0f;
                return true;
            }
            // Boss recoil impulse
            velocity.x = -velocity.x;
            return false;
        } else {
            state = State.STOMPED;
            stateTimer = 0f;
            return true;
        }
    }

    public void render(ShapeRenderer renderer) {
        if (state == State.DEAD) return;

        renderer.set(ShapeRenderer.ShapeType.Filled);
        float px = position.x;
        float py = position.y;

        if (type == Type.BOSS_BOWSER) {
            renderBowser(renderer, px, py);
        } else if (type == Type.KOOPA) {
            renderKoopa(renderer, px, py);
        } else {
            renderGoomba(renderer, px, py);
        }
    }

    private void renderGoomba(ShapeRenderer renderer, float px, float py) {
        if (state == State.STOMPED) {
            // Squashed flat brown mushroom
            renderer.setColor(0.55f, 0.28f, 0.08f, 1f);
            renderer.rect(px, py, width, 8);
            return;
        }

        // Feet (Black)
        renderer.setColor(Color.BLACK);
        float step = ((int) (stateTimer * 6f) % 2 == 0) ? 2f : -2f;
        renderer.rect(px + 2 + step, py, 9, 6);
        renderer.rect(px + width - 11 - step, py, 9, 6);

        // Body stem (Light peach)
        renderer.setColor(0.96f, 0.82f, 0.65f, 1f);
        renderer.rect(px + 5, py + 4, width - 10, 8);

        // Brown mushroom head
        renderer.setColor(0.58f, 0.28f, 0.08f, 1f);
        renderer.rect(px + 2, py + 10, width - 4, 12);
        renderer.rect(px + 4, py + 22, width - 8, 4);

        // Angry eyes
        renderer.setColor(Color.WHITE);
        renderer.rect(px + 5, py + 12, 5, 7);
        renderer.rect(px + width - 10, py + 12, 5, 7);
        renderer.setColor(Color.BLACK);
        renderer.rect(px + 7, py + 14, 3, 4);
        renderer.rect(px + width - 8, py + 14, 3, 4);
        // Eyebrows
        renderer.rect(px + 4, py + 19, 7, 2);
        renderer.rect(px + width - 11, py + 19, 7, 2);
    }

    private void renderKoopa(ShapeRenderer renderer, float px, float py) {
        // Feet
        renderer.setColor(0.95f, 0.75f, 0.15f, 1f);
        renderer.rect(px + 3, py, 8, 6);
        renderer.rect(px + width - 11, py, 8, 6);

        // Green shell
        renderer.setColor(0.15f, 0.75f, 0.22f, 1f);
        renderer.rect(px + 3, py + 6, width - 6, 22);
        renderer.setColor(Color.WHITE);
        renderer.rect(px + 5, py + 8, width - 10, 4);

        // Head
        renderer.setColor(0.95f, 0.80f, 0.15f, 1f);
        float headX = velocity.x < 0 ? px : px + width - 12;
        renderer.rect(headX, py + 24, 12, 12);
        // Beak & eyes
        renderer.setColor(Color.BLACK);
        renderer.rect(headX + (velocity.x < 0 ? 2 : 7), py + 28, 3, 4);
    }

    private void renderBowser(ShapeRenderer renderer, float px, float py) {
        // Boss health bar above head
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(px, py + height + 8, width, 7);
        renderer.setColor(Color.RED);
        float hpWidth = (width - 2) * ((float) bossHealth / 5f);
        renderer.rect(px + 1, py + height + 9, Math.max(0, hpWidth), 5);

        // Massive green shell with yellow spikes
        renderer.setColor(0.12f, 0.55f, 0.18f, 1f);
        renderer.rect(px + 12, py + 12, width - 24, height - 24);

        // Spikes on shell
        renderer.setColor(1.0f, 0.9f, 0.2f, 1f);
        renderer.rect(px + 16, py + height - 10, 8, 8);
        renderer.rect(px + 32, py + height - 10, 8, 8);
        renderer.rect(px + 8, py + 30, 8, 8);

        // Boss Face / Snout
        renderer.setColor(0.95f, 0.65f, 0.15f, 1f);
        float faceX = velocity.x < 0 ? px : px + width - 24;
        renderer.rect(faceX, py + 24, 24, 28);

        // Red hair / mane
        renderer.setColor(0.92f, 0.15f, 0.12f, 1f);
        renderer.rect(faceX + 4, py + 50, 16, 12);

        // Glowing red eyes
        renderer.setColor(Color.RED);
        renderer.rect(faceX + (velocity.x < 0 ? 4 : 14), py + 38, 5, 5);

        // Large Claws / Feet
        renderer.setColor(0.95f, 0.65f, 0.15f, 1f);
        renderer.rect(px + 4, py, 18, 12);
        renderer.rect(px + width - 22, py, 18, 12);
        renderer.setColor(Color.WHITE);
        renderer.rect(px + 4, py, 4, 4);
        renderer.rect(px + width - 8, py, 4, 4);
    }

    public boolean isDead() {
        return state == State.DEAD;
    }

    public boolean isStomped() {
        return state == State.STOMPED;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Type getType() {
        return type;
    }

    public int getBossHealth() {
        return bossHealth;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
