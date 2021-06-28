package com.andronikus.gameclient.ui.render;

import com.andronikus.gameclient.ui.ImagesUtil;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet that uses a big image and divides them into a set of smaller images called sprites.
 *
 * @author Andronikus
 */
public class SpriteSheet {

    private final BufferedImage spriteSheet;
    private final int tileSize;

    /**
     * Instantiate a sprite sheet.
     *
     * @param spriteSheetPath The path to the sprite sheet
     * @param tileSize The size of the tile
     */
    public SpriteSheet(String spriteSheetPath, int tileSize) {
        this(ImagesUtil.getImage(spriteSheetPath), tileSize);
    }

    /**
     * Instantiate a sprite sheet.
     *
     * @param spriteSheet The sprite sheet image
     * @param tileSize The size of the tile
     */
    public SpriteSheet(BufferedImage spriteSheet, int tileSize) {
        this.spriteSheet = spriteSheet;
        this.tileSize = tileSize;
    }

    /**
     * Get the tile.
     *
     * @param x The X location
     * @param y The Y location
     * @return The sprite
     */
    protected BufferedImage getTile(int x, int y) {
        return spriteSheet.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);
    }
}
