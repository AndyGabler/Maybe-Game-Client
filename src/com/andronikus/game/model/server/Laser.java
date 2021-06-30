package com.andronikus.game.model.server;

import lombok.Data;

import java.io.Serializable;

/**
 * A laser projectile.
 *
 * @author Andronikus
 */
@Data
public class Laser implements Serializable {

    private long x;
    private long y;
    private long xVelocity;
    private long yVelocity;
    private String loyalty;
    private long id;
    private double angle;
}
