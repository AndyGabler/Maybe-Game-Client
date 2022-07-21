package com.andronikus.gameclient.ui.input;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Input to the client.
 *
 * @author Andronikus
 */
public class ClientInput implements IUserInput {

    @Getter
    private ClientInputType type;
    @Getter
    private List<Object> parameters;

    /**
     * Instantiate an input to the client.
     *
     * @param aType The input type
     * @param parameters Parameters to the input
     */
    public ClientInput(ClientInputType aType, Object... parameters) {
        this.type = aType;
        this.parameters = Arrays.stream(parameters).collect(Collectors.toList());
    }
}
