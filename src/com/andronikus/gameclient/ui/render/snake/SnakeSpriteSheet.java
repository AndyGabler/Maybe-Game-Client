package com.andronikus.gameclient.ui.render.snake;

import com.andronikus.gameclient.ui.render.SpriteSheet;

import java.awt.image.BufferedImage;

/**
 * Sprite sheet for a snake.
 *
 * @author Andronikus
 */
public class SnakeSpriteSheet extends SpriteSheet {

    public SnakeSpriteSheet() {
        super("snakes/snakes.png", 16, 64);
    }

    /**
     * Get sprite for the idle state of the snake.
     *
     * @param animationState State of the animation
     * @return Sprite on the grid for the snake
     */
    public BufferedImage getIdleSprite(int animationState) {
        return getTile(0, animationState);
    }

    /**
     * Get sprite for the chasing state of the snake.
     *
     * @param animationState State of the animation
     * @return Sprite on the grid for the snake
     */
    public BufferedImage getChasingSprite(int animationState) {
        return getTile(1, animationState);
    }

    /**
     * Get sprite for the dying state of the snake.
     *
     * @param animationState State of the animation
     * @return Sprite on the grid for the snake
     */
    public BufferedImage getDyingSprite(int animationState) {
        return getTile(2, animationState);
    }
}
