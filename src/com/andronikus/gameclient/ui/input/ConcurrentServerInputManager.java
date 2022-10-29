package com.andronikus.gameclient.ui.input;

import com.andronikus.game.model.server.GameState;
import com.gabler.udpmanager.ResourceLock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Concurrent thread-safe input manager that allows for reading and writing at the same time.
 *
 * @author Andronikus
 */
public class ConcurrentServerInputManager {

    private static final int INPUT_SIZE = 5;

    private final ConcurrentLinkedQueue<ServerInput> inputQueue;
    private final LinkedTransferQueue<ServerInput> ackRequiredInputTransferQueue;
    private final ArrayList<ServerInput> ackRequiredInputs;
    private volatile int headPosition;
    private volatile int size;
    private final ResourceLock<InputIdManager> idManagerLock;

    /**
     * Instantiate input manager.
     */
    public ConcurrentServerInputManager() {
        inputQueue = new ConcurrentLinkedQueue<>();
        ackRequiredInputTransferQueue = new LinkedTransferQueue<>();
        ackRequiredInputs = new ArrayList<>();
        idManagerLock = new ResourceLock<>(new InputIdManager());
        headPosition = 0;
        size = 0;
    }


    /**
     * Remove inputs that are acked or their condition met from the list of repeating inputs. Meant to be called from a
     * single Input Polling thread.
     *
     * @param state The state from the server
     */
    public void removeInputsFromServerState(GameState state) {
        // TODO this feels a bit too un-modular with the requirement of a game state in here
        performQueueTransfer();

        ackRequiredInputs.removeIf(input ->
            input.checkAck(state)
        );
    }

    // TODO publicly expose?
    private void performQueueTransfer() {
        ServerInput input = ackRequiredInputTransferQueue.poll();
        while (input != null) {
            ackRequiredInputs.add(input);
            input = ackRequiredInputTransferQueue.poll();
        }
    }

    /**
     * Add an input to the queue. Meant to be called from a Input Source thread.
     *
     * @param input The input to put in the queue
     */
    public void addToQueue(ServerInput input) {
        idManagerLock.performRunInLock(idManager -> {
            final long id = idManager.getIdCounter();
            input.setInputId(id);
            idManager.setIdCounter(id + 1);
        });

        if (input.isServerConditionCheckRequired()) {
            ackRequiredInputTransferQueue.add(input);
        } else {
            enqueueInput(input);
        }
    }

    private void enqueueInput(ServerInput input) {
        size = size + 1; // TODO okay, yikes. volatile called from 2 threads (input and server broadcast)
        inputQueue.add(input);
    }

    /**
     * Get input codes that have not yet been processed.
     *
     * @return The input codes
     */
    public List<ServerInput> getUnhandledInputs() {
        // Put on the queue those inputs which require acking that have not yet been acked
        // TODO deadlocking potential? If server decides it's not going to ack inputs.... uh.... yikes. That's our input slots eaten
        ackRequiredInputs.forEach(this::addToQueue);

        // Process inputs
        final int postCallHeadPosition = Math.min(size, headPosition + INPUT_SIZE);
        final int indexToCountTo = postCallHeadPosition - headPosition;
        headPosition = postCallHeadPosition;

        final ArrayList<ServerInput> inputCodes = new ArrayList<>();
        int index = 0;
        while (index < indexToCountTo) {
            inputCodes.add(inputQueue.poll());
            index++;
        }

        return inputCodes;
    }
}
