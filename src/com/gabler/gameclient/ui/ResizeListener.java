package com.gabler.gameclient.ui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Catches when the JPanel resized.
 *
 * @author Andy Gabler
 * @since 2018/05/12
 */
public class ResizeListener implements ComponentListener {

    private final GameWindow window;

    /**
     * Instantiate resize listener.
     *
     * @param aWindow Window to listen to
     */
    public ResizeListener(GameWindow aWindow) {
        window = aWindow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentHidden(ComponentEvent event) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentMoved(ComponentEvent event) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentResized(ComponentEvent event) {
        window.setWidth((int) event.getComponent().getSize().getWidth());
        window.setHeight((int) event.getComponent().getSize().getHeight());

        window.repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentShown(ComponentEvent event) {}
}
