package com.damidu.supermariodsa.physics;

import com.badlogic.gdx.math.Rectangle;
import com.damidu.supermariodsa.model.Platform;
import com.damidu.supermariodsa.model.Player;

/**
 * Manages Axis-Aligned Bounding Box (AABB) collision detection and resolution.
 * Separates X and Y collision phases to prevent corner-sticking and tunneling.
 */
public class CollisionManager {

    private final Rectangle testBounds = new Rectangle();

    /**
     * Updates player position along both axes with collision detection and response.
     *
     * @param player    The active player instance.
     * @param platforms Array of solid platforms in the level.
     * @param delta     Elapsed time since last frame.
     */
    public void update(Player player, Platform[] platforms, float delta) {
        float playerWidth = player.getBounds().width;
        float playerHeight = player.getBounds().height;

        // --- 1. HORIZONTAL AXIS MOVEMENT & RESOLUTION ---
        float newX = player.getX() + player.getVelocity().x * delta;

        // Level boundary: Prevent walking off the left edge
        if (newX < 0f) {
            newX = 0f;
            player.getVelocity().x = 0f;
        }

        testBounds.set(newX, player.getY(), playerWidth, playerHeight);

        for (Platform platform : platforms) {
            if (testBounds.overlaps(platform.getBounds())) {
                if (player.getVelocity().x > 0f) {
                    newX = platform.getX() - playerWidth;
                } else if (player.getVelocity().x < 0f) {
                    newX = platform.getX() + platform.getWidth();
                }
                player.getVelocity().x = 0f;
                testBounds.x = newX;
            }
        }
        player.setX(newX);

        // --- 2. VERTICAL AXIS MOVEMENT & RESOLUTION ---
        float newY = player.getY() + player.getVelocity().y * delta;
        testBounds.set(player.getX(), newY, playerWidth, playerHeight);

        boolean grounded = false;

        for (Platform platform : platforms) {
            if (testBounds.overlaps(platform.getBounds())) {
                if (player.getVelocity().y <= 0f) {
                    // Falling down: land on the top surface of the platform
                    newY = platform.getY() + platform.getHeight();
                    player.getVelocity().y = 0f;
                    grounded = true;
                } else if (player.getVelocity().y > 0f) {
                    // Jumping up: hit underside / ceiling of the platform
                    newY = platform.getY() - playerHeight;
                    player.getVelocity().y = 0f;
                }
                testBounds.y = newY;
            }
        }

        player.setY(newY);
        player.setOnGround(grounded);

        // --- 3. LEVEL BOUNDS / PIT RECOVERY ---
        if (player.getY() < -100f) {
            player.takeDamage(1);
            player.respawn();
        }

        player.update(delta);
    }
}
