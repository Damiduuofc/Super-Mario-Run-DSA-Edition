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
import com.damidu.supermariodsa.dsa.CustomLinkedList;
import com.damidu.supermariodsa.dsa.CustomQueue;
import com.damidu.supermariodsa.dsa.CustomStack;
import com.damidu.supermariodsa.dsa.RecursionHelper;
import com.damidu.supermariodsa.level.Level;
import com.damidu.supermariodsa.level.LevelManager;
import com.damidu.supermariodsa.model.Coin;
import com.damidu.supermariodsa.model.Enemy;
import com.damidu.supermariodsa.model.GameEvent;
import com.damidu.supermariodsa.model.Platform;
import com.damidu.supermariodsa.model.Player;
import com.damidu.supermariodsa.model.PlayerStateSnapshot;
import com.damidu.supermariodsa.model.PowerUp;
import com.damidu.supermariodsa.physics.CollisionManager;
import com.damidu.supermariodsa.persistence.SaveManager;
import com.damidu.supermariodsa.ui.HUD;

import java.util.List;

/**
 * Main Game Screen managing gameplay, camera scrolling, physics,
 * DSA stack undo, DSA event queue, and progressive level transitions.
 * Supports explicit Save and Resume state persistence.
 */
public class GameScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final String playerName;
    private final int levelNumber;
    private final Level level;
    private final Player player;
    private final CollisionManager collisionManager;
    private final HUD hud;

    // --- MANDATORY DSA STRUCTURES ---
    private final CustomStack<PlayerStateSnapshot> undoStack;
    private final CustomQueue<GameEvent> eventQueue;
    private final CustomLinkedList<String> eventHistoryList;

    private float snapshotTimer = 0f;
    private boolean paused = false;
    private String pauseStatusMessage = null;

    // Recursive 3-2-1-GO Countdown State
    private boolean inCountdown = true;
    private float countdownTimer = 0f;
    private int countdownIndex = 0;
    private final List<String> countdownSequence;

    public GameScreen(SuperMarioGame game, String playerName, int levelNumber,
                      int lives, int health, int score, int coins, float spawnX, float spawnY) {
        this.game = game;
        this.playerName = (playerName == null || playerName.trim().isEmpty()) ? SaveManager.getActivePlayerName() : playerName.trim();
        this.levelNumber = levelNumber;
        this.level = LevelManager.loadLevel(levelNumber);

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);

        float initialX = (spawnX > 0) ? spawnX : level.getSpawnX();
        float initialY = (spawnY > 0) ? spawnY : level.getSpawnY();

        this.player = new Player(initialX, initialY);
        this.player.setLives(lives);
        this.player.setHealth(health > 0 ? health : Player.MAX_HEALTH);
        this.player.setScore(score);
        this.player.setCoins(coins);

        this.collisionManager = new CollisionManager();
        this.hud = new HUD(game.getFont(), game.getShapeRenderer());

        this.undoStack = new CustomStack<>(80);
        this.eventQueue = new CustomQueue<>(40);
        this.eventHistoryList = new CustomLinkedList<>();

        this.countdownSequence = RecursionHelper.buildCountdownTrace(3);

        eventQueue.enqueue(new GameEvent(GameEvent.EventType.LEVEL_COMPLETED,
                "Started " + level.getName() + " - " + this.playerName, 0));

        // Initial save checkpoint
        saveCurrentProgress();
    }

    public GameScreen(SuperMarioGame game, int levelNumber, int lives, int score, int coins) {
        this(game, SaveManager.getActivePlayerName(), levelNumber, lives, Player.MAX_HEALTH, score, coins, -1f, -1f);
    }

    public GameScreen(SuperMarioGame game) {
        this(game, 1, 3, 0, 0);
    }

    public void saveCurrentProgress() {
        SaveManager.saveProgress(
                playerName,
                levelNumber,
                player.getLives(),
                player.getHealth(),
                player.getScore(),
                player.getCoins(),
                player.getX(),
                player.getY()
        );
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        float clampedDelta = Math.min(delta, 1f / 30f);

        handleGlobalKeys();

        // 1. RECURSIVE COUNTDOWN PHASE
        if (inCountdown) {
            updateCountdown(clampedDelta);
            renderWorld();
            renderCountdownOverlay();
            return;
        }

        // 2. PAUSE PHASE
        if (paused) {
            handlePauseKeys();
            renderWorld();
            renderPauseOverlay();
            return;
        }

        // 3. GAMEPLAY UPDATE
        updateGameplay(clampedDelta);

        // 4. RENDER GAME WORLD & ENTITIES
        renderWorld();

        // 5. RENDER HUD
        game.getBatch().begin();
        hud.render(game.getBatch(), playerName, player, levelNumber, undoStack,
                SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT);
        game.getBatch().end();
    }

    private void updateCountdown(float delta) {
        countdownTimer += delta;
        if (countdownTimer >= 0.75f) {
            countdownTimer = 0f;
            countdownIndex++;
            if (countdownIndex >= countdownSequence.size()) {
                inCountdown = false;
                game.getSoundManager().playCoin();
            } else {
                game.getSoundManager().playJump();
            }
        }
    }

    private void updateGameplay(float delta) {
        // Record undo snapshots periodically (DSA Stack)
        snapshotTimer += delta;
        if (snapshotTimer >= 0.35f) {
            snapshotTimer = 0f;
            undoStack.push(player.createSnapshot());
        }

        // Quick-Save feature via F5 or K key
        if (Gdx.input.isKeyJustPressed(Keys.F5) || Gdx.input.isKeyJustPressed(Keys.K)) {
            saveCurrentProgress();
            game.getSoundManager().playCoin();
            hud.showNotification("PROGRESS SAVED! [World 1-" + levelNumber + " | " + player.getScore() + " pts]");
            eventQueue.enqueue(new GameEvent(GameEvent.EventType.LEVEL_COMPLETED,
                    "Game progress saved to file successfully!", 0));
        }

        // Handle user input
        player.handleInput(delta);
        player.applyGravity(delta);

        // Update physics and collisions
        collisionManager.update(player, level.getPlatforms(), delta);

        // Checkpoint detection & Auto-save
        if (player.getX() >= level.getCheckpointX() && player.getX() < level.getCheckpointX() + 40f) {
            player.setSpawnPoint(level.getCheckpointX(), 100f);
            saveCurrentProgress();
        }

        // Update enemies
        for (Enemy enemy : level.getEnemies()) {
            enemy.update(delta, level.getPlatforms());

            if (!enemy.isDead() && !enemy.isStomped() && player.getBounds().overlaps(enemy.getBounds())) {
                boolean isStomp = player.getVelocity().y < 0 && (player.getY() + 10f >= enemy.getY() + enemy.getHeight() * 0.5f);

                if (isStomp || player.hasStarPower()) {
                    boolean defeated = enemy.takeStompDamage();
                    player.bounceOffEnemy();
                    game.getSoundManager().playStomp();

                    int pts = (enemy.getType() == Enemy.Type.BOSS_BOWSER) ? 1000 : 400;
                    player.addScore(pts);

                    String msg = defeated ? ("Defeated " + enemy.getType() + "! +" + pts + " pts") : "Hit Boss Bowser!";
                    eventQueue.enqueue(new GameEvent(GameEvent.EventType.ENEMY_DEFEATED, msg, pts));
                    eventHistoryList.addLast(msg);
                } else {
                    boolean damaged = player.takeDamage(1);
                    if (damaged) {
                        game.getSoundManager().playHurt();
                        eventQueue.enqueue(new GameEvent(GameEvent.EventType.PLAYER_HURT,
                                "Ouch! Lost 1 Health! (" + player.getHealth() + " HP left)", 0));
                    }
                }
            }
        }

        // Update coins
        for (Coin coin : level.getCoins()) {
            coin.update(delta);
            if (!coin.isCollected() && player.getBounds().overlaps(coin.getBounds())) {
                coin.collect();
                player.addCoin();
                game.getSoundManager().playCoin();
                eventQueue.enqueue(new GameEvent(GameEvent.EventType.COIN_COLLECTED,
                        "Coin collected! +200 pts", 200));
            }
        }

        // Update power-ups
        for (PowerUp powerUp : level.getPowerUps()) {
            powerUp.update(delta);
            if (!powerUp.isCollected() && player.getBounds().overlaps(powerUp.getBounds())) {
                powerUp.collect();
                game.getSoundManager().playPowerUp();

                if (powerUp.getType() == PowerUp.Type.SUPER_MUSHROOM) {
                    player.activateSuperMushroom();
                    eventQueue.enqueue(new GameEvent(GameEvent.EventType.POWERUP_ACQUIRED,
                            "Super Mushroom! +1 Health & Size!", 1000));
                } else {
                    player.activateStar(10f);
                    eventQueue.enqueue(new GameEvent(GameEvent.EventType.POWERUP_ACQUIRED,
                            "Star Power! Invincibility activated!", 1000));
                }
            }
        }

        // Update Goal Flag
        level.getGoalFlag().update(delta);
        if (!level.getGoalFlag().isReached() && player.getBounds().overlaps(level.getGoalFlag().getBounds())) {
            level.getGoalFlag().triggerReached();
            game.getSoundManager().playVictory();

            int coinBonus = RecursionHelper.calculateCoinBonus(player.getCoins());
            player.addScore(coinBonus);

            if (levelNumber < LevelManager.TOTAL_LEVELS) {
                // Auto-save progression to next level
                SaveManager.saveProgress(playerName, levelNumber + 1, player.getLives(), player.getHealth(), player.getScore(), player.getCoins(), 40f, 100f);
                game.setScreen(new GameScreen(game, playerName, levelNumber + 1, player.getLives(), player.getHealth(), player.getScore(), player.getCoins(), 40f, 100f));
            } else {
                SaveManager.clearProgress();
                game.getLeaderboardManager().recordScore(playerName, player.getScore(), 3, player.getCoins());
                game.setScreen(new VictoryScreen(game, player.getScore(), player.getCoins()));
            }
            return;
        }

        // Check Game Over
        if (!player.isAlive()) {
            game.getSoundManager().playGameOver();
            SaveManager.clearProgress();
            game.getLeaderboardManager().recordScore(playerName, player.getScore(), levelNumber, player.getCoins());
            game.setScreen(new GameOverScreen(game, player.getScore(), levelNumber, player.getCoins()));
            return;
        }

        hud.update(delta, eventQueue);
        updateCameraScrolling();
    }

    private void updateCameraScrolling() {
        float targetCameraX = player.getX();
        float halfViewportWidth = SuperMarioGame.VIRTUAL_WIDTH / 2f;
        float minX = halfViewportWidth;
        float maxX = Math.max(halfViewportWidth, level.getWidth() - halfViewportWidth);

        camera.position.x = Math.max(minX, Math.min(maxX, targetCameraX));
        camera.position.y = SuperMarioGame.VIRTUAL_HEIGHT / 2f;
        camera.update();
    }

    private void renderWorld() {
        Color sky = level.getSkyColor();
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getShapeRenderer().setProjectionMatrix(camera.combined);

        renderDistantScenery();

        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : level.getPlatforms()) {
            platform.render(game.getShapeRenderer());
        }
        level.getGoalFlag().render(game.getShapeRenderer());
        for (Coin coin : level.getCoins()) {
            coin.render(game.getShapeRenderer());
        }
        for (PowerUp p : level.getPowerUps()) {
            p.render(game.getShapeRenderer());
        }
        for (Enemy enemy : level.getEnemies()) {
            enemy.render(game.getShapeRenderer());
        }
        player.render(game.getShapeRenderer());
        game.getShapeRenderer().end();
    }

    private void renderDistantScenery() {
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        if (levelNumber == 1) {
            game.getShapeRenderer().setColor(0.30f, 0.65f, 0.35f, 0.9f);
            for (float hx = 100f; hx < level.getWidth(); hx += 420f) {
                game.getShapeRenderer().circle(hx, 48f, 120f);
            }
        } else if (levelNumber == 3) {
            game.getShapeRenderer().setColor(0.95f, 0.32f, 0.08f, 1f);
            game.getShapeRenderer().rect(0, 0, level.getWidth(), 30f);
        }
        game.getShapeRenderer().end();
    }

    private void renderCountdownOverlay() {
        game.getBatch().begin();
        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(3.0f);

        String currentText = (countdownIndex < countdownSequence.size()) ? countdownSequence.get(countdownIndex) : "GO!";
        game.getFont().draw(game.getBatch(), currentText,
                SuperMarioGame.VIRTUAL_WIDTH / 2f - 40f, SuperMarioGame.VIRTUAL_HEIGHT / 2f + 40f);

        game.getFont().getData().setScale(1.0f);
        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), "Recursive Call Stack Countdown: Depth (" + (3 - countdownIndex) + ")",
                SuperMarioGame.VIRTUAL_WIDTH / 2f - 160f, SuperMarioGame.VIRTUAL_HEIGHT / 2f - 30f);
        game.getBatch().end();
    }

    private void renderPauseOverlay() {
        game.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        game.getShapeRenderer().setColor(0f, 0f, 0f, 0.75f);
        game.getShapeRenderer().rect(0, 0, SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT);

        // Pause Menu Box
        game.getShapeRenderer().setColor(0.18f, 0.22f, 0.35f, 0.95f);
        game.getShapeRenderer().rect(160f, 90f, SuperMarioGame.VIRTUAL_WIDTH - 320f, SuperMarioGame.VIRTUAL_HEIGHT - 180f);
        game.getShapeRenderer().setColor(0.95f, 0.82f, 0.15f, 1f);
        game.getShapeRenderer().rect(160f, SuperMarioGame.VIRTUAL_HEIGHT - 130f, SuperMarioGame.VIRTUAL_WIDTH - 320f, 3f);
        game.getShapeRenderer().end();

        game.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        game.getBatch().begin();

        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(1.8f);
        game.getFont().draw(game.getBatch(), "GAME PAUSED", 290f, 375f);
        game.getFont().getData().setScale(1.0f);

        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), "[P] or [R]  --  Resume Game", 240f, 310f);

        game.getFont().setColor(Color.GREEN);
        game.getFont().draw(game.getBatch(), "[S]         --  Save Progress to File", 240f, 270f);

        game.getFont().setColor(Color.CYAN);
        game.getFont().draw(game.getBatch(), "[M]         --  Save Progress & Exit to Main Menu", 240f, 230f);

        game.getFont().setColor(Color.LIGHT_GRAY);
        game.getFont().draw(game.getBatch(), "[Q]         --  Quit to Main Menu without saving", 240f, 190f);

        if (pauseStatusMessage != null) {
            game.getFont().setColor(Color.GOLD);
            game.getFont().draw(game.getBatch(), pauseStatusMessage, 220f, 140f);
        }

        game.getBatch().end();
    }

    private void handlePauseKeys() {
        if (Gdx.input.isKeyJustPressed(Keys.S)) {
            saveCurrentProgress();
            game.getSoundManager().playCoin();
            pauseStatusMessage = "PROGRESS SAVED TO FILE! (" + playerName + " | World 1-" + levelNumber + ")";
        }

        if (Gdx.input.isKeyJustPressed(Keys.M)) {
            saveCurrentProgress();
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        if (Gdx.input.isKeyJustPressed(Keys.Q)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        if (Gdx.input.isKeyJustPressed(Keys.R) || Gdx.input.isKeyJustPressed(Keys.P) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            paused = false;
            pauseStatusMessage = null;
            game.getSoundManager().playJump();
        }
    }

    private void handleGlobalKeys() {
        if (Gdx.input.isKeyJustPressed(Keys.P) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            paused = !paused;
            pauseStatusMessage = null;
            game.getSoundManager().playJump();
        }

        // DSA Stack Undo Trigger (Keys Z or U)
        if (!paused && !inCountdown && (Gdx.input.isKeyJustPressed(Keys.Z) || Gdx.input.isKeyJustPressed(Keys.U))) {
            if (!undoStack.isEmpty()) {
                PlayerStateSnapshot snapshot = undoStack.pop();
                player.restoreSnapshot(snapshot);
                game.getSoundManager().playJump();
                eventQueue.enqueue(new GameEvent(GameEvent.EventType.UNDO_PERFORMED,
                        "Undo movement executed! (Stack size: " + undoStack.size() + ")", 0));
            }
        }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height); }
    @Override
    public void pause() { this.paused = true; }
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
