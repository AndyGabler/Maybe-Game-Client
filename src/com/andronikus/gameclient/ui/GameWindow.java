package com.andronikus.gameclient.ui;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.BoundingBoxBorder;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;
import com.andronikus.game.model.server.MicroBlackHole;
import com.andronikus.game.model.server.Player;
import com.andronikus.game.model.server.Portal;
import com.andronikus.game.model.server.Snake;
import com.andronikus.gameclient.engine.IClientInputSupplier;
import com.andronikus.gameclient.engine.IGameStateRenderer;
import com.andronikus.gameclient.engine.IRendererPresetup;
import com.andronikus.gameclient.ui.input.ClientInput;
import com.andronikus.gameclient.ui.input.IUserInput;
import com.andronikus.gameclient.ui.input.ServerInput;
import com.andronikus.gameclient.ui.keyboard.KeyBoardListener;
import com.andronikus.gameclient.ui.render.asteroid.LargeAsteroidStopMotionController;
import com.andronikus.gameclient.ui.render.asteroid.SmallAsteroidStopMotionController;
import com.andronikus.gameclient.ui.render.blackhole.MicroBlackHoleStopMotionController;
import com.andronikus.gameclient.ui.render.hud.HudRenderer;
import com.andronikus.gameclient.ui.render.hud.TrackerSpriteSheet;
import com.andronikus.gameclient.ui.render.laser.LaserAnimationController;
import com.andronikus.gameclient.ui.render.player.PlayerStopMotionController;
import com.andronikus.gameclient.ui.render.portal.PortalStopMotionController;
import com.andronikus.gameclient.ui.render.snake.SnakeStopMotionController;
import lombok.Getter;
import lombok.Setter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Font;
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
    private final ConcurrentInputManager serverInputManager;

    private final Image background;
    private static final int PLAYER_SIZE = 64;
    private static final int LASER_WIDTH = 48;
    private static final int LASER_HEIGHT = 32;
    private static final int SMALL_ASTEROID_SIZE = 64;
    private static final int LARGE_ASTEROID_WIDTH = 96;
    private static final int LARGE_ASTEROID_HEIGHT = 192;
    private static final int SNAKE_WIDTH = 16;
    private static final int SNAKE_HEIGHT = 64;
    private static final int PORTAL_SIZE = 64;
    private static final Color COMMAND_TEXT_COLOR = new Color(94, 222, 52);
    private static final Font COMMAND_TEXT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);

    private PlayerStopMotionController mainPlayerStopMotionController = null;
    private final List<PlayerStopMotionController> playerStopMotionControllers = new ArrayList<>();
    private final List<LaserAnimationController> laserAnimationControllers = new ArrayList<>();
    private final List<SmallAsteroidStopMotionController> smallAsteroidStopMotionControllers = new ArrayList<>();
    private final List<LargeAsteroidStopMotionController> largeAsteroidStopMotionControllers = new ArrayList<>();
    private final List<SnakeStopMotionController> snakeStopMotionControllers = new ArrayList<>();
    private final List<PortalStopMotionController> portalStopMotionControllers = new ArrayList<>();
    private final List<MicroBlackHoleStopMotionController> blackHoleStopMotionControllers = new ArrayList<>();

    private final HudRenderer hudRenderer = new HudRenderer();
    private final TrackerSpriteSheet trackerSpriteSheet = new TrackerSpriteSheet();

    @Getter
    private volatile boolean commandMode;
    private volatile String commandBuffer = "";
    private volatile boolean commandLocked = false;
    private int commandCarrotTickCount = 0;
    private boolean commandCarrotToggle = false;

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

        background = ImagesUtil.getImage("background.png");
        serverInputManager = new ConcurrentInputManager();
    }

    /**
     * Graphical hook.
     *
     * @param graphics The graphics to draw on.
     */
    public void paintComponent(Graphics graphics) {
        // TODO generify iterative animations

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

        state.getBlackHoles().forEach(blackHole -> {
            final BufferedImage sprite = getOrCreateAnimationControllerForBlackHole(blackHole).nextSprite(state, blackHole);

            renderObjectRelativeToMainPlayer(
                graphics, sprite, blackHole.getX(), blackHole.getY(),
                PORTAL_SIZE, PORTAL_SIZE, blackHole.getAngle(), playerX, playerY
            );
        });

        state.getPortals().forEach(portal -> {
            final BufferedImage sprite = getOrCreateAnimationControllerForPortal(portal).nextSprite(state, portal);

            renderObjectRelativeToMainPlayer(
                graphics, sprite, portal.getX(), portal.getY(),
                PORTAL_SIZE, PORTAL_SIZE, portal.getAngle(), playerX, playerY
            );
        });

        // Render players
        state.getPlayers().forEach(playerToRender -> {
            if (playerToRender == player) {
                if (mainPlayerStopMotionController == null) {
                    mainPlayerStopMotionController = new PlayerStopMotionController(playerToRender);
                }

                final BufferedImage sprite = mainPlayerStopMotionController.nextSprite(state, playerToRender);

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

                final BufferedImage tracker = trackerSpriteSheet.getTrackerSpriteForColor(playerToRender.getColor());
                final long yDiff = playerToRender.getY() - player.getY();
                final long xDiff = playerToRender.getX() - player.getX();

                if (!playerToRender.isDead()) {
                    if (Math.abs(yDiff) > height / 2 || Math.abs(xDiff) > width / 2) {
                        // Render tracker for player outside of visible range
                        drawTrackerForOffScreenPlayer(graphics, tracker, (double) xDiff, (double) yDiff);
                    } else {
                        // Render tracker for player that is within visible range
                        renderObjectRelativeToMainPlayer(
                            graphics, tracker, playerToRender.getX(), playerToRender.getY() + PLAYER_SIZE,
                            PLAYER_SIZE / 2, PLAYER_SIZE / 2, Math.PI / 2 * 3, playerX, playerY
                        );
                    }
                }
            }
        });

        // Render snakes
        state.getSnakes().forEach(snake -> {
            final BufferedImage sprite = getOrCreateAnimationControllerForSnake(snake).nextSprite(state, snake);
            renderObjectRelativeToMainPlayer(
                graphics, sprite, snake.getX(), snake.getY(), SNAKE_WIDTH, SNAKE_HEIGHT, snake.getAngle(), playerX, playerY
            );
        });

        // Render asteroids
        state.getAsteroids().forEach(asteroid -> {
            if (asteroid.getSize() == 0) {
                final BufferedImage sprite = getOrCreateAnimationControllerForSmallAsteroid(asteroid).nextSprite(state, asteroid);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, asteroid.getX(), asteroid.getY(),
                    SMALL_ASTEROID_SIZE, SMALL_ASTEROID_SIZE, asteroid.getAngle(), playerX, playerY
                );
            } else {
                final BufferedImage sprite = getOrCreateAnimationControllerForLargeAsteroid(asteroid).nextSprite(state, asteroid);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, asteroid.getX(), asteroid.getY(),
                    LARGE_ASTEROID_WIDTH, LARGE_ASTEROID_HEIGHT, asteroid.getAngle(), playerX, playerY, true
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

        hudRenderer.drawHud(
            graphics, player.getHealth(), player.getShieldCount(), player.getShieldRecharge(),
            player.getBoostingCharge(), player.getBoostingRecharge(), player.getLaserCharges(), this
        );
        if (commandMode) {
            commandCarrotTickCount = (commandCarrotTickCount + 1) % 13;
            if (commandCarrotTickCount == 0) {
                commandCarrotToggle = !commandCarrotToggle;
            }
            String carrot = commandCarrotToggle ? "|" : "";
            graphics.setColor(COMMAND_TEXT_COLOR);
            graphics.setFont(COMMAND_TEXT_FONT);
            graphics.drawString("ENTER COMMAND:", 60, 15);
            graphics.drawString(commandBuffer + carrot, 60, 33);
        }
    }

    /**
     * Draw a tracker for a player that is off the screen.
     *
     * @param graphics The graphics
     * @param trackerSprite The sprite used to track the player
     * @param xDiff The difference in X location from the client player
     * @param yDiff The difference in Y location from the client player
     */
    private void drawTrackerForOffScreenPlayer(Graphics graphics, BufferedImage trackerSprite, double xDiff, double yDiff) {
        final double screenHypotenuse = Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));
        final double thetaBr = Math.acos((double)width / screenHypotenuse);
        final double thetaBl = Math.PI - thetaBr;
        final double thetaTl = Math.PI + thetaBr;

        final double distanceFromOtherPlayer = Math.sqrt(Math.pow(yDiff, 2) + Math.pow(xDiff, 2));
        final double phi = Math.acos(xDiff / distanceFromOtherPlayer);

        int xDrawLocation = 0;
        int yDrawLocation = 0;

        if (thetaBl > phi && phi >= thetaBr) {
            double yLocationFromPlayer;
            int yOffset = 0;
            double xLocationFromPlayer = 0;

            if (yDiff > 0) {
                // dY = height/2 ON TOP
                yLocationFromPlayer = (double)height / -2;
                if (xDiff != 0) {
                    xLocationFromPlayer = -yLocationFromPlayer / Math.tan(phi);
                }
            } else {
                // dY = -height/2 ON BOTTOM
                yLocationFromPlayer = (double)height / 2;
                yOffset = -32;
                if (xDiff != 0) {
                    xLocationFromPlayer = yLocationFromPlayer / Math.tan(phi);
                }
            }

            xDrawLocation = (int)(xLocationFromPlayer + width / 2 - 16);
            yDrawLocation = (int)(yLocationFromPlayer + height / 2 + yOffset);
        } else if (thetaTl > phi && phi >= thetaBl) {
            // dX = -width/2 ON LEFT
            double xLocationFromPlayer = -(double)width / 2;
            double yLocationFromPlayer = Math.tan(phi) * -xLocationFromPlayer;

            if (yDiff != 0) {
                yLocationFromPlayer *= (yDiff / Math.abs(yDiff));
            }

            xDrawLocation = (int)xLocationFromPlayer + width / 2;
            yDrawLocation = (int)yLocationFromPlayer + height / 2 - 16;
        } else {
            // dX = width/2 ON RIGHT
            double xLocationFromPlayer = (double)width / 2;
            double yLocationFromPlayer = Math.tan(phi) * -xLocationFromPlayer;

            if (yDiff != 0) {
                yLocationFromPlayer *= (yDiff / Math.abs(yDiff));
            }

            xDrawLocation = (int)xLocationFromPlayer + width / 2 - 32;
            yDrawLocation = (int)yLocationFromPlayer + height / 2 - 16;
        }

        final AffineTransform transform = new AffineTransform();
        transform.translate(xDrawLocation + 16, yDrawLocation + 16);
        transform.rotate((phi * -1) + Math.PI / 2);
        transform.translate(-(32 / 2), -(32 / 2));

        ((Graphics2D)graphics).drawImage(trackerSprite, transform, this);
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
        renderObjectRelativeToMainPlayer(graphics, sprite, x, y, renderWidth, renderHeight, angle, playerX, playerY, false);
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
     * @param skipForceRotate Skip the step where the client forcefully rotates object to fit sprite specs
     */
    private void renderObjectRelativeToMainPlayer(
        Graphics graphics, BufferedImage sprite,
        long x, long y, int renderWidth, int renderHeight, double angle,
        long playerX, long playerY, boolean skipForceRotate
    ) {
        final int xOffset = (int)(x - playerX);
        final int yOffset = (int)(y - playerY);

        final AffineTransform transform = new AffineTransform();
        transform.translate(this.width / 2 + xOffset, this.height / 2 - yOffset);

        double spriteFitOffset = Math.PI / 2;
        if (skipForceRotate) {
            spriteFitOffset = 0;
        }

        transform.rotate((angle * -1) + spriteFitOffset);
        transform.translate(-(renderWidth / 2), -(renderHeight / 2));

        ((Graphics2D)graphics).drawImage(sprite, transform, this);
    }

    /**
     * Get or create animation controller for a snake.
     *
     * @param snake The snake
     * @return The snake's animation controller, whether newly created or old
     */
    private SnakeStopMotionController getOrCreateAnimationControllerForSnake(Snake snake) {
        return snakeStopMotionControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsRoot(snake))
            .findFirst()
            .orElseGet(() -> {
                final SnakeStopMotionController newController = new SnakeStopMotionController(snake);
                snakeStopMotionControllers.add(newController);
                return newController;
            });
    }

    /**
     * Get or create animation controller for a player that is not the client player.
     *
     * @param player The player
     * @return The player's animation controller, whether newly created or old
     */
    private PlayerStopMotionController getOrCreateAnimationControllerForPlayer(Player player) {
        return playerStopMotionControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsRoot(player))
            .findFirst()
            .orElseGet(() -> {
                final PlayerStopMotionController newController = new PlayerStopMotionController(player);
                playerStopMotionControllers.add(newController);
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
            .filter(controller -> controller.checkIfObjectIsRoot(laser))
            .findFirst()
            .orElseGet(() -> {
                final LaserAnimationController newController = new LaserAnimationController(laser);
                laserAnimationControllers.add(newController);
                return newController;
            });
    }

    /**
     * Get or create animation controller for a small asteroid.
     *
     * @param asteroid The asteroid
     * @return The asteroid's animation controller, whether newly created or old
     */
    private SmallAsteroidStopMotionController getOrCreateAnimationControllerForSmallAsteroid(Asteroid asteroid) {
        return smallAsteroidStopMotionControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsRoot(asteroid))
            .findFirst()
            .orElseGet(() -> {
                final SmallAsteroidStopMotionController newController = new SmallAsteroidStopMotionController(asteroid);
                smallAsteroidStopMotionControllers.add(newController);
                return newController;
            });
    }

    /**
     * Get or create animation controller for a large asteroid.
     *
     * @param asteroid The asteroid
     * @return The asteroid's animation controller, whether newly created or old
     */
    private LargeAsteroidStopMotionController getOrCreateAnimationControllerForLargeAsteroid(Asteroid asteroid) {
        return largeAsteroidStopMotionControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsRoot(asteroid))
            .findFirst()
            .orElseGet(() -> {
                final LargeAsteroidStopMotionController newController = new LargeAsteroidStopMotionController(asteroid);
                largeAsteroidStopMotionControllers.add(newController);
                return newController;
            });
    }

    /**
     * Get or create animation controller for a black hole.
     *
     * @param blackHole The black hole
     * @return The black hole's animation controller, whether newly created or old
     */
    private MicroBlackHoleStopMotionController getOrCreateAnimationControllerForBlackHole(MicroBlackHole blackHole) {
        return blackHoleStopMotionControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsRoot(blackHole))
            .findFirst()
            .orElseGet(() -> {
                final MicroBlackHoleStopMotionController newController = new MicroBlackHoleStopMotionController(blackHole);
                blackHoleStopMotionControllers.add(newController);
                return newController;
            });
    }

    /**
     * Get or create animation controller for a portal.
     *
     * @param portal The portal
     * @return The portal's animation controller, whether newly created or old
     */
    private PortalStopMotionController getOrCreateAnimationControllerForPortal(Portal portal) {
        return portalStopMotionControllers
            .stream()
            .filter(controller -> controller.checkIfObjectIsRoot(portal))
            .findFirst()
            .orElseGet(() -> {
                final PortalStopMotionController newController = new PortalStopMotionController(portal);
                portalStopMotionControllers.add(newController);
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
    public void addInput(IUserInput input) {
        if (input instanceof ServerInput) {
            serverInputManager.addToQueue(((ServerInput) input).getCode());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAndClearInputs() {
        return serverInputManager.getUnhandledCodes();
    }
}
