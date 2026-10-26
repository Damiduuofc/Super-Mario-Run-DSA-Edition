package com.damidu.supermariodsa;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.damidu.supermariodsa.audio.SoundManager;
import com.damidu.supermariodsa.persistence.LeaderboardManager;
import com.damidu.supermariodsa.screens.MainMenuScreen;

/**
 * Root game application class managing shared resources, audio, persistence,
 * and clean state machine screen transitions.
 */
public class SuperMarioGame extends Game {

    public static final int VIRTUAL_WIDTH = 800;
    public static final int VIRTUAL_HEIGHT = 480;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SoundManager soundManager;
    private LeaderboardManager leaderboardManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        soundManager = new SoundManager();
        leaderboardManager = new LeaderboardManager();

        // Launch into the Main Menu screen
        setScreen(new MainMenuScreen(this));
    }

    public SpriteBatch getBatch() { return batch; }
    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }
    public BitmapFont getFont() { return font; }
    public SoundManager getSoundManager() { return soundManager; }
    public LeaderboardManager getLeaderboardManager() { return leaderboardManager; }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (soundManager != null) soundManager.dispose();
    }
}
