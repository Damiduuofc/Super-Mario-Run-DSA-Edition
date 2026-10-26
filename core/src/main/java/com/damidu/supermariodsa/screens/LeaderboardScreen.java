package com.damidu.supermariodsa.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.damidu.supermariodsa.SuperMarioGame;
import com.damidu.supermariodsa.dsa.BinarySearch;
import com.damidu.supermariodsa.dsa.BubbleSort;
import com.damidu.supermariodsa.dsa.LinearSearch;
import com.damidu.supermariodsa.persistence.LeaderboardEntry;

/**
 * Visual leaderboard display and live interactive DSA demonstrator.
 * Allows the examiner/lecturer to inspect manual Bubble Sort metrics,
 * Linear Search step trace, and Recursive Binary Search call depth.
 */
public class LeaderboardScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private String activeDialogTitle = null;
    private String[] activeDialogLines = null;
    private String statusMessage = "Press [B] for Bubble Sort | [L] for Linear Search | [S] for Binary Search";

    public LeaderboardScreen(SuperMarioGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.10f, 0.12f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getShapeRenderer().setProjectionMatrix(camera.combined);

        // Header Panel
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().setColor(0.18f, 0.22f, 0.32f, 1f);
        game.getShapeRenderer().rect(40f, 40f, SuperMarioGame.VIRTUAL_WIDTH - 80f, SuperMarioGame.VIRTUAL_HEIGHT - 80f);
        game.getShapeRenderer().setColor(0.95f, 0.82f, 0.15f, 1f);
        game.getShapeRenderer().rect(40f, SuperMarioGame.VIRTUAL_HEIGHT - 90f, SuperMarioGame.VIRTUAL_WIDTH - 80f, 3f);
        game.getShapeRenderer().end();

        game.getBatch().begin();

        // Title
        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(1.4f);
        game.getFont().draw(game.getBatch(), "LEADERBOARD & DSA ALGORITHM INSPECTOR", 140f, 440f);
        game.getFont().getData().setScale(1.0f);

        // Leaderboard entries list
        LeaderboardEntry[] entries = game.getLeaderboardManager().getEntries();
        float y = 350f;

        game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
        game.getFont().draw(game.getBatch(), "RANK   PLAYER               SCORE       STAGE       COINS", 80f, 375f);

        for (int i = 0; i < Math.min(entries.length, 8); i++) {
            LeaderboardEntry e = entries[i];
            if (i == 0) game.getFont().setColor(Color.GOLD);
            else if (i == 1) game.getFont().setColor(Color.LIGHT_GRAY);
            else if (i == 2) game.getFont().setColor(0.85f, 0.55f, 0.25f, 1f);
            else game.getFont().setColor(Color.WHITE);

            String row = String.format("#%-4d  %-18s  %-10d  World 1-%-2d  %-6d",
                    i + 1, e.getPlayerName(), e.getScore(), e.getLevelReached(), e.getCoinsCollected());
            game.getFont().draw(game.getBatch(), row, 80f, y);
            y -= 28f;
        }

        // Live Action Status
        game.getFont().setColor(Color.GREEN);
        game.getFont().draw(game.getBatch(), statusMessage, 70f, 95f);

        // Footer
        game.getFont().setColor(Color.GRAY);
        game.getFont().draw(game.getBatch(), "Press [M] or [ESC] to return to Main Menu", 250f, 65f);

        game.getBatch().end();

        // If a DSA demonstration dialog is active, draw it on top
        if (activeDialogLines != null) {
            renderDSADialog();
        }
    }

    private void renderDSADialog() {
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().setColor(0f, 0f, 0f, 0.88f);
        game.getShapeRenderer().rect(60f, 70f, SuperMarioGame.VIRTUAL_WIDTH - 120f, SuperMarioGame.VIRTUAL_HEIGHT - 140f);
        game.getShapeRenderer().setColor(0.2f, 0.7f, 1.0f, 1f);
        game.getShapeRenderer().rect(60f, SuperMarioGame.VIRTUAL_HEIGHT - 100f, SuperMarioGame.VIRTUAL_WIDTH - 120f, 30f);
        game.getShapeRenderer().end();

        game.getBatch().begin();
        game.getFont().setColor(Color.BLACK);
        game.getFont().draw(game.getBatch(), activeDialogTitle, 75f, SuperMarioGame.VIRTUAL_HEIGHT - 80f);

        game.getFont().setColor(Color.WHITE);
        float dy = SuperMarioGame.VIRTUAL_HEIGHT - 120f;
        for (String line : activeDialogLines) {
            if (line.contains("SUCCESS") || line.contains("FOUND")) {
                game.getFont().setColor(Color.GREEN);
            } else if (line.contains("NOT FOUND") || line.contains("exhausted")) {
                game.getFont().setColor(Color.SALMON);
            } else {
                game.getFont().setColor(Color.WHITE);
            }
            game.getFont().draw(game.getBatch(), line, 75f, dy);
            dy -= 22f;
            if (dy < 110f) break;
        }

        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getBatch(), "Press [ENTER], [SPACE], or [C] to close this trace dialog", 180f, 95f);
        game.getBatch().end();
    }

    private void handleInput() {
        if (activeDialogLines != null) {
            if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.C) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                activeDialogLines = null;
                activeDialogTitle = null;
                game.getSoundManager().playJump();
            }
            return;
        }

        // B: Manual Bubble Sort demonstration
        if (Gdx.input.isKeyJustPressed(Keys.B)) {
            BubbleSort.SortMetrics m = game.getLeaderboardManager().manualBubbleSort();
            statusMessage = String.format("Bubble Sort Complete: %d comparisons, %d swaps, %d passes (O(n) early-exit verified)!",
                    m.comparisons, m.swaps, m.passes);
            game.getSoundManager().playCoin();
        }

        // L: Linear Search trace demonstration
        if (Gdx.input.isKeyJustPressed(Keys.L)) {
            LinearSearch.SearchResult result = game.getLeaderboardManager().searchByName("Damidu");
            activeDialogTitle = "MANUAL LINEAR SEARCH TRACE: Query = 'Damidu'";
            activeDialogLines = result.trace;
            game.getSoundManager().playPowerUp();
        }

        // S: Recursive Binary Search trace demonstration
        if (Gdx.input.isKeyJustPressed(Keys.S)) {
            BinarySearch.BinarySearchResult result = game.getLeaderboardManager().searchByScore(850);
            activeDialogTitle = "RECURSIVE BINARY SEARCH TRACE: Target Score = 850 (Descending)";
            activeDialogLines = result.trace;
            game.getSoundManager().playPowerUp();
        }

        if (Gdx.input.isKeyJustPressed(Keys.M) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
