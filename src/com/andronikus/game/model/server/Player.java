package com.andronikus.game.model.server;

import lombok.Data;

/**
 * A player in the game.
 *
 * @author Andronikus
 */
@Data
public class Player implements ICollideable {

    private String sessionId;

    private long x;
    private long y;

    private long XVelocity = 0;
    private long yVelocity = 0;

    private long externalXAcceleration;
    private long externalYAcceleration;

    private long speed = 0;
    private long acceleration = 0;

    private boolean boosting = false;
    private int boostingCharge;
    private int boostingRecharge;

    private double angle;
    private double rotationalVelocity = 0;

    private int health;
    private boolean dead;

    private int shieldCount;
    private int shieldRecharge;
    private boolean shieldLostThisTick;

    private int laserCharges;
    private int laserRecharge;

    private PlayerColor color;

    private boolean thrusting;

    private int venom;

    private Long collidedPortalId = null;
    private boolean performedWarp = false;

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
}
