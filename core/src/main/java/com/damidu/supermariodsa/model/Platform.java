package com.damidu.supermariodsa.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a static physical surface or obstacle in the level.
 * Used with axis-aligned bounding boxes (AABB) for collision detection.
 */
public class Platform {

    public enum Type {
        GROUND,
        FLOATING_GRASS,
        BRICK,
        MYSTERY_BLOCK,
        PIPE
    }

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Type type;
    private final Rectangle bounds;

    public Platform(float x, float y, float width, float height, Type type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Type getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Renders the platform with distinct retro Mario-style block appearances.
     */
    public void render(ShapeRenderer renderer) {
        switch (type) {
            case GROUND:
                renderGround(renderer);
                break;
            case FLOATING_GRASS:
                renderFloatingGrass(renderer);
                break;
            case BRICK:
                renderBrick(renderer);
                break;
            case MYSTERY_BLOCK:
                renderMysteryBlock(renderer);
                break;
            case PIPE:
                renderPipe(renderer);
                break;
        }
    }

    private void renderGround(ShapeRenderer renderer) {
        // Dirt body
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.58f, 0.30f, 0.12f, 1f); // Rich brown
        renderer.rect(x, y, width, height - 12);

        // Darker dirt texture seams
        renderer.setColor(0.42f, 0.20f, 0.08f, 1f);
        for (float sx = x; sx < x + width; sx += 32) {
            renderer.rect(sx + 8, y + 6, 8, 8);
            renderer.rect(sx + 20, y + 20, 8, 8);
        }

        // Top grass strip
        renderer.setColor(0.18f, 0.72f, 0.20f, 1f); // Vibrant grass green
        renderer.rect(x, y + height - 12, width, 12);

        // Grass highlight blade tips
        renderer.setColor(0.35f, 0.88f, 0.30f, 1f);
        renderer.rect(x, y + height - 3, width, 3);
    }

    private void renderFloatingGrass(ShapeRenderer renderer) {
        // Earth base
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.55f, 0.32f, 0.15f, 1f);
        renderer.rect(x, y, width, height - 8);

        // Grass top
        renderer.setColor(0.20f, 0.75f, 0.22f, 1f);
        renderer.rect(x, y + height - 8, width, 8);

        // Grass top edge highlight
        renderer.setColor(0.40f, 0.90f, 0.35f, 1f);
        renderer.rect(x, y + height - 2, width, 2);
    }

    private void renderBrick(ShapeRenderer renderer) {
        renderer.set(ShapeRenderer.ShapeType.Filled);
        // Base brick terra-cotta color
        renderer.setColor(0.78f, 0.30f, 0.18f, 1f);
        renderer.rect(x, y, width, height);

        // Mortar lines / brick outlines
        renderer.setColor(0.35f, 0.12f, 0.08f, 1f);
        float blockSize = 26f;
        for (float bx = x; bx < x + width; bx += blockSize) {
            float bw = Math.min(blockSize, (x + width) - bx);
            // Block border
            renderer.rect(bx, y, bw, 2);
            renderer.rect(bx, y + height - 2, bw, 2);
            renderer.rect(bx, y, 2, height);
            renderer.rect(bx + bw - 2, y, 2, height);

            // Inner brick groove
            renderer.rect(bx, y + height / 2 - 1, bw, 2);
            renderer.rect(bx + bw / 2 - 1, y, 2, height / 2);
            renderer.rect(bx + bw / 4, y + height / 2, 2, height / 2);
        }
    }

    private void renderMysteryBlock(ShapeRenderer renderer) {
        renderer.set(ShapeRenderer.ShapeType.Filled);
        // Golden amber base
        renderer.setColor(0.96f, 0.72f, 0.12f, 1f);
        renderer.rect(x, y, width, height);

        // Dark gold inner border
        renderer.setColor(0.65f, 0.42f, 0.05f, 1f);
        renderer.rect(x, y, width, 2);
        renderer.rect(x, y + height - 2, width, 2);
        renderer.rect(x, y, 2, height);
        renderer.rect(x + width - 2, y, 2, height);

        // Corner rivets
        renderer.setColor(0.30f, 0.18f, 0.02f, 1f);
        renderer.rect(x + 3, y + 3, 3, 3);
        renderer.rect(x + width - 6, y + 3, 3, 3);
        renderer.rect(x + 3, y + height - 6, 3, 3);
        renderer.rect(x + width - 6, y + height - 6, 3, 3);

        // White question mark symbol styling in center
        renderer.setColor(Color.WHITE);
        float cx = x + width / 2;
        float cy = y + height / 2;
        renderer.rect(cx - 3, cy + 3, 6, 3);
        renderer.rect(cx + 1, cy, 2, 4);
        renderer.rect(cx - 1, cy - 2, 2, 3);
        renderer.rect(cx - 1, cy - 7, 2, 2);
    }

    private void renderPipe(ShapeRenderer renderer) {
        renderer.set(ShapeRenderer.ShapeType.Filled);
        // Pipe body
        renderer.setColor(0.12f, 0.65f, 0.18f, 1f);
        renderer.rect(x + 4, y, width - 8, height - 16);

        // Pipe highlight & shadow on body
        renderer.setColor(0.30f, 0.85f, 0.35f, 1f);
        renderer.rect(x + 8, y, 6, height - 16);
        renderer.setColor(0.08f, 0.45f, 0.12f, 1f);
        renderer.rect(x + width - 12, y, 6, height - 16);

        // Pipe rim (top lip)
        renderer.setColor(0.12f, 0.68f, 0.18f, 1f);
        renderer.rect(x, y + height - 16, width, 16);
        renderer.setColor(0.35f, 0.88f, 0.40f, 1f);
        renderer.rect(x + 4, y + height - 16, 8, 16);
        renderer.setColor(0.08f, 0.45f, 0.12f, 1f);
        renderer.rect(x + width - 8, y + height - 16, 6, 16);

        // Pipe lip border
        renderer.setColor(0.04f, 0.28f, 0.08f, 1f);
        renderer.rect(x, y + height - 16, width, 2);
        renderer.rect(x, y + height - 2, width, 2);
    }
}
