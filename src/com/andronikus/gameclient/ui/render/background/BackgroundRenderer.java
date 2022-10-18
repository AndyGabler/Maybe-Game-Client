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

    public void render(Graphics graphics, Player mainPlayer, BoundingBoxBorder border, GameWindow observer) {

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
