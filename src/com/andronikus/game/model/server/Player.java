package com.andronikus.game.model.server;

import lombok.Data;

/**
 * A player in the game.
 *
 * @author Andronikus
 */
@Data
public class Player implements IMoveable {

    private String sessionId;

    private long x;
    private long y;

    private long xVelocity = 0;
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
    private long laserShotTime;
    private Double laserShotAngle;

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

    @Override
    public void setMoveableId(long id) {
        // can't set player ID
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMoveableId() {
        return 0L;
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
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String moveableTag() {
        return "PLAYER";
    }
}
