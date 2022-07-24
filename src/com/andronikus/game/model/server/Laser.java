package com.andronikus.game.model.server;

import lombok.Data;

/**
 * A laser projectile.
 *
 * @author Andronikus
 */
@Data
public class Laser implements ICollideable {

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
}
