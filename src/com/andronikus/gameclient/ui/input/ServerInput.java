package com.andronikus.gameclient.ui.input;

import com.andronikus.game.model.server.GameState;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BiFunction;

/**
 * Input to the server.
 *
 * @author Andronikus
 */
public class ServerInput implements IUserInput {

    @Getter
    private final String code;

    @Getter
    private boolean directAckRequired = false;

    private final BiFunction<GameState, ServerInput, Boolean> repeatUntilFulfilledCondition;

    /**
     * ID of the input to assign pre-flight.
     */
    @Getter
    @Setter
    private Long inputId = null;

    /**
     * Session ID of the input.
     */
    @Getter
    @Setter
    private String sessionId;

    /**
     * Instantiate an input to the server.
     *
     * @param aCode The input code
     */
    public ServerInput(String aCode) {
        this(aCode, null);
    }

    /**
     * Instantiate an input to the server.
     *
     * @param aCode The input code
     * @param aRequiresAck If the input requires ack from the server
     */
    public ServerInput(String aCode, boolean aRequiresAck) {
        this(aCode, serverAcknowledgementScanner(aRequiresAck));
        directAckRequired = aRequiresAck;
    }

    private static BiFunction<GameState, ServerInput, Boolean> serverAcknowledgementScanner(boolean scannerNeeded) {
        if (!scannerNeeded) {
            return null;
        }

        return ((gameState, input) ->
            gameState.getInputAcknowledgements()
                .stream()
                .anyMatch(ack -> ack.getInputId() == input.inputId && ack.getSessionId().equalsIgnoreCase(input.sessionId))
        );
    }

    /**
     * Instantiate an input to the server.
     *
     * @param aCode The input code
     * @param aRepeatUntilFulfilledCondition Condition that, when fulfilled, will stop the repetition of an input
     */
    public ServerInput(String aCode, BiFunction<GameState, ServerInput, Boolean> aRepeatUntilFulfilledCondition) {
        code = aCode;
        repeatUntilFulfilledCondition = aRepeatUntilFulfilledCondition;
    }

    public boolean checkAck(GameState state) {
        return repeatUntilFulfilledCondition.apply(state, this);
    }

    public boolean isServerConditionCheckRequired() {
        return repeatUntilFulfilledCondition != null;
    }
}
