package com.damidu.supermariodsa.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Collectible power-up enhancing player abilities and health.
 */
public class PowerUp {

    public enum Type {
        SUPER_MUSHROOM, // Adds +1 Health / Max Health and +1000 score
        STAR_INVINCIBILITY // 10 seconds of invincibility, rainbow glow, instakill enemies
    }

    public static final float SIZE = 24f;

    private final float x;
    private final float y;
    private final Type type;
    private final Rectangle bounds;
    private boolean collected;
    private float floatTimer;

    public PowerUp(float x, float y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.collected = false;
        this.floatTimer = 0f;
    }

    public void update(float delta) {
        floatTimer += delta * 4f;
    }

    public void render(ShapeRenderer renderer) {
        if (collected) return;

        renderer.set(ShapeRenderer.ShapeType.Filled);
        float bobY = y + (float) Math.sin(floatTimer) * 4f;

        if (type == Type.SUPER_MUSHROOM) {
            // Mushroom stem (peach/white)
            renderer.setColor(0.98f, 0.95f, 0.88f, 1f);
            renderer.rect(x + 5, bobY, 14, 10);

            // Red cap
            renderer.setColor(0.92f, 0.15f, 0.12f, 1f);
            renderer.rect(x + 2, bobY + 8, SIZE - 4, 12);
            renderer.rect(x + 4, bobY + 18, SIZE - 8, 4);

            // White spots
            renderer.setColor(Color.WHITE);
            renderer.rect(x + 5, bobY + 12, 4, 4);
            renderer.rect(x + 15, bobY + 12, 4, 4);
            renderer.rect(x + 10, bobY + 16, 4, 3);
        } else {
            // Star power-up: vibrant golden star
            renderer.setColor(1.0f, 0.9f, 0.1f, 1f);
            renderer.rect(x + 4, bobY + 4, SIZE - 8, SIZE - 8);
            renderer.rect(x + 8, bobY, SIZE - 16, SIZE);
            renderer.rect(x, bobY + 8, SIZE, SIZE - 16);

            // Eyes
            renderer.setColor(Color.BLACK);
            renderer.rect(x + 8, bobY + 12, 2, 6);
            renderer.rect(x + 14, bobY + 12, 2, 6);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }

    public Type getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
