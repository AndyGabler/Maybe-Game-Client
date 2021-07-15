package com.andronikus.game.model.server;

/**
 * Object that is moveable by the engine.
 *
 * @author Andronikus
 */
public interface IMoveable extends ICollideable {

    /**
     * Set ID of the moveable.
     *
     * @param id The moveable
     */
    void setMoveableId(long id);

    /**
     * Set X Position of the moveable.
     * @param x The x
     */
    void setXPosition(long x);

    /**
     * Set Y Position of the moveable.
     *
     * @param y The y
      */
    void setYPosition(long y);

    /**
     * Set how much X changes per tick (velocity)
     *
     * @param xDelta The x velocity
     */
    void setXTickDelta(long xDelta);

    /**
     * Set how much Y changes per tick (velocity)
     *
     * @param yDelta The y velocity
     */
    void setYTickDelta(long yDelta);

    /**
     * Set the angle at which movement is occuring.
     *
     * @param angle The angle at which movement is occuring
     */
    void setDirection(double angle);

    /**
     * Set the rotational velocity.
     *
     * @param angle The angle at the angle changes each tick
     */
    void setDirectionTickDelta(double angle);
}
