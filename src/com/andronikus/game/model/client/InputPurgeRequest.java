package com.andronikus.game.model.client;

import lombok.Data;

import java.io.Serializable;

/**
 * Request to purge an input from the server's acknowledgements.
 *
 * @author Andronikus
 */
@Data
public class InputPurgeRequest implements Serializable {
    private long id;
}
