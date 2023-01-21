package com.andronikus.gameclient.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Mouse adapter for a game window.
 *
 * @author Andronikus
 * @since 2018/04/20
 */
public class MouseListenerImpl implements MouseMotionListener, MouseListener {

    // TODO: Any kind mouse client needs to give the server some kind of indication of what it clicked which the server
    // TODO: will need to verify is within FOV. Let's not implement mouse input mapping... for now

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
        window.setLaserShotCoordinates(mouseEvent.getX(), mouseEvent.getY());
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
        window.setLaserShotCoordinates(mouseEvent.getX(), mouseEvent.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        window.setLaserShotCoordinates(null, null);
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
