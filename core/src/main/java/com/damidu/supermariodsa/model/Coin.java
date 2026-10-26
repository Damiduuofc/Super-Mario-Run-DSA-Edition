package com.damidu.supermariodsa.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Collectible spinning golden coin awarding points and progression credit.
 */
public class Coin {

    public static final float SIZE = 18f;

    private final float x;
    private final float y;
    private final Rectangle bounds;
    private boolean collected;
    private float animationTimer;

    public Coin(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.collected = false;
        this.animationTimer = (float) (Math.random() * 2.0); // Offset spin phase
    }

    public void update(float delta) {
        animationTimer += delta * 6f;
    }

    public void render(ShapeRenderer renderer) {
        if (collected) return;

        renderer.set(ShapeRenderer.ShapeType.Filled);

        // Sinusoidal spin width factor (1.0 -> 0.1 -> 1.0)
        float spinFactor = Math.abs((float) Math.cos(animationTimer));
        float currentWidth = Math.max(3f, SIZE * spinFactor);
        float renderX = x + (SIZE - currentWidth) / 2f;

        // Outer bright golden amber rim
        renderer.setColor(1.0f, 0.84f, 0.0f, 1f);
        renderer.rect(renderX, y, currentWidth, SIZE);

        // Inner lighter gold highlight
        renderer.setColor(1.0f, 0.95f, 0.35f, 1f);
        renderer.rect(renderX + 2f * spinFactor, y + 2f, Math.max(1f, currentWidth - 4f * spinFactor), SIZE - 4f);

        // Center rectangular coin slit
        renderer.setColor(0.85f, 0.65f, 0.05f, 1f);
        renderer.rect(renderX + (currentWidth - 2f * spinFactor) / 2f, y + 4f, Math.max(1f, 2f * spinFactor), SIZE - 8f);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
