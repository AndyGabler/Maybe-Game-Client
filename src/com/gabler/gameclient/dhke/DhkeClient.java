package com.gabler.gameclient.dhke;

import com.gabler.client.Client;
import com.gabler.client.ClientConfiguration;
import com.gabler.client.ClientStartException;
import com.gabler.server.ChatThread;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client for the creation of AES keys through Diffie-Helman Key Exchange.
 *
 * @author Andy Gabler
 */
public class DhkeClient extends ClientConfiguration {

    private static final Logger LOGGER = Logger.getLogger("DhkeClient");
    private static final int DHKE_SERVLET_PORT = 13351;

    private final BiConsumer<byte[], String> keyAndIdConsumer;
    private volatile DhkeState dhkeState = null;

    /**
     * Instantiate client for the creation of AES keys through Diffie-Helman Key Exchange.
     *
     * @param aKeyAndIdConsumer Callback to call whenever a key is calculated
     */
    public DhkeClient(BiConsumer<byte[], String> aKeyAndIdConsumer) {
        keyAndIdConsumer = aKeyAndIdConsumer;
    }

    /**
     * Start the client.
     *
     * @param hostName The name of the host of the DHKE server
     * @throws ClientStartException If the client cannot start
     */
    public void start(String hostName) throws ClientStartException {
        final Client client = new Client(hostName, DHKE_SERVLET_PORT);
        client.setOperations(this);
        client.startConnection();
    }

    /**
     * Perform a DHKE with the server and give that key to the callback.
     */
    public void requestNewKey() {
        // Just send some gibberish to the server to get its attention and get it to send a public key
        this.client.sendMessage("AHJ3281DFADSF3218312DARF");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String handleIncoming(String message, ChatThread chatThread, ChatThread chatThread1) {
        // Two parameters are garbage for clients, we know it came from server.

        if (message.equalsIgnoreCase("E")) {
            LOGGER.severe("Server sent error response code.");
            dhkeState = null;
            return null;
        }

        // On the first message after completion, server sends an ID for the key we received
        if (dhkeState != null && dhkeState.isComplete()) {
            keyAndIdConsumer.accept(dhkeState.getKey(), message);
            dhkeState = null;
            return null;
        }

        if (dhkeState == null) {
            dhkeState = new DhkeState();
        }

        BigInteger nextIntToSendServer = null;
        if (message.contains(" ")) {
            try {
                dhkeState.takeNextInteger(new BigInteger(message.substring(0, message.indexOf(" "))));
                nextIntToSendServer = dhkeState.takeNextInteger(new BigInteger(message.substring(message.indexOf(" ") + 1)));
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Failed to parse server message to BigInteger: " + message, exception);
            }
        } else {
            try {
                nextIntToSendServer = dhkeState.takeNextInteger(new BigInteger(message));
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Failed to parse server message to BigInteger: " + message, exception);
            }
        }

        if (nextIntToSendServer != null) {
            this.client.sendMessage(nextIntToSendServer.toString());
        }

        // No ack sent to server
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCallMethod() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canTerminate() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void threadTerminationAction() {
        LOGGER.info("DHKE client listening thread terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clientTerminationAction() {
        LOGGER.info("DHKE client terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void joinAction(PrintStream printStream) {
        LOGGER.info("DHKE client connected to DHKE Server.");
    }
}
