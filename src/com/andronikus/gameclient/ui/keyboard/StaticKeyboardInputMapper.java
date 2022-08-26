package com.andronikus.gameclient.ui.keyboard;

import com.andronikus.gameclient.ui.input.ClientInput;
import com.andronikus.gameclient.ui.input.ClientInputType;
import com.andronikus.gameclient.ui.input.IUserInput;
import com.andronikus.gameclient.ui.input.ServerInput;

import java.awt.event.KeyEvent;

/**
 * Static and default mapper of keyboard input to input codes. Does not take any kind of configuration into account.
 *
 * @author Andronikus
 */
public class StaticKeyboardInputMapper implements IKeyBoardInputMapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public IUserInput mapInput(KeyBoardPressType eventType, KeyboardPressLocation location, int keyCode) {
        IUserInput input = null;

        // TODO this will turn to spaghetti real fast, consider more dynamic mapping object?
        switch(eventType) {
            case RELEASED:
                switch (keyCode) {
                    case KeyEvent.VK_Q:
                        input = new ServerInput("BOOSTEND");
                        break;
                    case KeyEvent.VK_SPACE:
                        input = new ServerInput("SHOOT");
                        break;
                    case KeyEvent.VK_W:
                        input = new ServerInput("THRUSTEND");
                        break;
                    case KeyEvent.VK_ENTER:
                        input = new ClientInput(ClientInputType.COMMAND_WINDOW_TOGGLE);
                        break;
                    case KeyEvent.VK_F1:
                        input = new ClientInput(ClientInputType.SHOW_COLLISION_MARKERS);
                        break;
                    case KeyEvent.VK_F3:
                        input = new ClientInput(ClientInputType.DISPLAY_ADVANCED_HUD);
                        break;
                }
                break;
            case TYPED:
                break;
            case PRESSED:
                switch (keyCode) {
                    case KeyEvent.VK_Q:
                        input = new ServerInput("BOOST");
                        break;
                    case KeyEvent.VK_SHIFT:
                        if (location == KeyboardPressLocation.LEFT) {
                            input = new ServerInput("BREAK");
                        }
                        break;
                    case KeyEvent.VK_W:
                        input = new ServerInput("THRUST");
                        break;
                    case KeyEvent.VK_S:
                        input = new ServerInput("RTHRUST");
                        break;
                    case KeyEvent.VK_A:
                        input = new ServerInput("LROTATE");
                        break;
                    case KeyEvent.VK_D:
                        input = new ServerInput("RROTATE");
                        break;
                }
                break;
        }

        return input;
    }
}
