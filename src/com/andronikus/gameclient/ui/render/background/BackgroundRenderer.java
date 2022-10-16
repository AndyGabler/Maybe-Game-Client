package com.andronikus.gameclient.ui.render.background;

import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameclient.ui.AspectRatio;
import com.andronikus.gameclient.ui.GameWindow;
import com.andronikus.gameclient.ui.ImagesUtil;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BackgroundRenderer {



    private ArrayList<ArrayList<BufferedImage>> tiles = null;
    private final BufferedImage masterImage;
    private final int totalWidth;
    private final int totalHeight;
    private final int chunkWidth;
    private final int chunkHeight;

    public BackgroundRenderer(String imagePath, int chunkWidth, int chunkHeight) {
        masterImage = ImagesUtil.getImage(imagePath);
        totalWidth = masterImage.getWidth();
        totalHeight = masterImage.getHeight();
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
    }

    /**
     * Pre-calculate the tiles on the image.
     */
    public void prechunkImage() {
        chunkImage();
        for (int tileX = 0; tileX < tiles.size(); tileX++) {
            for (int tileY = 0; tileY < tiles.get(tileX).size(); tileY++) {
                calculateOrGetChunk(tileX, tileY);
            }
        }
    }

    /**
     * Generate the spots for the tiles, without necessarily calculating what goes in them.
     */
    private void chunkImage() {
        int chunkXCount = totalWidth / chunkWidth;
        if (totalWidth % chunkWidth != 0) {
            chunkXCount++;
        }
        int chunkYCount = totalWidth / chunkHeight;
        if (totalHeight % chunkHeight != 0) {
            chunkYCount++;
        }

        tiles = new ArrayList<>(chunkXCount);
        for (int chunkX = 0; chunkX < chunkXCount; chunkX++) {
            final ArrayList<BufferedImage> tileColumn = new ArrayList<>(chunkYCount);
            for (int chunkY = 0; chunkY < chunkYCount; chunkY++) {
                tileColumn.add(null);
            }
            tiles.add(tileColumn);
        }
    }

    /**
     * Calculate (or get the cached result) of what goes in a chunk.
     *
     * @param chunkX The Chunk X
     * @param chunkY The Chunk Y
     * @return The image in the chunk, whether newly split off or cached
     */
    private BufferedImage calculateOrGetChunk(int chunkX, int chunkY) {
        BufferedImage result = tiles.get(chunkX).get(chunkY);
        if (result == null) {
            result = masterImage.getSubimage(chunkX * chunkWidth, chunkY * chunkHeight, chunkWidth, chunkHeight);
            tiles.get(chunkX).set(chunkY, result);
        }
        return result;
    }

    public void render(Graphics graphics, Player mainPlayer, BoundingBoxBorder border, long width, long height, GameWindow observer) {
        if (tiles == null) {
            // chunkImage();
        }

        // To keep the edge the same for all players, we keep the same amount of pixels reserved, however, this determines the scaling of the background
        final double widthScale = observer.getAspectRatio().getWidthScale();
        final double heightScale = observer.getAspectRatio().getHeightScale();

        // Dimensions scale with how the outside is scaled
        final double backgroundWidth  = ((double) AspectRatio.OVERDRAW_WIDTH * 2.0 + (double)border.getMaxX()) * widthScale;
        final double backgroundHeight = ((double) AspectRatio.OVERDRAW_HEIGHT * 2.0 + (double)border.getMaxY()) * heightScale;

        // Box traversal ratios
        final double boxXTraversalRatio = (double) mainPlayer.getX() / (double) border.getMaxX();
        final double boxYTraversalRatio = (double) (border.getMaxY() - mainPlayer.getY()) / (double) border.getMaxY();

        // Background coordinates
        final int backGroundX = (int) (-boxXTraversalRatio * (double)border.getMaxX() * widthScale);
        final int backGroundY = (int) (-boxYTraversalRatio * (double)border.getMaxY() * heightScale);

        graphics.drawImage(masterImage, backGroundX, backGroundY, (int)backgroundWidth, (int) backgroundHeight, observer);
    }
}
