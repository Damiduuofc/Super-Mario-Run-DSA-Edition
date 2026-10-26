package com.damidu.supermariodsa.physics;

import com.damidu.supermariodsa.model.Platform;
import com.damidu.supermariodsa.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionManagerTest {

    private CollisionManager collisionManager;
    private Player player;

    @BeforeEach
    void setUp() {
        collisionManager = new CollisionManager();
        player = new Player(50f, 100f);
    }

    @Test
    @DisplayName("Player falls onto platform and lands securely")
    void testLandingOnPlatform() {
        Platform floor = new Platform(0f, 0f, 200f, 50f, Platform.Type.GROUND);
        Platform[] platforms = new Platform[]{floor};

        // Position player 5px above floor, falling downwards
        player.setX(50f);
        player.setY(55f);
        player.getVelocity().y = -200f;

        // Step by 0.1s (would move downward by 20px without collision)
        collisionManager.update(player, platforms, 0.1f);

        // Player should land on top of platform: y = 50.0
        assertEquals(50f, player.getY(), 0.01f, "Player Y must equal platform top");
        assertEquals(0f, player.getVelocity().y, 0.01f, "Vertical velocity must zero out on landing");
        assertTrue(player.isOnGround(), "Player must be flagged as on ground");
    }

    @Test
    @DisplayName("Player stops horizontally when running into a wall or pipe")
    void testHorizontalWallCollision() {
        Platform pipe = new Platform(100f, 50f, 50f, 60f, Platform.Type.PIPE);
        Platform[] platforms = new Platform[]{pipe};

        // Place player just left of pipe, moving right
        player.setX(90f);
        player.setY(50f);
        player.getVelocity().x = 150f;

        // Step by 0.1s (would advance by 15px to x=105 without collision)
        collisionManager.update(player, platforms, 0.1f);

        // Player should be stopped at pipe.x - Player.WIDTH = 100 - 26 = 74
        assertEquals(100f - Player.WIDTH, player.getX(), 0.01f, "Player X must stop at pipe left edge");
        assertEquals(0f, player.getVelocity().x, 0.01f, "Horizontal velocity must reset to 0 upon wall collision");
    }

    @Test
    @DisplayName("Player jumping upward bonks head on underside of brick block")
    void testCeilingHeadBonk() {
        Platform ceiling = new Platform(50f, 150f, 60f, 24f, Platform.Type.BRICK);
        Platform[] platforms = new Platform[]{ceiling};

        // Player positioned just below the ceiling, moving upwards fast
        player.setX(60f);
        player.setY(140f);
        player.getVelocity().y = 300f;

        collisionManager.update(player, platforms, 0.1f);

        // Player should be pushed below platform: y = 150 - Player.HEIGHT = 150 - 42 = 108
        assertEquals(150f - Player.HEIGHT, player.getY(), 0.01f, "Player Y must be below ceiling block");
        assertEquals(0f, player.getVelocity().y, 0.01f, "Upward velocity must be stopped on ceiling bonk");
        assertFalse(player.isOnGround(), "Player is hitting ceiling, not on ground");
    }

    @Test
    @DisplayName("Player is blocked at the left boundary (x >= 0)")
    void testLeftBoundaryClamp() {
        Platform[] platforms = new Platform[0];

        player.setX(5f);
        player.setY(50f);
        player.getVelocity().x = -200f; // Moving left fast

        collisionManager.update(player, platforms, 0.1f);

        assertEquals(0f, player.getX(), 0.01f, "Player cannot move beyond x=0");
        assertEquals(0f, player.getVelocity().x, 0.01f, "Leftward velocity must reset to 0 at boundary");
    }

    @Test
    @DisplayName("Player falling into deep pit is automatically respawned")
    void testPitRespawn() {
        Platform[] platforms = new Platform[0];

        // Player falls deep below screen
        player.setX(300f);
        player.setY(-120f);
        player.getVelocity().y = -400f;

        collisionManager.update(player, platforms, 0.016f);

        // Should be back at original spawn (50, 100)
        assertEquals(50f, player.getX(), 0.01f, "Player should respawn at initial X");
        assertEquals(100f, player.getY(), 0.01f, "Player should respawn at initial Y");
        assertEquals(0f, player.getVelocity().y, 0.01f, "Velocity should be reset to 0");
    }
}
