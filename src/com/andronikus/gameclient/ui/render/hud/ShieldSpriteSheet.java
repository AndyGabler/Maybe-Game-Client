package com.andronikus.gameclient.ui.render.hud;

import com.andronikus.gameclient.ui.render.SpriteSheet;
import java.awt.image.BufferedImage;

/**
 * Sprite sheet for the shield for a player.
 *
 * @author Andronikus
 */
public class ShieldSpriteSheet extends SpriteSheet {

    private static final int TILE_WIDTH = 41;
    private static final int TILE_HEIGHT = 39;

    public ShieldSpriteSheet() {
        super("hud/shieldSpriteSheet.png", TILE_WIDTH, TILE_HEIGHT);
    }

    /**
     * Get sprite for the status icon.
     *
     * @param chargeRate Charge state ranging from 0-12
     * @return Sprite on the grid for the shield
     */
    public BufferedImage getStatusIcon(int chargeRate) {
        return getTile(0, chargeRate);
    }
}
