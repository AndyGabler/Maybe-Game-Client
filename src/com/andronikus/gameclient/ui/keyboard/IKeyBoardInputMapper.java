package com.andronikus.gameclient.ui.keyboard;

import com.andronikus.gameclient.ui.input.IUserInput;

/**
 * Mapper of keyboard input to input codes.
 *
 * @author Andronikus
 */
public interface IKeyBoardInputMapper {

    /**
     * Map keyboard event to an input code.
     *
     * @param eventType The type of keyboard event
     * @param location Location of the key-board press
     * @param keyCode The code for the character pressed
     * @return Input if we are interested in this code, otherwise, null
     */
    IUserInput mapInput(KeyBoardPressType eventType, KeyboardPressLocation location, int keyCode);
}
