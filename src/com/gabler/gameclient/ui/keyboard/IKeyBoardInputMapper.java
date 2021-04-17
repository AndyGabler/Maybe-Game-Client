package com.gabler.gameclient.ui.keyboard;

/**
 * Mapper of keyboard input to input codes.
 *
 * @author Andy Gabler
 */
public interface IKeyBoardInputMapper {

    /**
     * Map keyboard event to an input code.
     *
     * @param eventType The type of keyboard event
     * @param keyCode The code for the character pressed
     * @return Input code if we are interested in this code, otherwise, null
     */
    String mapInput(KeyBoardPressType eventType, int keyCode);
}
