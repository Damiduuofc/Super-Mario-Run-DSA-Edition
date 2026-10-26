package com.damidu.supermariodsa.level;

import com.badlogic.gdx.graphics.Color;
import com.damidu.supermariodsa.model.Coin;
import com.damidu.supermariodsa.model.Enemy;
import com.damidu.supermariodsa.model.GoalFlag;
import com.damidu.supermariodsa.model.Platform;
import com.damidu.supermariodsa.model.PowerUp;

/**
 * Builds the 3 increasingly challenging levels for the game progression.
 * Demonstrates storing level data in structured arrays.
 */
public class LevelManager {

    public static final int TOTAL_LEVELS = 3;

    public static Level loadLevel(int levelNumber) {
        switch (levelNumber) {
            case 1:
                return createLevel1();
            case 2:
                return createLevel2();
            case 3:
                return createLevel3();
            default:
                return createLevel1();
        }
    }

    private static Level createLevel1() {
        Color skyBlue = new Color(0.42f, 0.70f, 0.98f, 1f);

        Platform[] platforms = new Platform[] {
                // Ground segments with pit gaps
                new Platform(0f, 0f, 600f, 48f, Platform.Type.GROUND),
                new Platform(680f, 0f, 600f, 48f, Platform.Type.GROUND), // 80px pit
                new Platform(1360f, 0f, 850f, 48f, Platform.Type.GROUND), // 80px pit

                // Stairs
                new Platform(180f, 48f, 32f, 24f, Platform.Type.BRICK),
                new Platform(212f, 48f, 32f, 48f, Platform.Type.BRICK),

                // Floating Grass & Mystery blocks
                new Platform(320f, 140f, 96f, 22f, Platform.Type.FLOATING_GRASS),
                new Platform(450f, 180f, 30f, 30f, Platform.Type.MYSTERY_BLOCK),
                new Platform(500f, 180f, 60f, 24f, Platform.Type.BRICK),

                // Mid-level platforms over pit
                new Platform(640f, 120f, 60f, 20f, Platform.Type.FLOATING_GRASS),
                new Platform(780f, 160f, 90f, 22f, Platform.Type.FLOATING_GRASS),
                new Platform(920f, 200f, 30f, 30f, Platform.Type.MYSTERY_BLOCK),

                // Warp pipes
                new Platform(1150f, 48f, 48f, 60f, Platform.Type.PIPE),
                new Platform(1550f, 48f, 48f, 75f, Platform.Type.PIPE),

                // High finish platform
                new Platform(1750f, 140f, 140f, 24f, Platform.Type.BRICK),
                new Platform(1920f, 48f, 40f, 30f, Platform.Type.BRICK),
                new Platform(1960f, 48f, 40f, 60f, Platform.Type.BRICK)
        };

        Coin[] coins = new Coin[] {
                new Coin(330f, 175f), new Coin(360f, 175f), new Coin(390f, 175f),
                new Coin(510f, 220f), new Coin(530f, 220f),
                new Coin(800f, 195f), new Coin(830f, 195f),
                new Coin(1420f, 80f), new Coin(1460f, 80f), new Coin(1500f, 80f),
                new Coin(1770f, 175f), new Coin(1810f, 175f), new Coin(1850f, 175f)
        };

        Enemy[] enemies = new Enemy[] {
                new Enemy(420f, 48f, Enemy.Type.GOOMBA, 90f),
                new Enemy(900f, 48f, Enemy.Type.GOOMBA, 110f),
                new Enemy(1480f, 48f, Enemy.Type.GOOMBA, 60f),
                new Enemy(1820f, 48f, Enemy.Type.GOOMBA, 80f)
        };

        PowerUp[] powerUps = new PowerUp[] {
                new PowerUp(453f, 215f, PowerUp.Type.SUPER_MUSHROOM),
                new PowerUp(923f, 235f, PowerUp.Type.STAR_INVINCIBILITY)
        };

        GoalFlag flag = new GoalFlag(2080f, 48f);

        return new Level(1, "World 1-1: Sunny Grassland", 2200f, 480f, skyBlue,
                platforms, coins, enemies, powerUps, flag, 40f, 100f, 1050f);
    }

    private static Level createLevel2() {
        Color caveDarkSlate = new Color(0.12f, 0.14f, 0.20f, 1f);

        Platform[] platforms = new Platform[] {
                new Platform(0f, 0f, 400f, 48f, Platform.Type.GROUND),
                new Platform(490f, 0f, 350f, 48f, Platform.Type.GROUND), // 90px pit
                new Platform(930f, 0f, 450f, 48f, Platform.Type.GROUND), // 90px pit
                new Platform(1480f, 0f, 400f, 48f, Platform.Type.GROUND), // 100px pit
                new Platform(1960f, 0f, 600f, 48f, Platform.Type.GROUND),

                // Cave ledges & floating blocks
                new Platform(200f, 120f, 80f, 22f, Platform.Type.BRICK),
                new Platform(340f, 180f, 80f, 22f, Platform.Type.BRICK),
                new Platform(450f, 130f, 60f, 20f, Platform.Type.FLOATING_GRASS),
                new Platform(600f, 190f, 100f, 22f, Platform.Type.BRICK),
                new Platform(750f, 140f, 70f, 20f, Platform.Type.FLOATING_GRASS),

                // Pipes
                new Platform(1100f, 48f, 50f, 80f, Platform.Type.PIPE),
                new Platform(1380f, 120f, 60f, 22f, Platform.Type.FLOATING_GRASS),
                new Platform(1620f, 170f, 110f, 24f, Platform.Type.BRICK),
                new Platform(1850f, 130f, 70f, 22f, Platform.Type.FLOATING_GRASS)
        };

        Coin[] coins = new Coin[] {
                new Coin(220f, 150f), new Coin(250f, 150f),
                new Coin(620f, 220f), new Coin(650f, 220f),
                new Coin(1000f, 80f), new Coin(1030f, 80f), new Coin(1060f, 80f),
                new Coin(1640f, 205f), new Coin(1670f, 205f), new Coin(1700f, 205f),
                new Coin(2100f, 80f), new Coin(2140f, 80f)
        };

        Enemy[] enemies = new Enemy[] {
                new Enemy(260f, 48f, Enemy.Type.GOOMBA, 80f),
                new Enemy(620f, 48f, Enemy.Type.KOOPA, 100f),
                new Enemy(1020f, 48f, Enemy.Type.KOOPA, 120f),
                new Enemy(1550f, 48f, Enemy.Type.GOOMBA, 70f),
                new Enemy(2100f, 48f, Enemy.Type.KOOPA, 90f)
        };

        PowerUp[] powerUps = new PowerUp[] {
                new PowerUp(640f, 225f, PowerUp.Type.SUPER_MUSHROOM),
                new PowerUp(1660f, 205f, PowerUp.Type.STAR_INVINCIBILITY)
        };

        GoalFlag flag = new GoalFlag(2450f, 48f);

        return new Level(2, "World 1-2: Underground Cavern", 2560f, 480f, caveDarkSlate,
                platforms, coins, enemies, powerUps, flag, 40f, 100f, 1200f);
    }

    private static Level createLevel3() {
        Color castleCrimson = new Color(0.25f, 0.08f, 0.08f, 1f);

        Platform[] platforms = new Platform[] {
                // Castle bridge floor with lava pits
                new Platform(0f, 0f, 500f, 48f, Platform.Type.BRICK),
                new Platform(590f, 0f, 400f, 48f, Platform.Type.BRICK), // 90px lava pit
                new Platform(1070f, 0f, 450f, 48f, Platform.Type.BRICK), // 80px lava pit
                new Platform(1600f, 0f, 1100f, 48f, Platform.Type.BRICK), // Boss arena floor

                // Stepping stone platforms
                new Platform(240f, 110f, 90f, 24f, Platform.Type.BRICK),
                new Platform(380f, 150f, 80f, 24f, Platform.Type.BRICK),
                new Platform(520f, 100f, 70f, 22f, Platform.Type.FLOATING_GRASS),
                new Platform(700f, 130f, 120f, 24f, Platform.Type.BRICK),
                new Platform(900f, 170f, 90f, 24f, Platform.Type.BRICK),
                new Platform(1020f, 110f, 60f, 22f, Platform.Type.FLOATING_GRASS),

                // Boss Arena elevated ledges
                new Platform(1750f, 110f, 120f, 24f, Platform.Type.BRICK),
                new Platform(2000f, 160f, 140f, 24f, Platform.Type.BRICK),
                new Platform(2250f, 110f, 120f, 24f, Platform.Type.BRICK)
        };

        Coin[] coins = new Coin[] {
                new Coin(250f, 175f), new Coin(280f, 175f),
                new Coin(720f, 215f), new Coin(760f, 215f),
                new Coin(1120f, 80f), new Coin(1160f, 80f),
                new Coin(1780f, 185f), new Coin(2020f, 245f), new Coin(2270f, 185f)
        };

        Enemy[] enemies = new Enemy[] {
                new Enemy(320f, 48f, Enemy.Type.KOOPA, 80f),
                new Enemy(780f, 48f, Enemy.Type.KOOPA, 100f),
                new Enemy(1250f, 48f, Enemy.Type.KOOPA, 90f),
                // Final Boss Bowser guarding the finish!
                new Enemy(2150f, 48f, Enemy.Type.BOSS_BOWSER, 180f)
        };

        PowerUp[] powerUps = new PowerUp[] {
                new PowerUp(400f, 245f, PowerUp.Type.SUPER_MUSHROOM),
                new PowerUp(1450f, 80f, PowerUp.Type.STAR_INVINCIBILITY)
        };

        GoalFlag flag = new GoalFlag(2600f, 48f);

        return new Level(3, "World 1-3: Bowser's Fortress", 2750f, 480f, castleCrimson,
                platforms, coins, enemies, powerUps, flag, 40f, 100f, 1350f);
    }
}
