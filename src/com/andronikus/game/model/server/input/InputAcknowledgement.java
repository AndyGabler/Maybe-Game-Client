package com.andronikus.game.model.server.input;

import lombok.Data;

import java.io.Serializable;

@Data
public class InputAcknowledgement implements Serializable {
    private String sessionId;
    private long inputId;
    private long createdGameStateVersion;
}
