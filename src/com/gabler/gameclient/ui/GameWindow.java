package com.gabler.gameclient.ui;

import com.gabler.game.model.server.BoundingBoxBorder;
import com.gabler.game.model.server.GameState;
import com.gabler.game.model.server.Player;
import com.gabler.gameclient.engine.IClientInputSupplier;
import com.gabler.gameclient.engine.IGameStateRenderer;
import com.gabler.gameclient.engine.IRendererPresetup;
import com.gabler.gameclient.ui.keyboard.KeyBoardListener;
import com.gabler.gameclient.ui.render.player.PlayerAnimationController;
import lombok.Getter;
import lombok.Setter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
    private static final int PLAYER_SIZE = 64;

    private PlayerAnimationController mainPlayerAnimationController = null;
    private List<PlayerAnimationController> playerAnimationControllers = new ArrayList<>();

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

        // Check if the game state is loaded in (that is, has the server acked us, if not, blue screen)
        final GameState state = latestGameState;
        if (state == null) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, width, height);
            return;
        }

        // Put an obnoxious color in the background so its obvious if render has gone wrong or missed a spot
        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, width, height);

        // Precompute some variables like current player to reduce operation time for complex operations
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

        // Draw the background
        double backgroundWidth = (double)maxPlayerX + (double)width * 2;
        double backgroundHeight = (double)maxPlayerY + (double)height * 2;
        int backGroundX = (int)(-playerX - width);
        int backGroundY = (int)(-transformedPlayerY - height);
        graphics.drawImage(background, backGroundX, backGroundY, (int)backgroundWidth, (int)backgroundHeight, this);

        final Player currentPlayer = player;
        final long finalPlayerX = playerX;
        final long finalPlayerY = playerY;

        // Render players
        state.getPlayers().forEach(playerToRender -> {
            if (playerToRender == currentPlayer) {
                if (mainPlayerAnimationController == null) {
                    mainPlayerAnimationController = new PlayerAnimationController(playerToRender);
                }

                final BufferedImage sprite = mainPlayerAnimationController.nextSprite(state, playerToRender);

                AffineTransform transform = new AffineTransform();
                transform.translate(width / 2, height / 2);
                transform.rotate((playerToRender.getAngle() * -1) + Math.PI / 2);
                transform.translate(-32, -32);

                ((Graphics2D)graphics).drawImage(sprite, transform, this);
            } else {
                final BufferedImage sprite = getOrCreateAnimationControllerForPlayer(playerToRender).nextSprite(state, playerToRender);

                int xOffset = (int)(playerToRender.getX() - finalPlayerX);
                int yOffset = (int)(playerToRender.getY() - finalPlayerY);

                AffineTransform transform = new AffineTransform();
                transform.translate(width / 2 + xOffset, height / 2 - yOffset);
                transform.rotate((playerToRender.getAngle() * -1) + Math.PI / 2);
                transform.translate(-32, -32);

                ((Graphics2D)graphics).drawImage(sprite, transform, this);
            }
        });

        graphics.setColor(Color.GREEN);
        graphics.drawRect((int) (playerX * -1) + (width / 2), (int) (playerY - maxPlayerY) + (height / 2), (int)maxPlayerX, (int)maxPlayerY);
    }

    /**
     * Get or create animation controller for a player that is not the client player.
     *
     * @param player The player
     * @return The player's animation controller, whether newly created or old
     */
    private PlayerAnimationController getOrCreateAnimationControllerForPlayer(Player player) {
        return playerAnimationControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsAnimatedEntity(player))
            .findFirst()
            .orElseGet(() -> {
                final PlayerAnimationController newController = new PlayerAnimationController(player);
                playerAnimationControllers.add(newController);
                return newController;
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
