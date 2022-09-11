package com.andronikus.game.model.server.debug;

import lombok.Data;

import java.io.Serializable;

/**
 * Collision flag indicating a player collided with something.
 *
 * @author Andronikus
 */
@Data
public class PlayerCollisionFlag implements Serializable {
    private String sessionId;
    private String collisionType;
    private long collisionId;
    private long gameStateVersion;
}
