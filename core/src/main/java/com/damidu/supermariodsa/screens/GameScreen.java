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
 */
public class GameScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final int levelNumber;
    private final Level level;
    private final Player player;
    private final CollisionManager collisionManager;
    private final HUD hud;

    // --- MANDATORY DSA STRUCTURES ---
    // Stack for undoing player movements (DSA Requirement C)
    private final CustomStack<PlayerStateSnapshot> undoStack;
    // Queue for event notifications (DSA Requirement D)
    private final CustomQueue<GameEvent> eventQueue;
    // Linked List for dynamic event records (DSA Requirement B)
    private final CustomLinkedList<String> eventHistoryList;

    private float snapshotTimer = 0f;
    private boolean paused = false;

    // Recursive 3-2-1-GO Countdown State (DSA Requirement G)
    private boolean inCountdown = true;
    private float countdownTimer = 0f;
    private int countdownIndex = 0;
    private final List<String> countdownSequence;

    public GameScreen(SuperMarioGame game, int levelNumber, int lives, int score, int coins) {
        this.game = game;
        this.levelNumber = levelNumber;
        this.level = LevelManager.loadLevel(levelNumber);

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);

        this.player = new Player(level.getSpawnX(), level.getSpawnY());
        this.player.setLives(lives);
        this.player.setScore(score);
        this.player.setCoins(coins);

        this.collisionManager = new CollisionManager();
        this.hud = new HUD(game.getFont(), game.getShapeRenderer());

        // Initialize DSA Collections
        this.undoStack = new CustomStack<>(80); // Stored up to 80 undo snapshots
        this.eventQueue = new CustomQueue<>(40); // Event queue for notifications
        this.eventHistoryList = new CustomLinkedList<>(); // Dynamic linked list event log

        // Recursively generate 3-2-1-GO sequence
        this.countdownSequence = RecursionHelper.buildCountdownTrace(3);

        eventQueue.enqueue(new GameEvent(GameEvent.EventType.LEVEL_COMPLETED,
                "Started " + level.getName(), 0));

        // Save session checkpoint
        SaveManager.saveProgress(levelNumber, player.getLives(), player.getScore(), player.getCoins());
    }

    public GameScreen(SuperMarioGame game) {
        this(game, 1, 3, 0, 0);
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
        hud.render(game.getBatch(), player, levelNumber, undoStack,
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
        // Record undo snapshots periodically or before movements (DSA Stack)
        snapshotTimer += delta;
        if (snapshotTimer >= 0.35f) {
            snapshotTimer = 0f;
            undoStack.push(player.createSnapshot());
        }

        // Handle user input
        player.handleInput(delta);
        player.applyGravity(delta);

        // Update physics and collisions
        collisionManager.update(player, level.getPlatforms(), delta);

        // Checkpoint detection
        if (player.getX() >= level.getCheckpointX() && player.getX() < level.getCheckpointX() + 50f) {
            player.setSpawnPoint(level.getCheckpointX(), 100f);
        }

        // Update enemies
        for (Enemy enemy : level.getEnemies()) {
            enemy.update(delta, level.getPlatforms());

            if (!enemy.isDead() && !enemy.isStomped() && player.getBounds().overlaps(enemy.getBounds())) {
                // Stomp test: If player is falling downwards and above the enemy's vertical center
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
                    // Player takes damage
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
                    player.activateStar(10f); // 10s star power
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

            // Calculate recursive coin bonus (DSA Requirement G)
            int coinBonus = RecursionHelper.calculateCoinBonus(player.getCoins());
            player.addScore(coinBonus);

            if (levelNumber < LevelManager.TOTAL_LEVELS) {
                // Progress to next level!
                SaveManager.saveProgress(levelNumber + 1, player.getLives(), player.getScore(), player.getCoins());
                game.setScreen(new GameScreen(game, levelNumber + 1, player.getLives(), player.getScore(), player.getCoins()));
            } else {
                // Game Victory! All 3 levels completed!
                SaveManager.clearProgress();
                game.getLeaderboardManager().recordScore("Player", player.getScore(), 3, player.getCoins());
                game.setScreen(new VictoryScreen(game, player.getScore(), player.getCoins()));
            }
            return;
        }

        // Check Game Over
        if (!player.isAlive()) {
            game.getSoundManager().playGameOver();
            SaveManager.clearProgress();
            game.getLeaderboardManager().recordScore("Player", player.getScore(), levelNumber, player.getCoins());
            game.setScreen(new GameOverScreen(game, player.getScore(), levelNumber, player.getCoins()));
            return;
        }

        // Update HUD notifications from Queue
        hud.update(delta, eventQueue);

        // Update Camera Scrolling (DSA Scrolling World, v1.2)
        updateCameraScrolling();
    }

    private void updateCameraScrolling() {
        // Camera smoothly follows player horizontally, clamped to level boundaries
        float targetCameraX = player.getX();
        float halfViewportWidth = SuperMarioGame.VIRTUAL_WIDTH / 2f;
        float minX = halfViewportWidth;
        float maxX = Math.max(halfViewportWidth, level.getWidth() - halfViewportWidth);

        camera.position.x = Math.max(minX, Math.min(maxX, targetCameraX));
        camera.position.y = SuperMarioGame.VIRTUAL_HEIGHT / 2f;
        camera.update();
    }

    private void renderWorld() {
        // Sky Background
        Color sky = level.getSkyColor();
        Gdx.gl.glClearColor(sky.r, sky.g, sky.b, sky.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getShapeRenderer().setProjectionMatrix(camera.combined);

        // 1. Background elements (clouds, hills, castle stones)
        renderDistantScenery();

        // 2. Platforms
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : level.getPlatforms()) {
            platform.render(game.getShapeRenderer());
        }
        game.getShapeRenderer().end();

        // 3. Goal Flag
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        level.getGoalFlag().render(game.getShapeRenderer());
        game.getShapeRenderer().end();

        // 4. Coins & Power-ups
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        for (Coin coin : level.getCoins()) {
            coin.render(game.getShapeRenderer());
        }
        for (PowerUp p : level.getPowerUps()) {
            p.render(game.getShapeRenderer());
        }
        game.getShapeRenderer().end();

        // 5. Enemies
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : level.getEnemies()) {
            enemy.render(game.getShapeRenderer());
        }
        game.getShapeRenderer().end();

        // 6. Player
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
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
            // Castle fiery lava glow at bottom pits
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
        game.getShapeRenderer().setColor(0f, 0f, 0f, 0.65f);
        game.getShapeRenderer().rect(0, 0, SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT);
        game.getShapeRenderer().end();

        game.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        game.getBatch().begin();
        game.getFont().setColor(Color.YELLOW);
        game.getFont().getData().setScale(1.8f);
        game.getFont().draw(game.getBatch(), "GAME PAUSED", 310f, 300f);
        game.getFont().getData().setScale(1.0f);
        game.getFont().setColor(Color.WHITE);
        game.getFont().draw(game.getBatch(), "Press [P] or [ESC] to Resume", 300f, 240f);
        game.getFont().draw(game.getBatch(), "Press [M] to return to Main Menu", 280f, 200f);
        game.getBatch().end();
    }

    private void handleGlobalKeys() {
        // Pause / Resume Toggle
        if (Gdx.input.isKeyJustPressed(Keys.P) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            paused = !paused;
            game.getSoundManager().playJump();
        }

        // Return to main menu when paused
        if (paused && Gdx.input.isKeyJustPressed(Keys.M)) {
            game.setScreen(new MainMenuScreen(game));
            return;
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
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() { this.paused = true; }
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
}
