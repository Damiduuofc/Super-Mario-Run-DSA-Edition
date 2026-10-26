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
 * Heads-Up Display (HUD) rendering player name, health hearts, lives, score,
 * coins, DSA stack undo count, and queue event toast popups.
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
                toastTimer = 2.4f; // Display toast for 2.4 seconds
            }
        } else if (toastTimer > 0f) {
            toastTimer -= delta;
            if (toastTimer <= 0f) {
                currentToastMessage = null;
            }
        }
    }

    public void showNotification(String message) {
        this.currentToastMessage = message;
        this.toastTimer = 2.4f;
    }

    public void render(SpriteBatch batch, String playerName, Player player, int levelNumber,
                       CustomStack<PlayerStateSnapshot> undoStack, float screenWidth, float screenHeight) {

        // 1. Draw top status bar background banner
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.50f);
        shapeRenderer.rect(0, screenHeight - 48f, screenWidth, 48f);

        // Health Hearts Bar
        for (int i = 0; i < Player.MAX_HEALTH; i++) {
            if (i < player.getHealth()) {
                shapeRenderer.setColor(0.92f, 0.15f, 0.12f, 1f); // Red filled heart
            } else {
                shapeRenderer.setColor(0.35f, 0.35f, 0.38f, 1f); // Empty grey heart
            }
            shapeRenderer.rect(14f + (i * 16f), screenHeight - 42f, 13f, 13f);
        }

        // Active notification toast popup banner
        if (currentToastMessage != null) {
            shapeRenderer.setColor(0.1f, 0.14f, 0.22f, 0.90f);
            float toastWidth = Math.min(420f, screenWidth - 40f);
            shapeRenderer.rect((screenWidth - toastWidth) / 2f, screenHeight - 85f, toastWidth, 26f);
            shapeRenderer.setColor(0.95f, 0.85f, 0.15f, 1f);
            shapeRenderer.rect((screenWidth - toastWidth) / 2f, screenHeight - 85f, toastWidth, 2f);
        }

        shapeRenderer.end();
        batch.begin();

        // 2. Telemetry Text
        font.setColor(Color.WHITE);

        // Player Name & Lives
        String cleanName = (playerName == null || playerName.trim().isEmpty()) ? "Damidu" : playerName.trim();
        font.setColor(0.4f, 0.85f, 1.0f, 1f);
        font.draw(batch, cleanName, 68f, screenHeight - 28f);

        font.setColor(Color.WHITE);
        font.draw(batch, String.format("x %d", player.getLives()), 140f, screenHeight - 28f);
        font.draw(batch, String.format("WORLD 1-%d", levelNumber), 220f, screenHeight - 28f);

        // Coins & Score
        font.setColor(1.0f, 0.88f, 0.15f, 1f);
        font.draw(batch, String.format("COINS x %02d", player.getCoins()), 360f, screenHeight - 28f);
        font.setColor(Color.WHITE);
        font.draw(batch, String.format("SCORE: %06d", player.getScore()), 500f, screenHeight - 28f);

        // Undo Stack Telemetry (DSA Requirement C) & Quick-Save Hint
        font.setColor(0.4f, 0.85f, 1.0f, 1f);
        font.draw(batch, String.format("[Z]Undo:%d | [F5]Save", undoStack.size()), screenWidth - 170f, screenHeight - 28f);

        // Toast text notification from Event Queue
        if (currentToastMessage != null) {
            font.setColor(Color.YELLOW);
            float textX = (screenWidth - 400f) / 2f + 10f;
            font.draw(batch, currentToastMessage, textX, screenHeight - 68f);
        }
    }
}
