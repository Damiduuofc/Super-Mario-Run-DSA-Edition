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
 * Player Profile switching, Leaderboard, Instructions, Settings, and Exit.
 */
public class MainMenuScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private String[] menuItems;
    private int selectedIndex = 0;
    private boolean hasSavedGame;
    private SaveManager.GameProgress savedProgress;
    private String activePlayerName;

    public MainMenuScreen(SuperMarioGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);

        this.activePlayerName = SaveManager.getActivePlayerName();
        refreshMenuOptions();
    }

    private void refreshMenuOptions() {
        this.hasSavedGame = SaveManager.hasSavedProgress();
        this.savedProgress = SaveManager.loadProgress();

        String continueLabel;
        if (hasSavedGame && savedProgress != null) {
            continueLabel = "CONTINUE: " + savedProgress.getFormattedSummary();
        } else {
            continueLabel = "CONTINUE GAME (No Save Available)";
        }

        this.menuItems = new String[]{
                "START NEW GAME",
                continueLabel,
                "PLAYER PROFILE: [" + activePlayerName + "] (Press TAB to switch)",
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

        // Decorative background panel
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().setColor(0.18f, 0.22f, 0.35f, 1f);
        game.getShapeRenderer().rect(60f, 35f, SuperMarioGame.VIRTUAL_WIDTH - 120f, SuperMarioGame.VIRTUAL_HEIGHT - 70f);
        game.getShapeRenderer().setColor(0.95f, 0.82f, 0.15f, 1f);
        game.getShapeRenderer().rect(60f, SuperMarioGame.VIRTUAL_HEIGHT - 110f, SuperMarioGame.VIRTUAL_WIDTH - 120f, 4f);
        game.getShapeRenderer().end();

        game.getBatch().begin();

        // Title
        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(1.5f);
        game.getFont().draw(game.getBatch(), "SUPER MARIO RUN", 260f, 430f);
        game.getFont().getData().setScale(1.0f);
        game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
        game.getFont().draw(game.getBatch(), "-- DATA STRUCTURES & ALGORITHMS EDITION --", 200f, 395f);

        // Menu options
        float startY = 340f;
        for (int i = 0; i < menuItems.length; i++) {
            if (i == selectedIndex) {
                game.getFont().setColor(Color.GREEN);
                game.getFont().draw(game.getBatch(), ">>  " + menuItems[i] + "  <<", 100f, startY - (i * 38f));
            } else {
                if (i == 1 && !hasSavedGame) {
                    game.getFont().setColor(Color.GRAY);
                } else if (i == 2) {
                    game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
                } else {
                    game.getFont().setColor(Color.WHITE);
                }
                game.getFont().draw(game.getBatch(), "    " + menuItems[i], 100f, startY - (i * 38f));
            }
        }

        // Navigation Footer
        game.getFont().setColor(0.8f, 0.8f, 0.8f, 1f);
        game.getFont().draw(game.getBatch(), "Navigate: [W/S] or [Up/Down]  |  Select: [ENTER]  |  Switch Name: [TAB]", 120f, 55f);

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

        // TAB key quickly cycles active player profile name
        if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
            activePlayerName = SaveManager.cycleNextProfile(activePlayerName);
            refreshMenuOptions();
            game.getSoundManager().playCoin();
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            selectOption(selectedIndex);
        }
    }

    private void selectOption(int index) {
        game.getSoundManager().playCoin();

        switch (index) {
            case 0: // Start New Game
                SaveManager.clearProgress();
                game.setScreen(new GameScreen(game, activePlayerName, 1, 3, 3, 0, 0, 40f, 100f));
                break;
            case 1: // Continue Game (Resume Progress)
                if (hasSavedGame && savedProgress != null) {
                    game.setScreen(new GameScreen(
                            game,
                            savedProgress.playerName,
                            savedProgress.currentLevel,
                            savedProgress.lives,
                            savedProgress.health,
                            savedProgress.score,
                            savedProgress.coins,
                            savedProgress.checkpointX,
                            savedProgress.checkpointY
                    ));
                }
                break;
            case 2: // Switch Player Profile Name
                activePlayerName = SaveManager.cycleNextProfile(activePlayerName);
                refreshMenuOptions();
                break;
            case 3: // Leaderboard
                game.setScreen(new LeaderboardScreen(game));
                break;
            case 4: // Instructions
                game.setScreen(new HowToPlayScreen(game));
                break;
            case 5: // Settings / Audio Toggle
                game.getSoundManager().toggleMute();
                refreshMenuOptions();
                break;
            case 6: // Exit
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
