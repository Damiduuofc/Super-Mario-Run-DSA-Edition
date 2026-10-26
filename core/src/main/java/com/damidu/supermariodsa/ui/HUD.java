package com.damidu.supermariodsa.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.damidu.supermariodsa.dsa.CustomQueue;
import com.damidu.supermariodsa.dsa.CustomStack;
import com.damidu.supermariodsa.model.GameEvent;
import com.damidu.supermariodsa.model.Player;
import com.damidu.supermariodsa.model.PlayerStateSnapshot;

/**
 * Heads-Up Display (HUD) rendering health, lives, score, coins,
 * DSA stack undo status, and the event queue notification ticker.
 */
public class HUD {

    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;

    // Toast notification tracking
    private String currentToastMessage = null;
    private float toastTimer = 0f;

    public HUD(BitmapFont font, ShapeRenderer shapeRenderer) {
        this.font = font;
        this.shapeRenderer = shapeRenderer;
    }

    public void update(float delta, CustomQueue<GameEvent> eventQueue) {
        // Dequeue one pending event at a time for on-screen notification
        if (toastTimer <= 0f && !eventQueue.isEmpty()) {
            GameEvent event = eventQueue.dequeue();
            if (event != null) {
                currentToastMessage = event.getMessage();
                toastTimer = 2.2f; // Display toast for 2.2 seconds
            }
        } else if (toastTimer > 0f) {
            toastTimer -= delta;
            if (toastTimer <= 0f) {
                currentToastMessage = null;
            }
        }
    }

    public void render(SpriteBatch batch, Player player, int levelNumber,
                       CustomStack<PlayerStateSnapshot> undoStack, float screenWidth, float screenHeight) {

        // 1. Draw top status bar background banner
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.45f);
        shapeRenderer.rect(0, screenHeight - 48f, screenWidth, 48f);

        // Health Hearts Bar
        for (int i = 0; i < Player.MAX_HEALTH; i++) {
            if (i < player.getHealth()) {
                shapeRenderer.setColor(0.92f, 0.15f, 0.12f, 1f); // Red filled heart
            } else {
                shapeRenderer.setColor(0.35f, 0.35f, 0.38f, 1f); // Empty grey heart
            }
            shapeRenderer.rect(16f + (i * 18f), screenHeight - 42f, 14f, 14f);
        }

        // Active notification toast popup banner
        if (currentToastMessage != null) {
            shapeRenderer.setColor(0.1f, 0.12f, 0.18f, 0.85f);
            float toastWidth = Math.min(360f, screenWidth - 40f);
            shapeRenderer.rect((screenWidth - toastWidth) / 2f, screenHeight - 85f, toastWidth, 26f);
            shapeRenderer.setColor(0.95f, 0.85f, 0.15f, 1f);
            shapeRenderer.rect((screenWidth - toastWidth) / 2f, screenHeight - 85f, toastWidth, 2f);
        }

        shapeRenderer.end();
        batch.begin();

        // 2. Telemetry Text
        font.setColor(Color.WHITE);

        // Player Lives & World
        font.draw(batch, String.format("MARIO x %d", player.getLives()), 80f, screenHeight - 28f);
        font.draw(batch, String.format("WORLD 1-%d", levelNumber), 240f, screenHeight - 28f);

        // Coins & Score
        font.setColor(1.0f, 0.88f, 0.15f, 1f);
        font.draw(batch, String.format("COINS x %02d", player.getCoins()), 400f, screenHeight - 28f);
        font.setColor(Color.WHITE);
        font.draw(batch, String.format("SCORE: %06d", player.getScore()), 560f, screenHeight - 28f);

        // Undo Stack Telemetry (DSA Requirement C)
        font.setColor(0.4f, 0.85f, 1.0f, 1f);
        font.draw(batch, String.format("[Z/U] Undo: %d", undoStack.size()), screenWidth - 110f, screenHeight - 28f);

        // Toast text notification from Event Queue
        if (currentToastMessage != null) {
            font.setColor(Color.YELLOW);
            float textX = (screenWidth - 340f) / 2f + 10f;
            font.draw(batch, currentToastMessage, textX, screenHeight - 68f);
        }
    }
}
