package com.andronikus.game.model.server;

import java.io.Serializable;

/**
 * Collideable that can collide with other objects.
 *
 * @author Andronikus
 */
public interface ICollideable extends Serializable {

    /**
     * Get hitbox X.
     *
     * @return The X
     */
    long getBoxX();

    /**
     * Get hitbox Y.
     *
     * @return The Y
     */
    long getBoxY();

    /**
     * Get hitbox width.
     *
     * @return The width
     */
    int getBoxWidth();

    /**
     * Get hitbox height.
     *
     * @return The height
     */
    int getBoxHeight();

    /**
     * Angle at which the hitbox is tilted.
     *
     * @return The tilt angle
     */
    double getTilt();
}
