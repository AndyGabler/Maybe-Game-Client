package com.andronikus.game.model.server.debug;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Debug settings for when the server is in debug mode.
 *
 * @author Andronikus
 */
@Data
public class ServerDebugSettings implements Serializable {
    private ArrayList<CommandAcknowledgement> commandAcknowledgements;
    private ArrayList<PlayerCollisionFlag> playerCollisionFlags;
}
