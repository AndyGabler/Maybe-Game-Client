package com.andronikus.game.model.server;

import lombok.Data;

/**
 * A laser projectile.
 *
 * @author Andronikus
 */
@Data
public class Laser implements IMoveable {

    private long x;
    private long y;
    private long xVelocity;
    private long yVelocity;
    private String loyalty;
    private long id;
    private boolean active;
    private double angle;

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
        return 48;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxHeight() {
        return 32;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTilt() {
        return angle;
    }

    @Override
    public void setMoveableId(long id) {
        this.id = id;
    }

    @Override
    public void setXPosition(long x) {
        this.x = x;
    }

    @Override
    public void setYPosition(long y) {
        this.y = y;
    }

    @Override
    public void setXTickDelta(long xDelta) {
        this.xVelocity = xDelta;
    }

    @Override
    public void setYTickDelta(long yDelta) {
        this.yVelocity = yDelta;
    }

    @Override
    public void setDirection(double angle) {
        this.angle = angle;
    }

    @Override
    public void setDirectionTickDelta(double angle) {
        // Do nothing
    }
}
