package com.andronikus.game.model.client;

import lombok.Data;
import java.io.Serializable;

/**
 * Request to execute an input on the server from a client.
 *
 * @author Andronikus
 */
@Data
public class InputRequest implements Serializable {
    private String inputCode;
    private Long inputId = null;
    private boolean ackRequired = false;

    // Parameters for inputs
    private Serializable parameter0 = null;
}
