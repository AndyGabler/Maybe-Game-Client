package com.gabler.gameclient.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Concurrent thread-safe input manager that allows for reading and writing at the same time.
 *
 * @author Andy Gabler
 */
public class ConcurrentInputManager {

    private final ConcurrentLinkedQueue<String> codes;
    private volatile int headPosition;
    private volatile int size;

    /**
     * Instantiate input manager.
     */
    public ConcurrentInputManager() {
        codes = new ConcurrentLinkedQueue<>();
        headPosition = 0;
        size = 0;
    }

    /**
     * Add an input to the queue.
     *
     * @param code The input code
     */
    public void addToQueue(String code) {
        size = size + 1;
        codes.add(code);
    }

    /**
     * Get input codes that have not yet been processed.
     *
     * @return The input codes
     */
    public List<String> getUnhandledCodes() {
        final int sizeAtTimeOfCall = size;
        final int indexToCountTo = sizeAtTimeOfCall - headPosition;
        headPosition = sizeAtTimeOfCall;

        final ArrayList<String> inputCodes = new ArrayList<>();
        int index = 0;
        while (index < indexToCountTo) {
            inputCodes.add(codes.poll());
            index++;
        }

        return inputCodes;
    }
}
