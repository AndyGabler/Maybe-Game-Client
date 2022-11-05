package com.andronikus.gameclient.engine;

import com.andronikus.game.model.client.ClientRequest;
import com.andronikus.game.model.client.InputRequest;
import com.andronikus.game.model.client.InputPurgeRequest;
import com.andronikus.game.model.server.GameState;
import com.andronikus.gameclient.client.GameClient;
import com.andronikus.gameclient.engine.command.ClientCommandManager;
import com.andronikus.gameclient.ui.input.ServerInput;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

/**
 * Engine for the client. Hook for all components that create some kind of event (IE server messages or ticks).
 *
 * @author Andronikus
 */
public class ClientEngine implements ActionListener {

    /**
     * The delay a timer needs to fire 30 times a second
     */
    private static final double JAVA_TIMER_30FPS_DELAY = 30;

    private int sequenceNumber;
    private final GameClient client;
    private final IGameStateRenderer renderer;
    private final IClientInputManager inputManager;
    private final IRendererPresetup setupOperations;
    private final Timer timer;
    private ClientCommandManager commandManager = null;

    // Setter for whether or not this client has been acked by the server
    private volatile GameState latestGameState = null;
    private volatile boolean serverAckedClient = false;

    /**
     * Instantiate engine for the client.
     *
     * @param aClient The client the engine is connected to
     * @param aRenderer The render that renders game states
     * @param aInputSupplier Supplier for user inputs
     * @param aSetupOperations Operations to setup the renderer
     */
    public ClientEngine(GameClient aClient, IGameStateRenderer aRenderer, IClientInputManager aInputSupplier, IRendererPresetup aSetupOperations) {
        client = aClient;
        renderer = aRenderer;
        inputManager = aInputSupplier;
        setupOperations = aSetupOperations;
        sequenceNumber = 0;
        timer = makeTimer(30, this); // TODO non-static or different frame rate?
    }

    /**
     * Create a timer with a specified amount of ticks per second.
     *
     * @param tickRate The tick rate
     * @param listener The listener the timer calls
     * @return The timer
     */
    private static Timer makeTimer(int tickRate, ActionListener listener) {
        return new Timer((int) Math.pow(JAVA_TIMER_30FPS_DELAY, 2) / tickRate, listener);
    }

    /**
     * Perform an engine tick.
     */
    private void tick() {

        // Until server acks client, keep sending requests
        if (
            !serverAckedClient && (
            latestGameState == null ||
            latestGameState.getPlayers().stream().anyMatch(player -> player.getSessionId().equalsIgnoreCase(client.getSessionId()))
        )) {
            // Clear queued inputs
            sequenceNumber = sequenceNumber + 1;
            inputManager.getAndClearInputs();

            final ClientRequest request = new ClientRequest();
            request.setSessionToken(client.getSessionSecret());
            request.setSequenceNumber(sequenceNumber);

            final InputRequest joinGameRequest = new InputRequest();
            joinGameRequest.setInputCode("JOINGAME");

            request.setInputCode0(joinGameRequest);

            client.sendClientRequest(request);
            return;
        }

        final List<ServerInput> inputCodes = inputManager.getAndClearInputs();
        final String commandCode = inputManager.getCommand();
        final List<Long> inputIdsToPurge = inputManager.getInputPurgeRequests();
        if (inputCodes.size() > 0 || commandCode != null || inputIdsToPurge.size() > 0) {
            sequenceNumber = sequenceNumber + 1;

            final ClientRequest request = new ClientRequest();
            request.setSessionToken(client.getSessionSecret());
            request.setSequenceNumber(sequenceNumber);
            for (int index = 0; index < inputCodes.size(); index++) {
                final ServerInput nextServerInput = inputCodes.get(index);
                final InputRequest inputRequest = new InputRequest();
                inputRequest.setInputId(nextServerInput.getInputId());
                inputRequest.setInputCode(nextServerInput.getCode());
                inputRequest.setAckRequired(nextServerInput.isDirectAckRequired());

                if (index == 0) {
                    request.setInputCode0(inputRequest);
                } else if (index == 1) {
                    request.setInputCode1(inputRequest);
                } else if (index == 2) {
                    request.setInputCode2(inputRequest);
                } else if (index == 3) {
                    request.setInputCode3(inputRequest);
                } else if (index == 4) {
                    request.setInputCode4(inputRequest);
                }
            }

            for (int index = 0; index < inputIdsToPurge.size(); index++) {
                final Long idToPurge = inputIdsToPurge.get(index);
                final InputPurgeRequest purgeRequest = new InputPurgeRequest();
                purgeRequest.setId(idToPurge);

                if (index == 0) {
                    request.setInputPurge0(purgeRequest);
                } else if (index == 1) {
                    request.setInputPurge1(purgeRequest);
                } else if (index == 2) {
                    request.setInputPurge2(purgeRequest);
                } else if (index == 3) {
                    request.setInputPurge3(purgeRequest);
                } else if (index == 4) {
                    request.setInputPurge4(purgeRequest);
                }
            }

            if (latestGameState != null && commandManager != null && latestGameState.isServerDebugMode()) {
                if (commandCode != null) {
                    commandManager.addCommand(commandCode);
                }
                commandManager.processGameState(latestGameState);
                request.setClientCommands(commandManager.getUnackedCommands());
                request.setCommandsToRemove(commandManager.getAckedCommands());
            } else {
                request.setClientCommands(Collections.emptyList());
                request.setCommandsToRemove(Collections.emptyList());
            }

            client.sendClientRequest(request);
        }
    }

    /**
     * Take a game state.
     *
     * @param gameState The game state
     */
    public void takeGameState(GameState gameState) {
        latestGameState = gameState;

        // TODO mechanism for handling server rejecting the client
        if (!serverAckedClient &&
            gameState.getPlayers().stream().anyMatch(player -> player.getSessionId().equals(client.getSessionId()))) {
            commandManager = new ClientCommandManager(client.getSessionId());
            serverAckedClient = true;
        }

        renderer.setGameStateToRender(gameState);
    }

    /**
     * Start the engine.
     */
    public void start() {
        timer.start();
        setupOperations.setupBeforeRender();
        renderer.setSessionId(client.getSessionId());
    }

    /**
     * Pause the engine.
     */
    public void pauseEngine() {
        timer.stop();
    }

    /**
     * Resume the engine.
     */
    public void resumeEngine() {
        timer.start();
    }

    /**
     * Kill the engine.
     */
    public void kill() {
        timer.stop();
    }

    /**
     * Hook for the internal timer to decide when the engine should fire.
     *
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        tick();
        renderer.render();
    }
}
