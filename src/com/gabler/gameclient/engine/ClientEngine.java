package com.gabler.gameclient.engine;

import com.gabler.game.model.server.GameState;

import java.util.function.Supplier;

/**
 * Engine for the client. Hook for all components that create some kind of event (IE server messages or ticks).
 *
 * @author Andy Gabler
 */
public class ClientEngine {

    // TODO ticks and sequence number init

    private final Supplier<String> sessionIdResolver;
    private final IGameStateRenderer renderer;

    /**
     * Instantiate engine for the client.
     *
     * @param aSessionIdResolver Supplier for session IDs
     * @param aRenderer The render that renders game states
     */
    public ClientEngine(Supplier<String> aSessionIdResolver, IGameStateRenderer aRenderer) {
        sessionIdResolver = aSessionIdResolver;
        renderer = aRenderer;
    }

    /**
     * Take a game state.
     *
     * @param gameState The game state
     */
    public synchronized void takeGameState(GameState gameState) {
        renderer.render(gameState);
    }

    /**
     * Pause the engine.
     */
    public void pauseEngine() {

    }

    /**
     * Resume the engine.
     */
    public void resumeEngine() {

    }

    /**
     * Kill the engine.
     */
    public void kill() {

    }
}
