package com.gabler.gameclient.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Keyboard listener for a game window.
 *
 * @author Andy Gabler
 */
public class KeyBoardListener implements KeyListener {

    private final GameWindow window;

    /**
     * Instantiate a keyboard listener for a game window.
     *
     * @param aWindow Game window that might be interested in keyboard events
     */
    public KeyBoardListener(GameWindow aWindow) {
        window = aWindow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent event) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent event) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent event) {

    }
}
