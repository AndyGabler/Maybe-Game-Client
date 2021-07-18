package com.andronikus.game.model.server;

import lombok.Data;

/**
 * Portal that takes players from one side of the map to other.
 *
 * @author Andronikus
 */
@Data
public class Portal implements IMoveable {

    private long id;
    private long x;
    private long y;
    private double angle;
    private double angularVelocity;
    private Integer ticksSinceCollision = null;
    private Integer ticksSinceMovement = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBoxX() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBoxY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxWidth() {
        return 64;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return 64;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTilt() {
        return angle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMoveableId(long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXPosition(long x) {
        this.x = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYPosition(long y) {
        this.y = y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void setXTickDelta(long xDelta) {}

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void setYTickDelta(long yDelta) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDirection(double angle) {
        this.angle = angle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDirectionTickDelta(double angle) {
        this.angularVelocity = angle;
    }
}
