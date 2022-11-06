package com.andronikus.gameclient.engine;

import com.andronikus.gameclient.ui.input.ServerInput;

import java.util.List;

/**
 * Supplier for client input to the engine.
 *
 * @author Andronikus
 */
public interface IClientInputManager {

    /**
     * Get the inputs the user has put in and then clear get the inputs ready for next call.
     *
     * @return The inputs
     */
    List<ServerInput> getAndClearInputs();

    /**
     * Get the command that will be sent to the server.
     *
     * @return The command
     */
    String getCommand();

    /**
     * Get the next inputs to be cleared.
     *
     * @return The IDs of the inputs that are to be purged from server acknowledgements
     */
    List<Long> getInputPurgeRequests();
}
