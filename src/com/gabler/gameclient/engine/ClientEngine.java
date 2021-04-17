package com.gabler.gameclient.engine;

import com.gabler.game.model.client.ClientRequest;
import com.gabler.game.model.server.GameState;
import com.gabler.gameclient.client.GameClient;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Engine for the client. Hook for all components that create some kind of event (IE server messages or ticks).
 *
 * @author Andy Gabler
 */
public class ClientEngine implements ActionListener {

    /**
     * The delay a timer needs to fire 30 times a second
     */
    private static final double JAVA_TIMER_30FPS_DELAY = 30;

    private int sequenceNumber;
    private final GameClient client;
    private final IGameStateRenderer renderer;
    private final IClientInputSupplier inputSupplier;
    private final IRendererPresetup setupOperations;
    private final Timer timer;

    /**
     * Instantiate engine for the client.
     *
     * @param aClient The client the engine is connected to
     * @param aRenderer The render that renders game states
     * @param aInputSupplier Supplier for user inputs
     * @param aSetupOperations Operations to setup the renderer
     */
    public ClientEngine(GameClient aClient, IGameStateRenderer aRenderer, IClientInputSupplier aInputSupplier, IRendererPresetup aSetupOperations) {
        client = aClient;
        renderer = aRenderer;
        inputSupplier = aInputSupplier;
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

        final List<String> inputCodes = inputSupplier.getAndClearInputs();
        if (inputCodes.size() > 0) {
            sequenceNumber = sequenceNumber + 1;

            final ClientRequest request = new ClientRequest();
            request.setSessionToken(client.getSessionSecret());
            request.setSequenceNumber(sequenceNumber);
            request.setInputCodes(inputCodes);

            client.sendClientRequest(request);
        }
    }

    /**
     * Take a game state.
     *
     * @param gameState The game state
     */
    public void takeGameState(GameState gameState) {
        renderer.setGameStateToRender(gameState);
    }

    /**
     * Start the engine.
     */
    public void start() {
        timer.start();
        setupOperations.setupBeforeRender();
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
