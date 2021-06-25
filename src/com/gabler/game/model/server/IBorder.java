package com.gabler.game.model.server;

/**
 * Border interface.
 *
 * @author Andy Gabler
 */
public interface IBorder {

    /**
     * Adjust player velocities so that they do not cross the border.
     *
     * @param player The player to adjust
     */
    void adjustSpeedToNotCrossBorder(Player player);
}
