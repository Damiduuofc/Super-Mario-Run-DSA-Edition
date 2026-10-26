package com.damidu.supermariodsa.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.damidu.supermariodsa.SuperMarioGame;

public class GameOverScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final int finalScore;
    private final int levelReached;
    private final int coinsCollected;

    public GameOverScreen(SuperMarioGame game, int finalScore, int levelReached, int coinsCollected) {
        this.game = game;
        this.finalScore = finalScore;
        this.levelReached = levelReached;
        this.coinsCollected = coinsCollected;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.R) || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            game.setScreen(new GameScreen(game, 1, 3, 0, 0));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Keys.M) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        Gdx.gl.glClearColor(0.1f, 0.04f, 0.04f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();

        game.getFont().setColor(Color.RED);
        game.getFont().getData().setScale(2.2f);
        game.getFont().draw(game.getBatch(), "GAME OVER", 310f, 380f);

        game.getFont().getData().setScale(1.0f);
        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), String.format("Final Score: %d points", finalScore), 320f, 290f);
        game.getFont().draw(game.getBatch(), String.format("Reached: World 1-%d", levelReached), 320f, 260f);
        game.getFont().draw(game.getBatch(), String.format("Coins Collected: %d", coinsCollected), 320f, 230f);

        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getBatch(), "Press [R] or [SPACE] to Retry from Level 1", 240f, 150f);
        game.getFont().setColor(0.7f, 0.7f, 0.8f, 1f);
        game.getFont().draw(game.getBatch(), "Press [M] to return to Main Menu", 280f, 110f);

        game.getBatch().end();
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
