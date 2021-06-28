package com.andronikus.gameclient.ui.keyboard;

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
     * @return Input code if we are interested in this code, otherwise, null
     */
    String mapInput(KeyBoardPressType eventType, KeyboardPressLocation location, int keyCode);
}
