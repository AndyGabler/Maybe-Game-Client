package com.andronikus.game.model.server;

/**
 * Border interface.
 *
 * @author Andronikus
 */
public interface IBorder {

    /**
     * Adjust player velocities so that they do not cross the border.
     *
     * @param player The player to adjust
     */
    void adjustSpeedToNotCrossBorder(Player player);
}
