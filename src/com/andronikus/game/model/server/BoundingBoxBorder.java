package com.andronikus.game.model.server;

import lombok.Data;

import java.io.Serializable;

/**
 * Game border that is a bounding box and just defines the max X and Y a player may go.
 *
 * @author Andronikus
 */
@Data
public class BoundingBoxBorder implements Serializable, IBorder {
    private long maxX;
    private long maxY;

    /**
     * {@inheritDoc}
     */
    @Override
    public void adjustSpeedToNotCrossBorder(Player player) {
        final long distanceFromMaxX = maxX - player.getX();

        // If player will cross border within next tick....
        if (distanceFromMaxX < player.getXVelocity()) {
            player.setXVelocity(distanceFromMaxX); // ... Shorten velocity to only meet the border
        // If player's negative velocity will put them below 0 X ...
        } else if (player.getX() < Math.abs(player.getXVelocity()) && player.getXVelocity() < 0) {
            // ... their velocity simply becomes their X inverted
            player.setXVelocity(player.getX() * -1);
        }

        final long distanceFromMaxY = maxY - player.getY();

        // If player will cross border within next tick....
        if (distanceFromMaxY < player.getYVelocity()) {
            player.setYVelocity(distanceFromMaxY); // ... Shorten velocity to only meet the border
            // If player's negative velocity will put them below 0 Y ...
        } else if (player.getY() < Math.abs(player.getYVelocity()) && player.getYVelocity() < 0) {
            // ... their velocity simply becomes their Y inverted
            player.setYVelocity(player.getY() * -1);
        }
    }
}
