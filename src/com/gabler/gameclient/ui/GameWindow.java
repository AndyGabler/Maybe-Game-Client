package com.gabler.gameclient.ui;

import com.gabler.game.model.server.GameState;
import com.gabler.gameclient.engine.IClientInputSupplier;
import com.gabler.gameclient.engine.IGameStateRenderer;
import com.gabler.gameclient.engine.IRendererPresetup;
import com.gabler.gameclient.ui.keyboard.KeyBoardListener;
import lombok.Getter;
import lombok.Setter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

/**
 * The graphical user interface for a game. Responsible for rendering states of the game as well as collecting user
 * inputs.
 *
 * @author Andy Gabler
 */
public class GameWindow extends JPanel implements IGameStateRenderer, IClientInputSupplier, IRendererPresetup {

    @Getter
    @Setter
    private volatile int width;

    @Getter
    @Setter
    private volatile int height;
    private volatile String sessionId;
    private final JFrame frame;
    private volatile GameState latestGameState = null;
    private final ConcurrentInputManager inputManager;

    /**
     * Instantiate the graphical user interface for a game.
     */
    public GameWindow() {
        frame = new JFrame("Maybe Game Client"); // TODO cleverly title

        final MouseListenerImpl mouseListener = new MouseListenerImpl(this);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        final KeyBoardListener keyBoardListener = new KeyBoardListener(this);
        this.addKeyListener(keyBoardListener);
        frame.addKeyListener(keyBoardListener);
        this.addComponentListener(new ResizeListener(this));

        frame.add(this);
        frame.setIconImage(ImagesUtil.getImage("icon.png"));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inputManager = new ConcurrentInputManager();
    }

    /**
     * Graphical hook.
     *
     * @param graphics The graphics to draw on.
     */
    public void paintComponent(Graphics graphics) {
        // TODO actual render
        final GameState state = latestGameState;
        if (state == null) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, width, height);
            return;
        }

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);

        state.getPlayers().forEach(player -> {
            if (player.getSessionId().equalsIgnoreCase(sessionId)) {
                graphics.setColor(Color.RED);
            } else {
                graphics.setColor(Color.WHITE);
            }

            // TODO, remember, Y must be flipped around an axis
            graphics.fillRect((int) player.getX() - 15, (int)player.getY() - 15, 30, 30);
        });
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
    public void addInput(String input) {
        inputManager.addToQueue(input);
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
    public List<String> getAndClearInputs() {
        return inputManager.getUnhandledCodes();
    }
}
