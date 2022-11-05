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
    private static final int PURGE_SIZE = INPUT_SIZE; // Same since should be able to purge what you make

    private final ConcurrentLinkedQueue<ServerInput> inputQueue;
    private final LinkedTransferQueue<ServerInput> ackRequiredInputTransferQueue;
    private final ConcurrentLinkedQueue<Long> inputPurgeQueue;
    private final ArrayList<ServerInput> ackRequiredInputs;
    private volatile int inputHeadPosition;
    private volatile int inputPurgeHeadPosition;
    private volatile int inputSize;
    private volatile int purgeQueueSize;
    private final ResourceLock<InputIdManager> idManagerLock;

    /**
     * Instantiate input manager.
     */
    public ConcurrentServerInputManager() {
        inputQueue = new ConcurrentLinkedQueue<>();
        ackRequiredInputTransferQueue = new LinkedTransferQueue<>();
        ackRequiredInputs = new ArrayList<>();
        inputPurgeQueue = new ConcurrentLinkedQueue<>();
        idManagerLock = new ResourceLock<>(new InputIdManager());
        inputHeadPosition = 0;
        inputPurgeHeadPosition = 0;
        inputSize = 0;
        purgeQueueSize = 0;
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
            checkAckAndQueuePurge(state, input)
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
        inputSize = inputSize + 1; // TODO okay, yikes. volatile called from 2 threads (input and server broadcast)
        inputQueue.add(input);
    }

    /**
     * Get input codes that have not yet been processed.
     *
     * @return The input codes
     */
    public List<ServerInput> getUnhandledInputs() {
        // Put on the queue those inputs which require acking that have not yet been acked
        final ArrayList<ServerInput> inputCodes = new ArrayList<>();
        int inputAllowance = INPUT_SIZE - ackRequiredInputs.size();

        if (inputAllowance > 0) {
            // Process inputs, running head position is so amount to send is locked in
            final int postCallHeadPosition = Math.min(inputSize, inputHeadPosition + inputAllowance);
            final int indexToCountTo = postCallHeadPosition - inputHeadPosition;
            inputHeadPosition = postCallHeadPosition;

            int index = 0;
            while (index < indexToCountTo) {
                inputCodes.add(inputQueue.poll());
                index++;
                inputAllowance--;
            }
        }
        for (int index = 0; index < ackRequiredInputs.size() && inputAllowance > 0; index++) {
            inputCodes.add(ackRequiredInputs.get(0));
            inputAllowance--;
        }

        return inputCodes;
    }

    private boolean checkAckAndQueuePurge(GameState state, ServerInput input) {
        final boolean isAcked = input.checkAck(state);

        if (input.isDirectAckRequired()) {
            inputPurgeQueue.add(input.getInputId());
            purgeQueueSize += 1;
        }

        return isAcked;
    }

    public List<Long> getInputIdsToPurge() {
        final int postCallHeadPosition = Math.min(purgeQueueSize, inputPurgeHeadPosition + PURGE_SIZE);
        final int indexToCountTo = postCallHeadPosition - inputPurgeHeadPosition;
        inputPurgeHeadPosition = postCallHeadPosition;

        final ArrayList<Long> inputIds = new ArrayList<>();
        int index = 0;
        while (index < indexToCountTo) {
            inputIds.add(inputPurgeQueue.poll());
            index++;
        }

        return inputIds;
    }
}
