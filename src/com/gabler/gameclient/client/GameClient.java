package com.gabler.gameclient.client;

import com.gabler.client.ClientStartException;
import com.gabler.gameclient.auth.AuthenticationClient;
import com.gabler.gameclient.dhke.DhkeClient;
import com.gabler.gameclient.engine.ClientEngine;
import com.gabler.gameclient.engine.IGameStateRenderer;
import com.gabler.game.model.server.GameState;
import com.gabler.udpmanager.client.IUdpClientConfiguration;
import com.gabler.udpmanager.client.UdpClient;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Game client. Manages security and framework for transmitting messages between a central game server and game client.
 *
 * @author Andy Gabler
 */
public class GameClient implements IUdpClientConfiguration {

    private static final Logger LOGGER = Logger.getLogger("GameClient");

    private static final int GAME_SERVER_PORT = 13350;

    private final Function<byte[], GameState> byteToGameStateTransformer;
    private final String hostname;

    private volatile long latestRecordedSequenceNumber;
    private volatile boolean keyReceived = false;
    private volatile boolean authenticated = false;
    private volatile String sessionSecret;
    private volatile String sessionId;

    private final DhkeClient keyClient;
    private final UdpClient client;
    private final ClientEngine engine;
    private final AuthenticationClient authenticationClient;

    /**
     * Instantiate a game client.
     *
     * @param aHostname Hostname of the game server
     * @param aRenderer Renderer the engine uses
     */
    public GameClient(String aHostname, IGameStateRenderer aRenderer) {
        this(new BytesToObjectTransformer<>(), aHostname, aRenderer);
    }

    /**
     * Instantiate a game client.
     *
     * @param aByteToGameStateTransformer Transformer for turning a bytes message to a {@link GameState}
     * @param aHostname Hostname of the game server
     * @param aRenderer Renderer the engine uses
     */
    @SneakyThrows
    public GameClient(Function<byte[], GameState> aByteToGameStateTransformer, String aHostname, IGameStateRenderer aRenderer) {
        byteToGameStateTransformer = aByteToGameStateTransformer;
        hostname = aHostname;

        keyClient = new DhkeClient(this::setKey);
        client = new UdpClient(aHostname, GAME_SERVER_PORT);
        client.setConfiguration(this);
        engine = new ClientEngine(this::getSessionId, aRenderer);
        authenticationClient = new AuthenticationClient(this::setSessionInfo);
    }

    /**
     * Set the key that will be used by the client.
     *
     * @param key The key in bytes
     * @param keyId Identifier for the key
     */
    private void setKey(byte[] key, String keyId) {
        LOGGER.info("Setting AES key with id " + keyId);
        if (key != null) {
            client.setClientKey(keyId, key);
            keyReceived = true;
        }
    }

    /**
     * Set the session secret so that the session can be referenced with the server.
     *
     * @param aSessionSecret The session reference
     * @param aSessionId The ID of the session
     */
    private void setSessionInfo(String aSessionSecret, String aSessionId) {
        // Logging this is okay since this is client-side
        LOGGER.info("Session started with server: " + aSessionSecret);

        authenticated = true;
        sessionSecret = aSessionSecret;
        sessionId = aSessionId;
    }

    /**
     * Get public identifier for the session with the server. This is non-secret and the server may broadcast this.
     *
     * @return The session id
     */
    public synchronized String getSessionId() {
        return sessionId;
    }

    /**
     * Start the game client.
     *
     * @param username Username
     * @param password The password
     * @throws ClientStartException If underlying client failed to start
     * @throws GameClientStartException If logic error occurs
     * @throws IOException If error in sending preliminary message to server
     */
    public void start(String username, String password) throws ClientStartException, GameClientStartException, IOException {
        // First up on the docket, let's authenticate with the server
        authenticationClient.start(hostname);
        authenticationClient.requestAuth(username, password);

        // TODO timeout mechanism
        while (!authenticated) {
            Thread.onSpinWait();
        }
        authenticationClient.terminate();

        if (sessionSecret == null) {
            authenticated = false;
            throw new GameClientStartException("Authentication failed.");
        }

        // Okay, we have a session. Let's see to it that we have a shared secret with the game server.
        keyClient.start(hostname);
        keyClient.requestNewKey();

        // TODO timeout mechanism
        while (!keyReceived) {
            Thread.onSpinWait();
        }

        // Okay, we're good to go live.
        client.start();
        client.sendMessageToServer("CONN"); // TODO this can fail, add a mechanism to actually ensure the server notices or timeout
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleBytesMessage(byte[] bytes) {

        GameState gameState;
        try {
            gameState = byteToGameStateTransformer.apply(bytes);
        } catch (Exception exception) {
            /*
             * This is extremely abnormal.
             */
            LOGGER.log(Level.SEVERE, "Could not serialize server bytes message to a GameState.", exception);
            return;
        }

        if (gameState.getVersion() > latestRecordedSequenceNumber) {
            engine.takeGameState(gameState);
            latestRecordedSequenceNumber = gameState.getVersion();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleStringMessage(String s) {
        LOGGER.warning("String message received. This is abnormal.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAction() {
        LOGGER.info("Game Client started.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminationAction() {
        LOGGER.info("Game Client terminated. Killing the engine and key client.");
        keyClient.terminate();
        engine.kill();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pauseAction() {
        LOGGER.info("Game client paused. Paused engine.");
        engine.pauseEngine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resumeAction() {
        LOGGER.info("Game client resumed. Resuming engine.");
        engine.resumeEngine();
    }
}
