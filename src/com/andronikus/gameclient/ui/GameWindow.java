package com.andronikus.gameclient.ui;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameclient.engine.IClientInputManager;
import com.andronikus.gameclient.engine.IGameStateRenderer;
import com.andronikus.gameclient.engine.IRendererPresetup;
import com.andronikus.gameclient.ui.input.ClientInput;
import com.andronikus.gameclient.ui.input.ConcurrentServerInputManager;
import com.andronikus.gameclient.ui.input.IUserInput;
import com.andronikus.gameclient.ui.input.ServerInput;
import com.andronikus.gameclient.ui.keyboard.KeyBoardListener;
import com.andronikus.gameclient.ui.render.GameWindowRenderer;
import lombok.Getter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * The graphical user interface for a game. Responsible for rendering states of the game as well as collecting user
 * inputs.
 *
 * @author Andronikus
 */
public class GameWindow extends JPanel implements IGameStateRenderer, IClientInputManager, IRendererPresetup {

    @Getter
    private volatile int width;
    @Getter
    private volatile int height;
    @Getter
    private final RenderRatio candidateRenderRatio;

    private volatile String sessionId;
    private final JFrame frame;
    private volatile GameState latestGameState = null;
    private final GameWindowRenderer renderer;
    private final ConcurrentServerInputManager serverInputManager;

    @Getter
    private volatile boolean commandMode;
    @Getter
    private volatile String commandBuffer = "";
    /**
     * Locking mechanism to ensure that when the command is sending to the server the buffer is not changed by another
     * thread.
     */
    private volatile boolean commandLocked = false;
    @Getter
    private boolean advancedHudEnabled = false;
    @Getter
    private boolean collisionWatch = false;
    /*
     * Only written to from the mouse thread. Only read from from the tick thread.
     *
     * Determines that whatever is in the laser shot angle resource is to be wiped.
     */
    private volatile Double laserShotAngle = null;

    /**
     * Instantiate the graphical user interface for a game.
     */
    public GameWindow() {
        frame = new JFrame("Maybe Game Client"); // TODO cleverly title

        final MouseListenerImpl mouseListener = new MouseListenerImpl(this);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        KeyBoardListener keyBoardListener = new KeyBoardListener(this);
        this.addKeyListener(keyBoardListener);
        frame.addKeyListener(keyBoardListener);
        this.addComponentListener(new ResizeListener(this));

        frame.add(this);
        frame.setIconImage(ImagesUtil.getImage("icon.png"));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final RenderRatio renderRatio = new RenderRatio(550, 330);
        candidateRenderRatio = renderRatio.copy();
        renderer = new GameWindowRenderer(this, renderRatio);

        serverInputManager = new ConcurrentServerInputManager();
    }

    /**
     * Graphical hook.
     *
     * @param graphics The graphics to draw on.
     */
    public void paintComponent(Graphics graphics) {
        renderer.render(graphics, latestGameState, sessionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGameStateToRender(GameState toRender) {
        // Do not repaint off of the word of the server thread. Only a Java AWT thread can update this.
        latestGameState = toRender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        repaint();
    }

    /**
     * Add an input.
     *
     * @param input The input code
     */
    public void addInput(IUserInput input) {
        if (input instanceof ServerInput) {
            final ServerInput serverInput = (ServerInput) input;
            serverInput.setSessionId(sessionId);
            serverInputManager.addToQueue(serverInput);
        } else {
            handleClientInput((ClientInput) input);
        }
    }

    /**
     * Handle an input if it is intended for the Client, presumably, targeting state of the game window.
     *
     * @param clientInput The input
     */
    private void handleClientInput(ClientInput clientInput) {
        switch (clientInput.getType()) {
            case COMMAND_WINDOW_TOGGLE:
                if (latestGameState != null && latestGameState.isServerDebugMode() && !commandLocked) {
                    commandBuffer = "";
                    commandMode = true;
                }
                break;
            case DISPLAY_ADVANCED_HUD:
                if (latestGameState.isServerDebugMode()) {
                    advancedHudEnabled = !advancedHudEnabled;
                }
                break;
            case SHOW_COLLISION_MARKERS:
                if (latestGameState.isServerDebugMode()) {
                    collisionWatch = !collisionWatch;
                }
                break;
        }
    }

    /**
     * Add a character to the command buffer.
     *
     * @param character The character
     */
    public void appendCommandBuffer(char character) {
        if (!commandLocked) {
            commandBuffer += character;
        }
    }

    /**
     * Delete a character from the command buffer.
     */
    public void deleteCommandBufferCharacter() {
        if (!commandLocked && commandBuffer.length() > 0) {
            commandBuffer = commandBuffer.substring(0, commandBuffer.length() - 1);
        }
    }

    /**
     * Exit command mode.
     *
     * @param doCommand Whether or not the command as read on the buffer should be executed.
     */
    public void exitCommandMode(boolean doCommand) {
        commandMode = false;
        if (doCommand) {
            commandLocked = true;
        }
    }

    public void setLaserShotCoordinates(Integer laserShotScreenX, Integer laserShotScreenY) {
        if (laserShotScreenX == null || laserShotScreenY == null) {
            // Don't lock it for this, since we don't want a deadlock, but set null
            laserShotAngle = null;
            return;
        }

        // Click the player exactly, don't shoot since we can't calculate angle. Okay if angle already set
        if (laserShotScreenX == 0 && laserShotScreenY == 0) {
            return;
        }

        final double xClickDelta = (double) (laserShotScreenX - (width / 2));
        final double yClickDelta = (double) (laserShotScreenY - (height / 2));
        final double clickDistance = Math.sqrt(Math.pow(xClickDelta, 2) + Math.pow(yClickDelta, 2));

        final double theta = Math.acos(xClickDelta / clickDistance);
        if (yClickDelta >= 0) {
            laserShotAngle = -theta;
        } else {
            laserShotAngle = theta;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupBeforeRender() {
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setVisible(true);

        width = (int) frame.getSize().getWidth();
        height = (int) frame.getSize().getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommand() {
        String command = null;
        if (commandLocked) {
            command = commandBuffer;
            commandLocked = false;
        }
        return command;
    }

    public void setDimensions(int aWidth, int aHeight) {
        this.width = aWidth;
        this.height = aHeight;

        candidateRenderRatio.calculate(aWidth, aHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ServerInput> getAndClearInputs() {
        serverInputManager.removeInputsFromServerState(latestGameState);
        return serverInputManager.getUnhandledInputs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getInputPurgeRequests() {
        return serverInputManager.getInputIdsToPurge();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ServerInput> getTickInputs() {
        final ArrayList<ServerInput> newInputs = new ArrayList<>();

        // Cache angle for function in case of concurrent change
        final Double copyOfLaserAngle = laserShotAngle;
        if (copyOfLaserAngle != null) {
            final ServerInput shootInput = new ServerInput("SHOOT");
            shootInput.setParameter0(copyOfLaserAngle);
            shootInput.setSessionId(sessionId);
            // Shoot inputs are unacked
            // TODO okay but what if we need this to have the next id
            shootInput.setInputId(-1L);
            newInputs.add(shootInput);
        }

        return newInputs;
    }
}
