package com.andronikus.gameclient.ui.render.laser;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet for a player.
 *
 * @author Andronikus
 */
public class LaserSpriteSheet extends SpriteSheet {

    private static final int TILE_WIDTH = 48;
    private static final int TILE_HEIGHT = 32;

    public LaserSpriteSheet() {
        super("projectile/laser-spritesheet.png", TILE_WIDTH, TILE_HEIGHT);
    }

    /**
     * Get sprite for the traveling state of the laser.
     *
     * @param animationState Animation state, ranging from 0-3.
     * @return Sprite on the grid for the laser
     */
    public BufferedImage getTravelingSprite(int animationState) {
        return getTile(0, animationState);
    }

    /**
     * Get sprite for the hit state of the laser.
     *
     * @param animationState Animation state, ranging from 0-3.
     * @return Sprite on the grid for the laser
     */
    public BufferedImage getHitSprite(int animationState) {
        return getTile(1, animationState);
    }
}
