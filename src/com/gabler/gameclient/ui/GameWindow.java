package com.gabler.gameclient.ui;

import com.gabler.game.model.server.GameState;
import com.gabler.gameclient.engine.IClientInputSupplier;
import com.gabler.gameclient.engine.IGameStateRenderer;
import com.gabler.udpmanager.ResourceLock;
import lombok.Getter;
import lombok.Setter;

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
 * @author Andy Gabler
 */
public class GameWindow extends JPanel implements IGameStateRenderer, IClientInputSupplier {

    @Getter
    @Setter
    private volatile int width;

    @Getter
    @Setter
    private volatile int height;
    private final JFrame frame;
    private volatile GameState latestGameState = null;
    private final ResourceLock<ArrayList<String>> inputCodes;

    /**
     * Instantiate the graphical user interface for a game.
     */
    public GameWindow() {
        frame = new JFrame("Maybe Game Client"); // TODO cleverly title

        final MouseListenerImpl mouseListener = new MouseListenerImpl(this);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
        this.addKeyListener(new KeyBoardListener(this));
        this.addComponentListener(new ResizeListener(this));

        frame.add(this);
        frame.setIconImage(ImagesUtil.getImage("icon.png"));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(600, 400));

        width = (int) frame.getSize().getWidth();
        height = (int) frame.getSize().getHeight();

        inputCodes = new ResourceLock<>(new ArrayList<>());
    }

    /**
     * Graphical hook.
     *
     * @param graphics The graphics to draw on.
     */
    public void paintComponent(Graphics graphics) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GameState toRender) {
        // Do not repaint off of the word of the server thread. Only a Java AWT thread can update this.
        latestGameState = toRender;
    }

    /**
     * Add an input.
     *
     * @param input The input code
     */
    public void addInput(String input) {
        inputCodes.performRunInLock(codes -> {
            if (!codes.contains(input)) {
                codes.add(input);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAndClearInputs() {
        final List<String> inputs = inputCodes.performRunInLock(codes -> {
            final List<String> inputCodeCopy = new ArrayList<>(codes);
            codes.clear();
            return inputCodeCopy;
        });
        return inputs;
    }
}
