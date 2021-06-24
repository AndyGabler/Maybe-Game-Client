package com.gabler.gameclient.engine;

import com.gabler.game.model.server.GameState;

/**
 * Renderer for a {@link GameState}.
 *
 * @author Andy Gabler
 */
public interface IGameStateRenderer {

    /**
     * Set the game state to render.
     *
     * @param toRender The game state to render
     */
    void setGameStateToRender(GameState toRender);

    /**
     * Set the session associated to the player whose perspective is being followed.
     *
     * @param sessionId The session ID
     */
    void setSessionId(String sessionId);

    /**
     * Render a game-state in a user-readable way.
     */
    void render();
}
