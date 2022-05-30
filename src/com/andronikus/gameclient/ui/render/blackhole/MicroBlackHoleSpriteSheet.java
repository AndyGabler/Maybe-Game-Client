package com.andronikus.gameclient.ui.render.blackhole;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet for a black hole.
 *
 * @author Andronikus
 */
public class MicroBlackHoleSpriteSheet extends SpriteSheet {

    private static final int BLACK_HOLE_TILE_SIZE = 128;

    public MicroBlackHoleSpriteSheet() {
        super("blackhole/blackhole-sprite-sheet.png", BLACK_HOLE_TILE_SIZE);
    }

    /**
     * Get sprite for a black hole.
     *
     * @param animationState Animation state, ranging from 0-6
     * @return The sprite
     */
    public BufferedImage getSprite(int animationState) {
        return getTile(0, animationState);
    }
}
