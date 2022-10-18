package com.andronikus.gameclient.ui.render.background;

import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameclient.ui.RenderRatio;
import com.andronikus.gameclient.ui.GameWindow;
import com.andronikus.gameclient.ui.ImagesUtil;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Renderer for the background.
 *
 * @author Andronikus
 */
public class BackgroundRenderer {

    private final BufferedImage backgroundImage;
    public BackgroundRenderer(String imagePath) {
        backgroundImage = ImagesUtil.getImage(imagePath);
    }

    /**
     * Render the background.
     *
     * @param graphics The graphics to put the background
     * @param mainPlayer The player who is the central object
     * @param border The border
     * @param observer Where the background is being drawn on
     */
    public void render(Graphics graphics, Player mainPlayer, BoundingBoxBorder border, GameWindow observer) {

        // To keep the edge the same for all players, we keep the same amount of pixels reserved, however, this determines the scaling of the background
        final double widthScale = observer.getRenderRatio().getWidthScale();
        final double heightScale = observer.getRenderRatio().getHeightScale();

        // Dimensions scale with how the outside is scaled
        final double backgroundWidth  = ((double) RenderRatio.OVERDRAW_WIDTH * 2.0 + (double)border.getMaxX()) * widthScale;
        final double backgroundHeight = ((double) RenderRatio.OVERDRAW_HEIGHT * 2.0 + (double)border.getMaxY()) * heightScale;

        // Box traversal ratios
        final double boxXTraversalRatio = (double) mainPlayer.getX() / (double) border.getMaxX();
        final double boxYTraversalRatio = (double) (border.getMaxY() - mainPlayer.getY()) / (double) border.getMaxY();

        // Background coordinates
        final int backGroundX = (int) (-boxXTraversalRatio * (double)border.getMaxX() * widthScale);
        final int backGroundY = (int) (-boxYTraversalRatio * (double)border.getMaxY() * heightScale);

        graphics.drawImage(backgroundImage, backGroundX, backGroundY, (int)backgroundWidth, (int) backgroundHeight, observer);
    }
}
