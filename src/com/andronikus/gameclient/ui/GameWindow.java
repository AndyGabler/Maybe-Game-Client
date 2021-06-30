package com.andronikus.gameclient.ui;

import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameclient.engine.IClientInputSupplier;
import com.andronikus.gameclient.engine.IGameStateRenderer;
import com.andronikus.gameclient.engine.IRendererPresetup;
import com.andronikus.gameclient.ui.keyboard.KeyBoardListener;
import com.andronikus.gameclient.ui.render.laser.LaserAnimationController;
import com.andronikus.gameclient.ui.render.player.PlayerAnimationController;
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
 * @author Andronikus
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
    private static final int LASER_WIDTH = 48;
    private static final int LASER_HEIGHT = 32;

    private PlayerAnimationController mainPlayerAnimationController = null;
    private final List<PlayerAnimationController> playerAnimationControllers = new ArrayList<>();
    private final List<LaserAnimationController> laserAnimationControllers = new ArrayList<>();

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

        // Precompute some variables like current player to reduce operation time for complex operations
        final Optional<Player> currentPlayerOpt = state.getPlayers().stream().filter(player1 -> player1.getSessionId().equals(sessionId)).findFirst();
        if (currentPlayerOpt.isEmpty()) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, width, height);
            return;
        }

        // Put an obnoxious color in the background so its obvious if render has gone wrong or missed a spot
        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, width, height);

        final Player player = currentPlayerOpt.get();
        final long playerX = player.getX();
        final long playerY = player.getY();
        final long maxPlayerX = ((BoundingBoxBorder) state.getBorder()).getMaxX();
        final long maxPlayerY = ((BoundingBoxBorder) state.getBorder()).getMaxY();

        // Draw the background
        final long transformedPlayerY = maxPlayerY - playerY;
        final double backgroundWidth = (double)maxPlayerX + (double)width * 2;
        final double backgroundHeight = (double)maxPlayerY + (double)height * 2;
        int backGroundX = (int)(-playerX - width);
        int backGroundY = (int)(-transformedPlayerY - height);
        graphics.drawImage(background, backGroundX, backGroundY, (int)backgroundWidth, (int)backgroundHeight, this);

        // Render lasers that hit something
        state.getLasers().forEach(laser -> {
            if (laser.getXVelocity() != 0 || laser.getYVelocity() != 0) {
                final BufferedImage sprite = getOrCreateAnimationControllerForLaser(laser).nextSprite(state, laser);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, laser.getX(), laser.getY(),
                    LASER_WIDTH, LASER_HEIGHT, laser.getAngle(), playerX, playerY
                );
            }
        });

        // TODO remove me. This is for debugging purposes.
        graphics.setColor(new Color(50, 255, 200));
        graphics.drawString("  Boosting Charge: " + player.getBoostingCharge(), 50, 250);
        graphics.drawString("Boosting Recharge: " + player.getBoostingRecharge(), 50, 275);
        graphics.drawString("           Health: " + player.getHealth(), 50, 300);
        graphics.drawString("     Shield Count: " + player.getShieldCount(), 50, 325);
        graphics.drawString("  Shield Recharge: " + player.getShieldRecharge(), 50, 350);
        graphics.drawString("   Laser Recharge: " + player.getLaserRecharge(), 50, 375);
        graphics.drawString("    Laser Charges: " + player.getLaserCharges(), 50, 400);

        // Render players
        state.getPlayers().forEach(playerToRender -> {
            if (playerToRender == player) {
                if (mainPlayerAnimationController == null) {
                    mainPlayerAnimationController = new PlayerAnimationController(playerToRender);
                }

                final BufferedImage sprite = mainPlayerAnimationController.nextSprite(state, playerToRender);

                final AffineTransform transform = new AffineTransform();
                transform.translate(width / 2, height / 2);
                transform.rotate((playerToRender.getAngle() * -1) + Math.PI / 2);
                transform.translate(-PLAYER_SIZE / 2, -PLAYER_SIZE / 2);

                ((Graphics2D)graphics).drawImage(sprite, transform, this);
            } else {
                final BufferedImage sprite = getOrCreateAnimationControllerForPlayer(playerToRender).nextSprite(state, playerToRender);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, playerToRender.getX(), playerToRender.getY(),
                    PLAYER_SIZE, PLAYER_SIZE, playerToRender.getAngle(), playerX, playerY
                );
            }
        });

        // Render traveling lasers
        state.getLasers().forEach(laser -> {
            if (laser.getXVelocity() == 0 && laser.getYVelocity() == 0) {
                final BufferedImage sprite = getOrCreateAnimationControllerForLaser(laser).nextSprite(state, laser);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, laser.getX(), laser.getY(),
                    LASER_WIDTH, LASER_HEIGHT, laser.getAngle(), playerX, playerY
                );
            }
        });

        graphics.setColor(Color.GREEN);
        graphics.drawRect((int) (playerX * -1) + (width / 2), (int) (playerY - maxPlayerY) + (height / 2), (int)maxPlayerX, (int)maxPlayerY);
    }

    /**
     * Render object when its position is relative to the player. This should be most objects since the player is the
     * center of attention.
     *
     * @param graphics The graphics
     * @param sprite The sprite being rendered
     * @param x The absolute X location, the relative coordinate will be calculated within this method
     * @param y The absolute Y location, the relative coordinate will be calculated within this method
     * @param renderWidth The width of the render
     * @param renderHeight The height of the render
     * @param angle The angle of the render
     * @param playerX X position the main player is at
     * @param playerY Y position the main player is at
     */
    private void renderObjectRelativeToMainPlayer(
        Graphics graphics, BufferedImage sprite,
        long x, long y, int renderWidth, int renderHeight, double angle,
        long playerX, long playerY
    ) {
        final int xOffset = (int)(x - playerX);
        final int yOffset = (int)(y - playerY);

        final AffineTransform transform = new AffineTransform();
        transform.translate(this.width / 2 + xOffset, this.height / 2 - yOffset);
        transform.rotate((angle * -1) + Math.PI / 2);
        transform.translate(-(renderWidth / 2), -(renderHeight / 2));

        ((Graphics2D)graphics).drawImage(sprite, transform, this);
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
     * Get or create animation controller for a laser.
     *
     * @param laser The laser
     * @return The laser's animation controller, whether newly created or old
     */
    private LaserAnimationController getOrCreateAnimationControllerForLaser(Laser laser) {
        return laserAnimationControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsAnimatedEntity(laser))
            .findFirst()
            .orElseGet(() -> {
                final LaserAnimationController newController = new LaserAnimationController(laser);
                laserAnimationControllers.add(newController);
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
