package com.andronikus.game.model.client;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Client request to the server.
 *
 * @author Andronikus
 */
@Data
public class ClientRequest implements Serializable {
    private long sequenceNumber = 0;
    private List<String> inputCodes;
    private String sessionToken;
}
