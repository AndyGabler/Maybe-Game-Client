package com.andronikus.game.model.server;

import lombok.Data;

/**
 * Asteroid, or rather, giant rock in space.
 *
 * @author Andronikus
 */
@Data
public class Asteroid implements IMoveable {

    private long id;
    private long x;
    private long y;
    private long xVelocity;
    private long yVelocity;
    private double angle;
    private double angularVelocity;
    private int size;
    private int durability;
    private int crackingTicks;

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
        return size == 0 ? 64 : 96;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return size == 0 ? 64 : 192;
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
    public void setXTickDelta(long xDelta) {
        this.xVelocity = xDelta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setYTickDelta(long yDelta) {
        this.yVelocity = yDelta;
    }

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
