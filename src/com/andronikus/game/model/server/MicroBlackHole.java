package com.andronikus.game.model.server;

import lombok.Data;

/**
 * A little black hole that sucks players in for a few seconds.
 *
 * @author Andronikus
 */
@Data
public class MicroBlackHole implements IMoveable {

    private long id;
    private long x;
    private long y;
    private double angle;
    private double angularVelocity;

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
        return 128;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return 128;
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
    public long getMoveableId() {
        return id;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String moveableTag() {
        return "BLACKHOLE";
    }
}
