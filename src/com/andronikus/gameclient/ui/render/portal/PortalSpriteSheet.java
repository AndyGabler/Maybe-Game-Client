package com.andronikus.gameclient.ui.render.portal;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet for a portal.
 *
 * @author Andronikus
 */
public class PortalSpriteSheet extends SpriteSheet {

    private static final int TILE_SIZE = 64;

    public PortalSpriteSheet() {
        super("portal/portal-sprite-sheet.png", TILE_SIZE);
    }

    /**
     * Get sprite for the idle state of the portal.
     *
     * @param animationState Animation state, ranging from 0-6
     * @return Sprite on the grid for the portal
     */
    public BufferedImage getIdleSprite(int animationState) {
        return getTile(0, animationState);
    }

    /**
     * Get sprite for the warping state of the portal.
     *
     * @param animationState Animation state, ranging from 0-6
     * @return Sprite on the grid for the portal
     */
    public BufferedImage getWarpingSprite(int animationState) {
        return getTile(1, animationState);
    }
}
