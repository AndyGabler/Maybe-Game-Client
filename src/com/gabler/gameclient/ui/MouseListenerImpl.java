package com.gabler.gameclient.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Mouse adapter for a game window.
 *
 * @author Andy Gabler
 * @since 2018/04/20
 */
public class MouseListenerImpl implements MouseMotionListener, MouseListener {

    private final GameWindow window;

    /**
     * Instantiate a mouse adapter for a game window.
     *
     * @param aWindow Game window that might be interested in mouse events
     */
    public MouseListenerImpl(GameWindow aWindow) {
        window = aWindow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
