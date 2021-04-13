package com.gabler.gameclient.auth;

import com.gabler.client.Client;
import com.gabler.client.ClientConfiguration;
import com.gabler.client.ClientStartException;
import com.gabler.server.ChatThread;
import lombok.SneakyThrows;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Client for authenticating with a central server.
 *
 * @author Andy Gabler
 */
public class AuthenticationClient extends ClientConfiguration {

    private static final Logger LOGGER = Logger.getLogger("AuthenticationClient");
    private static final int AUTH_SERVLET_PORT = 13352;

    private final BiConsumer<String, String> sessionInfoConsumer;

    public AuthenticationClient(BiConsumer<String, String> aSessionInfoConsumer) {
        sessionInfoConsumer = aSessionInfoConsumer;
    }

    /**
     * Start the client.
     *
     * @param hostName The name of the host of the authentication server
     * @throws ClientStartException If the client cannot start
     */
    public void start(String hostName) throws ClientStartException {
        final Client client = new Client(AuthenticationClient::createSslSocket, hostName, AUTH_SERVLET_PORT);
        client.setOperations(this);
        client.startConnection();
    }

    @SneakyThrows
    private static Socket createSslSocket(String hostName, int portNumber) {
        final SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(hostName, portNumber);
        socket.startHandshake();
        return socket;
    }

    /**
     * Request authentication from server.
     */
    public void requestAuth(String username, String password) {
        client.sendMessage(username + " " + password);
    }

    /**
     * Terminate the client.
     */
    public void terminate() {
        LOGGER.info("Terminating Authentication Client.");
        client.terminate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String handleIncoming(String message, ChatThread chatThread, ChatThread chatThread1) {
        // Two parameters are garbage for clients, we know it came from server.
        if (message.equalsIgnoreCase("NOSESSION")) {
            sessionInfoConsumer.accept(null, null);
        } else if (message.length() > "SESSION ".length() && message.substring(0, "SESSION".length()).equalsIgnoreCase("SESSION")) {
            final String secretAndId = message.substring("SESSION ".length());
            final int indexOfSpace = secretAndId.indexOf(" ");
            final String secret = secretAndId.substring(0, indexOfSpace);
            final String id = secretAndId.substring(indexOfSpace + 1);
            sessionInfoConsumer.accept(secret, id);
        }

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
        LOGGER.info("Authentication client listening thread terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clientTerminationAction() {
        LOGGER.info("Authentication client terminated.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void joinAction(PrintWriter printStream) {
        LOGGER.info("Authentication client connected to Authentication Server.");
    }
}
