package com.andronikus.game.model.server;

import lombok.Data;

import java.io.Serializable;

/**
 * Acknowledge from the server of a command to a client.
 *
 * @author Andronikus
 */
@Data
public class CommandAcknowledgement implements Serializable {
    private String sessionId;
    private long commandId;
}
