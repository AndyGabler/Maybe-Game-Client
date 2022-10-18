package com.andronikus.game.model.client;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client request to the server.
 *
 * @author Andronikus
 */
@Data
public class ClientRequest implements Serializable {
    private long sequenceNumber = 0;
    private String inputCode0 = null;
    private String inputCode1 = null;
    private String inputCode2 = null;
    private String inputCode3 = null;
    private String inputCode4 = null;
    private String sessionToken;
    private List<ClientCommand> clientCommands = new ArrayList<>();
    private List<ClientCommand> commandsToRemove = new ArrayList<>();
}
