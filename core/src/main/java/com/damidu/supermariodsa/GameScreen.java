package com.damidu.supermariodsa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.damidu.supermariodsa.model.Platform;
import com.damidu.supermariodsa.model.Player;
import com.damidu.supermariodsa.physics.CollisionManager;

/**
 * Main gameplay screen for Version 1.0.
 * Demonstrates the rendering loop, world rendering, player kinematics,
 * jumping, gravity, and AABB platform collision detection.
 */
public class GameScreen implements Screen {

    private final SuperMarioGame game;
    private final OrthographicCamera camera;
    private final FitViewport viewport;

    private final Player player;
    private final CollisionManager collisionManager;

    // Platform layout stored in a plain Java array (DSA requirement A)
    private final Platform[] platforms;

    public GameScreen(SuperMarioGame game) {
        this.game = game;

        // Virtual coordinate system: 800 x 480
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SuperMarioGame.VIRTUAL_WIDTH, SuperMarioGame.VIRTUAL_HEIGHT, camera);
        this.camera.position.set(SuperMarioGame.VIRTUAL_WIDTH / 2f, SuperMarioGame.VIRTUAL_HEIGHT / 2f, 0f);

        // Spawn player above the starting ground
        this.player = new Player(40f, 100f);
        this.collisionManager = new CollisionManager();

        // Initialize platforms array: ground, steps, floating platforms, and pipe obstacle
        this.platforms = new Platform[] {
                // Ground section 1 (left side)
                new Platform(0f, 0f, 480f, 48f, Platform.Type.GROUND),
                // Pit gap from x=480 to x=560 (80px gap to jump across!)
                // Ground section 2 (right side)
                new Platform(560f, 0f, 240f, 48f, Platform.Type.GROUND),

                // Stepping stone stairs
                new Platform(80f, 48f, 36f, 24f, Platform.Type.BRICK),
                new Platform(116f, 48f, 36f, 48f, Platform.Type.BRICK),

                // Floating grass ledge
                new Platform(190f, 130f, 110f, 22f, Platform.Type.FLOATING_GRASS),

                // Mystery block suspended in the air
                new Platform(250f, 220f, 30f, 30f, Platform.Type.MYSTERY_BLOCK),

                // Elevated brick platform
                new Platform(330f, 190f, 120f, 24f, Platform.Type.BRICK),

                // High floating grass platform
                new Platform(490f, 260f, 130f, 22f, Platform.Type.FLOATING_GRASS),

                // Green Warp Pipe obstacle
                new Platform(680f, 48f, 48f, 64f, Platform.Type.PIPE)
        };
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Clamp excessive delta time (e.g. during window dragging) to prevent physics jumps
        float clampedDelta = Math.min(delta, 1f / 30f);

        // 1. UPDATE GAME LOGIC & PHYSICS
        player.handleInput(clampedDelta);
        player.applyGravity(clampedDelta);
        collisionManager.update(player, platforms, clampedDelta);

        // 2. CLEAR SCREEN WITH MARIO SKY BLUE
        Gdx.gl.glClearColor(0.42f, 0.70f, 0.98f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getShapeRenderer().setProjectionMatrix(camera.combined);
        game.getBatch().setProjectionMatrix(camera.combined);

        // 3. RENDER BACKGROUND SCENERY (Distant hills and clouds)
        renderBackground(game.getShapeRenderer());

        // 4. RENDER PLATFORMS & OBSTACLES
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : platforms) {
            platform.render(game.getShapeRenderer());
        }
        game.getShapeRenderer().end();

        // 5. RENDER PLAYER
        game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        player.render(game.getShapeRenderer());
        game.getShapeRenderer().end();

        // 6. RENDER HUD & ON-SCREEN TELEMETRY
        game.getBatch().begin();
        renderHUD();
        game.getBatch().end();
    }

    /**
     * Renders clouds and rolling hills in the background.
     */
    private void renderBackground(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Distant rolling green hills
        renderer.setColor(0.28f, 0.65f, 0.32f, 0.9f);
        renderer.circle(160f, 48f, 110f);
        renderer.circle(620f, 48f, 140f);

        // Fluffy clouds
        drawCloud(renderer, 120f, 380f, 1.0f);
        drawCloud(renderer, 380f, 410f, 1.2f);
        drawCloud(renderer, 650f, 360f, 0.9f);

        renderer.end();
    }

    private void drawCloud(ShapeRenderer renderer, float cx, float cy, float scale) {
        renderer.setColor(Color.WHITE);
        renderer.circle(cx - 20f * scale, cy, 18f * scale);
        renderer.circle(cx, cy + 6f * scale, 24f * scale);
        renderer.circle(cx + 22f * scale, cy, 18f * scale);
        renderer.rect(cx - 20f * scale, cy - 14f * scale, 42f * scale, 14f * scale);
    }

    /**
     * Renders telemetry and instruction text onto the screen.
     */
    private void renderHUD() {
        game.getFont().setColor(Color.WHITE);

        // Header Title
        game.getFont().draw(game.getBatch(), "SUPER MARIO RUN -- DSA EDITION (v1.0 Prototype)", 16f, 464f);

        // Real-time kinematic telemetry
        String telemetry = String.format("Pos: (%.1f, %.1f) | Vel: (%.1f, %.1f) | OnGround: %b | Facing: %s",
                player.getX(), player.getY(),
                player.getVelocity().x, player.getVelocity().y,
                player.isOnGround(),
                player.isFacingRight() ? "RIGHT" : "LEFT");
        game.getFont().draw(game.getBatch(), telemetry, 16f, 442f);

        // Controls reminder
        game.getFont().setColor(Color.YELLOW);
        game.getFont().draw(game.getBatch(), "Controls: [A/D or Arrows] Move  |  [W / Up / Space] Jump  |  [R] Respawn", 16f, 420f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
