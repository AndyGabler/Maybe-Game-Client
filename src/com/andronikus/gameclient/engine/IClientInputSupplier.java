package com.andronikus.gameclient.engine;

import java.util.List;

/**
 * Supplier for client input to the engine.
 *
 * @author Andronikus
 */
public interface IClientInputSupplier {

    /**
     * Get the inputs the user has put in and then clear get the inputs ready for next call.
     *
     * @return The inputs
     */
    List<String> getAndClearInputs();
}
