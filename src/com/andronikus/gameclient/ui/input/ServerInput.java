package com.andronikus.gameclient.ui.input;

import lombok.Getter;

/**
 * Input to the server.
 *
 * @author Andronikus
 */
public class ServerInput implements IUserInput {

    @Getter
    private String code;

    /**
     * Instantiate an input to the server.
     *
     * @param aCode The input code
     */
    public ServerInput(String aCode) {
        this.code = aCode;
    }
}
