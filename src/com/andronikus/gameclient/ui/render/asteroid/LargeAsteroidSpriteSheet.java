package com.andronikus.gameclient.ui.render.asteroid;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet for a large asteroid.
 *
 * @author Andronikus
 */
public class LargeAsteroidSpriteSheet extends SpriteSheet {

    public LargeAsteroidSpriteSheet() {
        super("asteroid/asteroid-size-1.png", 96, 192);
    }

    /**
     * Get sprite for the normal state of the asteroid.
     *
     * @return Sprite on the grid for the asteroid
     */
    public BufferedImage getAsteroidSprite() {
        return getTile(0, 0);
    }

    /**
     * Get sprite for the cracking state of the asteroid.
     *
     * @param crackingState Progression the asteroid is on in its cracking
     * @return Sprite on the grid for the asteroid
     */
    public BufferedImage getCrackingSprite(int crackingState) {
        return getTile(1, crackingState);
    }
}
