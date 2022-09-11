package com.andronikus.game.model.client;

import lombok.Data;

import java.io.Serializable;

/**
 * Client command request object.
 *
 * @author Andronikus
 */
@Data
public class ClientCommand implements Serializable {
    private String code;
    private long commandNumber;
}
