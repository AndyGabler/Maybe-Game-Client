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
    public String mapInput(KeyBoardPressType eventType, int keyCode) {
        String inputCode = null;

        // TODO this will turn to spaghetti real fast, consider more dynamic mapping object?
        switch(eventType) {
            case RELEASED:
                break;
            case TYPED:
                break;
            case PRESSED:
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        inputCode = "THRUST";
                        break;
                }
                break;
        }

        return inputCode;
    }
}