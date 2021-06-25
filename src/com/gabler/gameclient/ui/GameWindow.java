package com.gabler.gameclient.ui;

import com.gabler.game.model.server.BoundingBoxBorder;
import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.IBorder;
import com.gabler.game.model.server.Player;
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
import java.awt.Image;
import java.awt.Graphics;
import java.util.List;
import java.util.Optional;

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

    private final Image background;
    private static final double BACKGROUND_WIDTH = 5500;
    private static final double BACKGROUND_HEIGHT = 3310;
    private static final double BACKGROUND_OFFSET = 30;

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

        background = ImagesUtil.getImage("background.png");
        inputManager = new ConcurrentInputManager();
    }

    /**
     * Graphical hook.
     *
     * @param graphics The graphics to draw on.
     */
    public void paintComponent(Graphics graphics) {

        final GameState state = latestGameState;
        if (state == null) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, width, height);
            return;
        }
        graphics.setColor(Color.CYAN);
        graphics.fillRect(0, 0, width, height);

        final Optional<Player> currentPlayerOpt = state.getPlayers().stream().filter(player1 -> player1.getSessionId().equals(sessionId)).findFirst();
        Player player = null;
        long playerX = 0;
        long playerY = 0;
        long maxPlayerX = ((BoundingBoxBorder) state.getBorder()).getMaxX();
        long maxPlayerY = ((BoundingBoxBorder) state.getBorder()).getMaxY();

        if (currentPlayerOpt.isPresent()) {
            player = currentPlayerOpt.get();
            playerX = player.getX();
            playerY = player.getY();
        }
        long transformedPlayerY = maxPlayerY - playerY;

        double backgroundWidth = (double)maxPlayerX + (double)width * 2;
        double backgroundHeight = (double)maxPlayerY + (double)height * 2;
        int backGroundX = (int)(-playerX - width);
        int backGroundY = (int)(-transformedPlayerY - height);
        graphics.drawImage(background, backGroundX, backGroundY, (int)backgroundWidth, (int)backgroundHeight, this);

        final Player currentPlayer = player;
        final long finalPlayerX = playerX;
        final long finalPlayerY = playerY;
        state.getPlayers().forEach(playerToRender -> {
            if (playerToRender == currentPlayer) {
                graphics.setColor(Color.RED);
                graphics.fillRect(width / 2 - 12, height / 2 - 12, 24, 24);
            } else {
                graphics.setColor(Color.WHITE);
                int xOffset = (int)(playerToRender.getX() - finalPlayerX);
                int yOffset = (int)(playerToRender.getY() - finalPlayerY);
                graphics.fillRect(width / 2 - 12 + xOffset, height / 2 - 12 - yOffset, 24, 24);
            }
        });

        graphics.setColor(Color.GREEN);
        graphics.drawRect((int) (playerX * -1) + (width / 2), (int) (playerY - maxPlayerY) + (height / 2), (int)maxPlayerX, (int)maxPlayerY);
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
