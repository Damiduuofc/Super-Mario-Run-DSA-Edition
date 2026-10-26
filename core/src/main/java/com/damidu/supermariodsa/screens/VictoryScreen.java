package com.damidu.supermariodsa.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.damidu.supermariodsa.SuperMarioGame;
import com.damidu.supermariodsa.dsa.RecursionHelper;

public class VictoryScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final int finalScore;
    private final int coinsCollected;
    private final int coinBonus;

    public VictoryScreen(SuperMarioGame game, int finalScore, int coinsCollected) {
        this.game = game;
        this.finalScore = finalScore;
        this.coinsCollected = coinsCollected;
        this.coinBonus = RecursionHelper.calculateCoinBonus(coinsCollected);

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.M)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        Gdx.gl.glClearColor(0.08f, 0.16f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();

        game.getFont().setColor(Color.GREEN);
        game.getFont().getData().setScale(2.0f);
        game.getFont().draw(game.getBatch(), "CONGRATULATIONS!", 250f, 410f);

        game.getFont().getData().setScale(1.3f);
        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getBatch(), "YOU SAVED THE KINGDOM & DEFEATED BOWSER!", 130f, 350f);

        game.getFont().getData().setScale(1.0f);
        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), "All 3 Progressive Levels Successfully Completed!", 220f, 290f);
        game.getFont().draw(game.getBatch(), String.format("Total Score: %d points", finalScore), 280f, 250f);
        game.getFont().draw(game.getBatch(), String.format("Coins Collected: %d", coinsCollected), 280f, 220f);
        game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
        game.getFont().draw(game.getBatch(), String.format("Recursive Coin Bonus Awarded: +%d pts", coinBonus), 240f, 190f);

        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getBatch(), "Press [ENTER] or [M] to return to Main Menu", 230f, 100f);

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
