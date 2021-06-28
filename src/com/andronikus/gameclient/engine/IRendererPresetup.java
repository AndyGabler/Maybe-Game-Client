package com.andronikus.gameclient.engine;

/**
 * Interface for the handling of the setup of a renderer.
 *
 * @author Andronikus
 */
public interface IRendererPresetup {

    /**
     * Setup to perform before rendering of a game state is ready.
     */
    void setupBeforeRender();
}
