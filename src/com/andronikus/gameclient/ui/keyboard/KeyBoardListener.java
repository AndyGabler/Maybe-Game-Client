package com.andronikus.gameclient.ui.keyboard;

import com.andronikus.gameclient.ui.GameWindow;
import com.andronikus.gameclient.ui.input.IUserInput;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Keyboard listener for a game window.
 *
 * @author Andronikus
 */
public class KeyBoardListener implements KeyListener {

    private final IKeyBoardInputMapper inputMapper;
    private final GameWindow window;

    /**
     * Instantiate a keyboard listener for a game window.
     *
     * @param aWindow Game window that might be interested in keyboard events
     */
    public KeyBoardListener(GameWindow aWindow) {
        this(new StaticKeyboardInputMapper(), aWindow);
    }

    /**
     * Instantiate a keyboard listener for a game window.
     *
     * @param anInputMapper Responsible for mapping keyboard inputs to input codes a server takes
     * @param aWindow Game window that might be interested in keyboard events
     */
    public KeyBoardListener(IKeyBoardInputMapper anInputMapper, GameWindow aWindow) {
        inputMapper = anInputMapper;
        window = aWindow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent event) {
        handleKeyPress(KeyBoardPressType.TYPED, event.getKeyLocation(), event.getKeyCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent event) {
        handleKeyPress(KeyBoardPressType.PRESSED, event.getKeyLocation(), event.getKeyCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent event) {
        handleKeyPress(KeyBoardPressType.RELEASED, event.getKeyLocation(), event.getKeyCode());
    }

    /**
     * Handle a key event.
     *
     * @param eventType The event
     * @param locationCode Location code of the key-board press
     * @param keyCode The key code
     */
    private void handleKeyPress(KeyBoardPressType eventType, int locationCode, int keyCode) {
        if (window.isCommandMode()) {
            if (eventType == KeyBoardPressType.RELEASED) {
                int input = keyCode;
                if (
                    (input >= KeyEvent.VK_A && input <= KeyEvent.VK_Z) ||
                    (input >= KeyEvent.VK_0 && input <= KeyEvent.VK_9) ||
                    (input >= KeyEvent.VK_NUMPAD0 && input <= KeyEvent.VK_NUMPAD9) ||
                    input == KeyEvent.VK_SPACE ||
                    input == KeyEvent.VK_MINUS
                ) {
                    window.appendCommandBuffer((char)input);
                } else if (input == KeyEvent.VK_ENTER) {
                    window.exitCommandMode(true);
                } else if (input == KeyEvent.VK_ESCAPE) {
                    window.exitCommandMode(false);
                } else if (input == KeyEvent.VK_BACK_SPACE) {
                    window.deleteCommandBufferCharacter();
                }
            }
        } else {
            final IUserInput inputCode = inputMapper.mapInput(eventType, KeyboardPressLocation.getLocationByCode(locationCode), keyCode);
            if (inputCode != null) {
                window.addInput(inputCode);
            }
        }
    }
}
