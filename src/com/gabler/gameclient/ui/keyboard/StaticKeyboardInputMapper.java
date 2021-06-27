package com.gabler.gameclient.ui.keyboard;

import java.awt.event.KeyEvent;

/**
 * Static and default mapper of keyboard input to input codes. Does not take any kind of configuration into account.
 *
 * @author Andy Gabler
 */
public class StaticKeyboardInputMapper implements IKeyBoardInputMapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public String mapInput(KeyBoardPressType eventType, KeyboardPressLocation location, int keyCode) {
        String inputCode = null;

        // TODO this will turn to spaghetti real fast, consider more dynamic mapping object?
        switch(eventType) {
            case RELEASED:
                switch (keyCode) {
                    case KeyEvent.VK_Q:
                        inputCode = "BOOSTEND";
                        break;
                }
                break;
            case TYPED:
                break;
            case PRESSED:
                switch (keyCode) {
                    case KeyEvent.VK_Q:
                        inputCode = "BOOST";
                        break;
                    case KeyEvent.VK_SHIFT:
                        if (location == KeyboardPressLocation.LEFT) {
                            inputCode = "BREAK";
                        }
                        break;
                    case KeyEvent.VK_W:
                        inputCode = "THRUST";
                        break;
                    case KeyEvent.VK_S:
                        inputCode = "RTHRUST";
                        break;
                    case KeyEvent.VK_A:
                        inputCode = "LROTATE";
                        break;
                    case KeyEvent.VK_D:
                        inputCode = "RROTATE";
                        break;
                }
                break;
        }

        return inputCode;
    }
}
