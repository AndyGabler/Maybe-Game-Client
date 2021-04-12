package com.gabler.game.model.server;

import lombok.Data;

import java.io.Serializable;

/**
 * State of the game.
 *
 * @author Andy Gabler
 */
@Data
public class GameState implements Serializable {
    private long version = -1;
}
