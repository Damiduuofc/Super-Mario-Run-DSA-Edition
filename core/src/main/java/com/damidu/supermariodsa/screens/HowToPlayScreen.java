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

/**
 * How to Play guide and viva reference architecture explanation screen.
 */
public class HowToPlayScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    public HowToPlayScreen(SuperMarioGame game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.M) || Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        Gdx.gl.glClearColor(0.12f, 0.14f, 0.22f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getShapeRenderer().setProjectionMatrix(camera.combined);

        // Panel
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().setColor(0.16f, 0.20f, 0.30f, 1f);
        game.getShapeRenderer().rect(30f, 30f, SuperMarioGame.VIRTUAL_WIDTH - 60f, SuperMarioGame.VIRTUAL_HEIGHT - 60f);
        game.getShapeRenderer().setColor(0.95f, 0.82f, 0.15f, 1f);
        game.getShapeRenderer().rect(30f, SuperMarioGame.VIRTUAL_HEIGHT - 75f, SuperMarioGame.VIRTUAL_WIDTH - 60f, 3f);
        game.getShapeRenderer().end();

        game.getBatch().begin();

        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(1.3f);
        game.getFont().draw(game.getBatch(), "HOW TO PLAY & DSA ARCHITECTURE GUIDE", 150f, 445f);
        game.getFont().getData().setScale(1.0f);

        float y = 390f;

        // Section 1: Gameplay controls
        game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
        game.getFont().draw(game.getBatch(), "GAMEPLAY CONTROLS:", 50f, y);
        y -= 22f;
        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), "* [A / D] or [Left / Right]: Run horizontally with smooth acceleration & friction", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "* [W / Up / Space]: Jump arc (Stomp enemy heads from above to defeat them!)", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "* [Z / U]: UNDO MOVEMENT - Restores previous player snapshot from CustomStack", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "* [P / ESC]: Pause & Resume game  |  Power-Ups: Mushroom (+1 HP), Star (Invincible)", 60f, y); y -= 30f;

        // Section 2: DSA Requirements Viva mapping
        game.getFont().setColor(0.4f, 0.85f, 1.0f, 1f);
        game.getFont().draw(game.getBatch(), "MANDATORY DSA CONCEPTS (UNIVERSITY RUBRIC IMPLEMENTED):", 50f, y);
        y -= 22f;
        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), "1. ARRAYS: Stored Level platforms[], coins[], enemies[] & resizable LeaderboardEntry[]", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "2. LINKED LIST: CustomLinkedList<T> doubly linked node list for dynamic game events", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "3. STACK: CustomStack<T> LIFO history buffer for state undo with capacity bounds", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "4. QUEUE: CustomQueue<T> FIFO queue for buffering and delivering toast event popups", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "5. BUBBLE SORT: Manual BubbleSort.sort() ranking scores descending with early-exit flag", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "6. SEARCHING: LinearSearch by player name & Recursive BinarySearch by score with trace", 60f, y); y -= 20f;
        game.getFont().draw(game.getBatch(), "7. RECURSION: 3-2-1-GO call stack countdown & recursive coin score bonus formula", 60f, y); y -= 26f;

        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getBatch(), "Press [ENTER], [SPACE], or [M] to return to Main Menu", 210f, 55f);

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
