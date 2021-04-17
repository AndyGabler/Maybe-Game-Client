package com.gabler.gameclient.engine;

/**
 * Interface for the handling of the setup of a renderer.
 *
 * @author Andy Gabler
 */
public interface IRendererPresetup {

    /**
     * Setup to perform before rendering of a game state is ready.
     */
    void setupBeforeRender();
}
