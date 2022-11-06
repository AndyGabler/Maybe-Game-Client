package com.andronikus.game.model.server.input;

import lombok.Data;

import java.io.Serializable;

/**
 * Acknowledgement from the server that an input has been processed.
 *
 * @author Andronikus
 */
@Data
public class InputAcknowledgement implements Serializable {
    private String sessionId;
    private long inputId;
    private long createdGameStateVersion;
}
