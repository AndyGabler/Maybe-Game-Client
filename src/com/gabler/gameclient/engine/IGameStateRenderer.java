package com.gabler.gameclient.engine;

import com.gabler.game.model.server.GameState;

/**
 * Renderer for a {@link GameState}.
 *
 * @author Andy Gabler
 */
public interface IGameStateRenderer {

    /**
     * Render a game-state in a user-readable way.
     *
     * @param toRender The game state to render
     */
    void render(GameState toRender);
}
