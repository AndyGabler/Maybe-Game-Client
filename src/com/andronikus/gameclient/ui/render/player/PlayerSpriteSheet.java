package com.andronikus.gameclient.ui.render.player;

import com.andronikus.gameclient.ui.render.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet for a player.
 *
 * @author Andronikus
 */
public class PlayerSpriteSheet extends SpriteSheet {

    public static final int TILE_SIZE = 64;

    public PlayerSpriteSheet() {
        super("player/player-spritesheet.png", TILE_SIZE);
    }

    /**
     * Get sprite for the idle state of the player.
     *
     * @param animationState Animation state, ranging from 0-3.
     * @return Sprite on the grid for the player
     */
    public BufferedImage getIdleSprite(int animationState) {
        return getTile(0, animationState);
    }

    /**
     * Get sprite for the thrusting state of the player.
     *
     * @param animationState Animation state, ranging from 0-4
     * @return Sprite on the grid for the player
     */
    public BufferedImage getThrustingSprite(int animationState) {
        return getTile(1, animationState);
    }

    /**
     * Get sprite for the boosting state of the player.
     *
     * @param animationState Animation state, ranging from 0-4
     * @return Sprite on the grid for the player
     */
    public BufferedImage getBoostingSprite(int animationState) {
        return getTile(2, animationState);
    }
}
