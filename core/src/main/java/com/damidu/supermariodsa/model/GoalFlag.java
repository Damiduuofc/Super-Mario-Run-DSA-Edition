package com.damidu.supermariodsa.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Level exit goal flagpole. Touching triggers victory or level transition.
 */
public class GoalFlag {

    public static final float POLE_WIDTH = 8f;
    public static final float POLE_HEIGHT = 160f;

    private final float x;
    private final float y;
    private final Rectangle bounds;
    private float flagY;
    private boolean reached;

    public GoalFlag(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x - 10f, y, 40f, POLE_HEIGHT);
        this.flagY = y + POLE_HEIGHT - 32f;
        this.reached = false;
    }

    public void update(float delta) {
        if (reached && flagY > y + 16f) {
            flagY -= 120f * delta; // Flag slides smoothly down the pole
        }
    }

    public void render(ShapeRenderer renderer) {
        renderer.set(ShapeRenderer.ShapeType.Filled);

        // Stone base block
        renderer.setColor(0.45f, 0.45f, 0.48f, 1f);
        renderer.rect(x - 12f, y, 32f, 18f);

        // Main flagpole (dark metallic brass)
        renderer.setColor(0.75f, 0.72f, 0.60f, 1f);
        renderer.rect(x, y + 18f, POLE_WIDTH, POLE_HEIGHT - 18f);

        // Golden finial ball at the top
        renderer.setColor(1.0f, 0.85f, 0.15f, 1f);
        renderer.circle(x + POLE_WIDTH / 2f, y + POLE_HEIGHT, 9f);

        // Vibrant triangular victory flag
        renderer.setColor(0.18f, 0.78f, 0.25f, 1f);
        renderer.triangle(
                x - 28f, flagY + 12f,
                x, flagY + 24f,
                x, flagY
        );
    }

    public boolean isReached() {
        return reached;
    }

    public void triggerReached() {
        this.reached = true;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return x;
    }
}
