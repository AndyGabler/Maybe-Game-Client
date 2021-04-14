package com.gabler.gameclient.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Static, and final, images for each entity
 *
 * @author Andy Gabler
 * @since 2018/01/13
 */
public class ImagesUtil {

    /**
     * Image at a location within the image directory
     *
     * @param filePath The path to the images
     * @return The image at that location in the file system
     */
    public static BufferedImage getImage(String filePath) {
        final URL imageUrl = ImagesUtil.class.getClassLoader().getResource(filePath);
        final String fullPath = imageUrl.getFile();
        return getUnscopedImage(fullPath);
    }

    /**
     * Image at a location on the file system
     *
     * @param filePath The path to the images
     * @return The image at that location in the file system
     */
    public static BufferedImage getUnscopedImage(String filePath) {

        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(filePath));
        } catch (Exception problem) {}

        return image;
    }
}
