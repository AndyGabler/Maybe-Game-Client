package com.gabler.gameclient.ui.keyboard;

import java.awt.event.KeyEvent;

/**
 * Location of a key stroke.
 *
 * @author Andy Gabler
 */
public enum KeyboardPressLocation {
    LEFT(KeyEvent.KEY_LOCATION_LEFT),
    RIGHT(KeyEvent.KEY_LOCATION_RIGHT),
    NUMPAD(KeyEvent.KEY_LOCATION_NUMPAD),
    STANDARD(KeyEvent.KEY_LOCATION_STANDARD),
    UNKOWN(KeyEvent.KEY_LOCATION_UNKNOWN),
    NONE(null); // TODO see if location can ever be out of range

    private final Integer code;

    KeyboardPressLocation(Integer aCode) {
        code = aCode;
    }

    /**
     * Get location of the key by the code.
     *
     * @param code The code
     * @return Location of the key based on the code
     */
    public static KeyboardPressLocation getLocationByCode(int code) {
        for (KeyboardPressLocation candidateLocation : KeyboardPressLocation.values()) {
            if (candidateLocation.code == code) {
                return candidateLocation;
            }
        }

        return NONE;
    }
}
