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
import com.damidu.supermariodsa.persistence.SaveManager;

/**
 * Main Menu screen providing navigation between Start, Continue,
 * Leaderboard, Instructions, Settings, and Exit.
 */
public class MainMenuScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final String[] menuItems;
    private int selectedIndex = 0;
    private final boolean hasSavedGame;

    public MainMenuScreen(SuperMarioGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);

        this.hasSavedGame = SaveManager.hasSavedProgress();
        this.menuItems = new String[]{
                "START GAME",
                hasSavedGame ? "CONTINUE GAME" : "CONTINUE GAME (No Save)",
                "LEADERBOARD & DSA SEARCH",
                "HOW TO PLAY & DSA GUIDE",
                "SETTINGS (AUDIO: " + (game.getSoundManager().isMuted() ? "OFF" : "ON") + ")",
                "EXIT"
        };
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.12f, 0.16f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getShapeRenderer().setProjectionMatrix(camera.combined);

        // Decorative background banner
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().setColor(0.18f, 0.22f, 0.35f, 1f);
        game.getShapeRenderer().rect(100f, 40f, SuperMarioGame.VIRTUAL_WIDTH - 200f, SuperMarioGame.VIRTUAL_HEIGHT - 80f);
        game.getShapeRenderer().setColor(0.95f, 0.82f, 0.15f, 1f);
        game.getShapeRenderer().rect(100f, SuperMarioGame.VIRTUAL_HEIGHT - 120f, SuperMarioGame.VIRTUAL_WIDTH - 200f, 4f);
        game.getShapeRenderer().end();

        game.getBatch().begin();

        // Title
        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(1.5f);
        game.getFont().draw(game.getBatch(), "SUPER MARIO RUN", 260f, 420f);
        game.getFont().getData().setScale(1.0f);
        game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
        game.getFont().draw(game.getBatch(), "-- DATA STRUCTURES & ALGORITHMS EDITION --", 200f, 385f);

        // Menu options
        float startY = 320f;
        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedIndex) {
                game.getFont().setColor(Color.GREEN);
                game.getFont().draw(game.getBatch(), ">>  " + menuItems[i] + "  <<", 250f, startY - (i * 44f));
            } else {
                if (i == 1 && !hasSavedGame) {
                    game.getFont().setColor(Color.GRAY);
                } else {
                    game.getFont().setColor(Color.WHITE);
                }
                game.getFont().draw(game.getBatch(), "    " + menuItems[i], 250f, startY - (i * 44f));
            }
        }

        // Navigation Footer
        game.getFont().setColor(0.8f, 0.8f, 0.8f, 1f);
        game.getFont().draw(game.getBatch(), "Navigate: [W/S] or [Up/Down]  |  Select: [ENTER] or [SPACE]", 160f, 65f);

        game.getBatch().end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
            game.getSoundManager().playJump();
        }
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            selectedIndex = (selectedIndex + 1) % menuItems.length;
            game.getSoundManager().playJump();
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            selectOption(selectedIndex);
        }
    }

    private void selectOption(int index) {
        game.getSoundManager().playCoin();

        switch (index) {
            case 0: // Start Game
                SaveManager.clearProgress();
                game.setScreen(new GameScreen(game, 1, 3, 0, 0));
                break;
            case 1: // Continue Game
                if (hasSavedGame) {
                    SaveManager.GameProgress progress = SaveManager.loadProgress();
                    if (progress != null) {
                        game.setScreen(new GameScreen(game, progress.currentLevel, progress.lives, progress.score, progress.coins));
                    } else {
                        game.setScreen(new GameScreen(game, 1, 3, 0, 0));
                    }
                }
                break;
            case 2: // Leaderboard
                game.setScreen(new LeaderboardScreen(game));
                break;
            case 3: // Instructions
                game.setScreen(new HowToPlayScreen(game));
                break;
            case 4: // Settings / Audio Toggle
                game.getSoundManager().toggleMute();
                menuItems[4] = "SETTINGS (AUDIO: " + (game.getSoundManager().isMuted() ? "OFF" : "ON") + ")";
                break;
            case 5: // Exit
                Gdx.app.exit();
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
