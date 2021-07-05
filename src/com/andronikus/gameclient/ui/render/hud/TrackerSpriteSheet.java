package com.andronikus.gameclient.ui.render.hud;

import com.andronikus.game.model.server.PlayerColor;
import com.andronikus.gameclient.ui.render.SpriteSheet;
import java.awt.image.BufferedImage;

/**
 * Sprite sheet for the tracker for a player.
 *
 * @author Andronikus
 */
public class TrackerSpriteSheet extends SpriteSheet {

    private static final int TILE_SIZE = 32;

    public TrackerSpriteSheet() {
        super("hud/playermarkers.png", TILE_SIZE);
    }

    /**
     * Get sprite for the tracker.
     *
     * @param color Color of the player tracker
     * @return Sprite on the grid for the tracker
     */
    public BufferedImage getTrackerSpriteForColor(PlayerColor color) {
        return getTile(0, color.getId());
    }
}
