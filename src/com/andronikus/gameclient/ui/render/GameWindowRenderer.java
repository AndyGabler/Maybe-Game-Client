package com.andronikus.gameclient.ui.render;

import com.andronikus.game.model.server.*;
import com.andronikus.gameclient.ui.GameWindow;
import com.andronikus.gameclient.ui.RenderRatio;
import com.andronikus.gameclient.ui.render.asteroid.LargeAsteroidStopMotionController;
import com.andronikus.gameclient.ui.render.asteroid.SmallAsteroidStopMotionController;
import com.andronikus.gameclient.ui.render.background.BackgroundRenderer;
import com.andronikus.gameclient.ui.render.blackhole.MicroBlackHoleStopMotionController;
import com.andronikus.gameclient.ui.render.hud.HudRenderer;
import com.andronikus.gameclient.ui.render.hud.TrackerSpriteSheet;
import com.andronikus.gameclient.ui.render.laser.LaserAnimationController;
import com.andronikus.gameclient.ui.render.player.PlayerStopMotionController;
import com.andronikus.gameclient.ui.render.portal.PortalStopMotionController;
import com.andronikus.gameclient.ui.render.snake.SnakeStopMotionController;
import lombok.Getter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Steps related to a {@link GameWindow} where the actual game is rendered onto the screen.
 *
 * @author Andronikus
 */
public class GameWindowRenderer {

    @Getter
    private final GameWindow window;

    @Getter
    private volatile RenderRatio renderRatio;
    private final BackgroundRenderer backgroundRenderer;
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
    private static final Color GAME_SETTING_OFF_TEXT_COLOR = new Color(238, 60, 60);
    private static final Color GAME_SETTING_ON_TEXT_COLOR = new Color(80, 255, 113);
    private static final Font GAME_SETTING_TEXT_FONT = new Font(Font.DIALOG, Font.PLAIN, 14);

    private static final Color ADVANCED_HUD_TEXT_COLOR = new Color(190, 76, 167);
    private static final Font ADVANCED_HUD_TEXT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 26);

    private static final Color COLLISION_MARKER_COLOR = new Color(239, 58, 58, 136);

    private PlayerStopMotionController mainPlayerStopMotionController = null;
    private final java.util.List<PlayerStopMotionController> playerStopMotionControllers = new ArrayList<>();
    private final java.util.List<LaserAnimationController> laserAnimationControllers = new ArrayList<>();
    private final java.util.List<SmallAsteroidStopMotionController> smallAsteroidStopMotionControllers = new ArrayList<>();
    private final java.util.List<LargeAsteroidStopMotionController> largeAsteroidStopMotionControllers = new ArrayList<>();
    private final java.util.List<SnakeStopMotionController> snakeStopMotionControllers = new ArrayList<>();
    private final java.util.List<PortalStopMotionController> portalStopMotionControllers = new ArrayList<>();
    private final List<MicroBlackHoleStopMotionController> blackHoleStopMotionControllers = new ArrayList<>();

    private final HudRenderer hudRenderer = new HudRenderer();
    private final TrackerSpriteSheet trackerSpriteSheet = new TrackerSpriteSheet();

    private int commandCarrotTickCount = 0;
    private boolean commandCarrotToggle = false;

    public GameWindowRenderer(GameWindow aWindow, RenderRatio aRenderRatio) {
        window = aWindow;
        backgroundRenderer = new BackgroundRenderer("background.png");
        renderRatio = aRenderRatio;
    }

    public void render(Graphics graphics, GameState state, String mainPlayerSessionId) {
        // TODO generify iterative animations
        renderRatio = window.getCandidateRenderRatio().copy();

        // Check if the game state is loaded in (that is, has the server acked us, if not, blue screen)
        final int width = window.getWidth();
        final int height = window.getHeight();
        if (state == null) {
            graphics.setColor(Color.BLUE);
            graphics.fillRect(0, 0, width, height);
            return;
        }

        // Precompute some variables like current player to reduce operation time for complex operations
        final Optional<Player> currentPlayerOpt = state.getPlayers().stream().filter(player1 -> player1.getSessionId().equals(mainPlayerSessionId)).findFirst();
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
        backgroundRenderer.render(graphics, player, (BoundingBoxBorder) state.getBorder(), this);

        // Render lasers that hit something
        state.getLasers().forEach(laser -> {
            if (laser.getXVelocity() != 0 || laser.getYVelocity() != 0) {
                final BufferedImage sprite = getOrCreateAnimationControllerForLaser(laser).nextSprite(state, laser);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, state, mainPlayerSessionId, laser.getX(), laser.getY(),
                    LASER_WIDTH, LASER_HEIGHT, laser.getAngle(), playerX, playerY, laser.getId(), laser.moveableTag()
                );
            }
        });

        state.getBlackHoles().forEach(blackHole -> {
            final BufferedImage sprite = getOrCreateAnimationControllerForBlackHole(blackHole).nextSprite(state, blackHole);

            renderObjectRelativeToMainPlayer(
                graphics, sprite, state, mainPlayerSessionId, blackHole.getX(), blackHole.getY(),
                PORTAL_SIZE, PORTAL_SIZE, blackHole.getAngle(), playerX, playerY, blackHole.getId(), blackHole.moveableTag()
            );
        });

        state.getPortals().forEach(portal -> {
            final BufferedImage sprite = getOrCreateAnimationControllerForPortal(portal).nextSprite(state, portal);

            renderObjectRelativeToMainPlayer(
                graphics, sprite, state, mainPlayerSessionId, portal.getX(), portal.getY(),
                PORTAL_SIZE, PORTAL_SIZE, portal.getAngle(), playerX, playerY, portal.getId(), portal.moveableTag()
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
                transform.translate(-(renderRatio.getWidthScale() * (double) PLAYER_SIZE) / 2, -(renderRatio.getHeightScale() * (double) PLAYER_SIZE / 2));
                transform.scale(renderRatio.getWidthScale(), renderRatio.getHeightScale());

                ((Graphics2D)graphics).drawImage(sprite, transform, window);

                if (window.isAdvancedHudEnabled()) {
                    int hudY = height / 2 - player.getBoxHeight() / 2;
                    graphics.setColor(ADVANCED_HUD_TEXT_COLOR);
                    graphics.setFont(ADVANCED_HUD_TEXT_FONT);
                    graphics.drawString("(" + player.getX() + ", " + player.getY() + ")", width / 2, hudY);
                    hudY += 30;
                    String displayAngle = String.format("%.2f", Math.toDegrees(player.getAngle() % (Math.PI * 2)));
                    graphics.drawString("Angle: " + displayAngle, width / 2, hudY);
                }
                if (window.isCollisionWatch()) {
                    graphics.setColor(Color.CYAN);
                    final int playerBoxWidth = (int) (renderRatio.getWidthScale() * (double) player.getBoxWidth());
                    final int playerBoxHeight = (int) (renderRatio.getHeightScale() * (double) player.getBoxHeight());
                    final int collisionWatchX = width / 2 - playerBoxWidth / 2;
                    final int collisionWatchY = height / 2 - playerBoxHeight / 2;
                    graphics.drawRect(
                        collisionWatchX,
                        collisionWatchY,
                        playerBoxWidth,
                        playerBoxHeight
                    );

                    final boolean collisionFlag = state
                        .getDebugSettings()
                        .getPlayerCollisionFlags()
                        .stream()
                        .anyMatch(flag -> flag.getSessionId().equalsIgnoreCase(mainPlayerSessionId));
                    if (collisionFlag) {
                        graphics.setColor(COLLISION_MARKER_COLOR);
                        graphics.fillRect(
                            collisionWatchX,
                            collisionWatchY,
                            playerBoxWidth,
                            playerBoxHeight
                        );
                    }
                }
            } else {
                final BufferedImage sprite = getOrCreateAnimationControllerForPlayer(playerToRender).nextSprite(state, playerToRender);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, state, mainPlayerSessionId, playerToRender.getX(), playerToRender.getY(),
                    PLAYER_SIZE, PLAYER_SIZE, playerToRender.getAngle(), playerX, playerY, null, null
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
                            graphics, tracker, state, mainPlayerSessionId, playerToRender.getX(), playerToRender.getY() + PLAYER_SIZE,
                            PLAYER_SIZE / 2, PLAYER_SIZE / 2, Math.PI / 2 * 3, playerX, playerY, null, null
                        );
                    }
                }
            }
        });

        // Render snakes
        state.getSnakes().forEach(snake -> {
            final BufferedImage sprite = getOrCreateAnimationControllerForSnake(snake).nextSprite(state, snake);
            renderObjectRelativeToMainPlayer(
                graphics, sprite, state, mainPlayerSessionId, snake.getX(), snake.getY(), SNAKE_WIDTH, SNAKE_HEIGHT,
                snake.getAngle(), playerX, playerY, snake.getId(), snake.moveableTag()
            );
        });

        // Render asteroids
        state.getAsteroids().forEach(asteroid -> {
            if (asteroid.getSize() == 0) {
                final BufferedImage sprite = getOrCreateAnimationControllerForSmallAsteroid(asteroid).nextSprite(state, asteroid);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, state, mainPlayerSessionId, asteroid.getX(), asteroid.getY(),
                    SMALL_ASTEROID_SIZE, SMALL_ASTEROID_SIZE, asteroid.getAngle(), playerX, playerY, asteroid.getId(), asteroid.moveableTag()
                );
            } else {
                final BufferedImage sprite = getOrCreateAnimationControllerForLargeAsteroid(asteroid).nextSprite(state, asteroid);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, state, mainPlayerSessionId, asteroid.getX(), asteroid.getY(),
                    LARGE_ASTEROID_WIDTH, LARGE_ASTEROID_HEIGHT, asteroid.getAngle(), playerX, playerY, asteroid.getId(), asteroid.moveableTag(), true
                );
            }
        });

        // Render traveling lasers
        state.getLasers().forEach(laser -> {
            if (laser.getXVelocity() == 0 && laser.getYVelocity() == 0) {
                final BufferedImage sprite = getOrCreateAnimationControllerForLaser(laser).nextSprite(state, laser);

                renderObjectRelativeToMainPlayer(
                    graphics, sprite, state, mainPlayerSessionId, laser.getX(), laser.getY(),
                    LASER_WIDTH, LASER_HEIGHT, laser.getAngle(), playerX, playerY, laser.getId(), laser.moveableTag()
                );
            }
        });

        graphics.setColor(Color.GREEN);
        graphics.drawRect(
            (int) ((renderRatio.getWidthScale() * (double) playerX * -1) + ((double) width / 2)),
            (int) ((renderRatio.getHeightScale() * (double) (playerY - maxPlayerY)) + ((double) height / 2)),
            (int) (renderRatio.getWidthScale() * (double) maxPlayerX),
            (int) (renderRatio.getHeightScale() * (double) maxPlayerY)
        );

        hudRenderer.drawHud(
            graphics, player.getHealth(), player.getShieldCount(), player.getShieldRecharge(),
            player.getBoostingCharge(), player.getBoostingRecharge(), player.getLaserCharges(), window
        );

        if (window.isCommandMode()) {
            commandCarrotTickCount = (commandCarrotTickCount + 1) % 13;
            if (commandCarrotTickCount == 0) {
                commandCarrotToggle = !commandCarrotToggle;
            }
            String carrot = commandCarrotToggle ? "|" : "";
            graphics.setColor(COMMAND_TEXT_COLOR);
            graphics.setFont(COMMAND_TEXT_FONT);
            graphics.drawString("ENTER COMMAND:", 60, 15);
            graphics.drawString(window.getCommandBuffer() + carrot, 60, 33);
        }

        if (!state.isSpawningEnabled() ||
            !state.isMovementEnabled() ||
            !state.isCollisionsEnabled() ||
            !state.isTickEnabled()) {
            graphics.setFont(GAME_SETTING_TEXT_FONT);

            graphics.setColor(state.isTickEnabled() ? GAME_SETTING_ON_TEXT_COLOR : GAME_SETTING_OFF_TEXT_COLOR);
            graphics.drawString("Tick Enabled: " + state.isTickEnabled(), 14, height - 40);

            graphics.setColor(state.isMovementEnabled() ? GAME_SETTING_ON_TEXT_COLOR : GAME_SETTING_OFF_TEXT_COLOR);
            graphics.drawString("Movement Enabled: " + state.isMovementEnabled(), 14, height - 30);

            graphics.setColor(state.isCollisionsEnabled() ? GAME_SETTING_ON_TEXT_COLOR : GAME_SETTING_OFF_TEXT_COLOR);
            graphics.drawString("Collision Enabled: " + state.isCollisionsEnabled(), 14, height - 20);

            graphics.setColor(state.isSpawningEnabled() ? GAME_SETTING_ON_TEXT_COLOR : GAME_SETTING_OFF_TEXT_COLOR);
            graphics.drawString("Spawning Enabled: " + state.isSpawningEnabled(), 14, height - 10);
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
        final int width = window.getWidth();
        final int height = window.getHeight();

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

        ((Graphics2D)graphics).drawImage(trackerSprite, transform, window);
    }

    /**
     * Render object when its position is relative to the player. This should be most objects since the player is the
     * center of attention.
     *
     * @param graphics The graphics
     * @param sprite The sprite being rendered
     * @param state The game state
     * @param sessionId The session of the main player
     * @param x The absolute X location, the relative coordinate will be calculated within this method
     * @param y The absolute Y location, the relative coordinate will be calculated within this method
     * @param renderWidth The width of the render
     * @param renderHeight The height of the render
     * @param angle The angle of the render
     * @param playerX X position the main player is at
     * @param playerY Y position the main player is at
     * @param serverId ID of the entity according to the connected server
     * @param moveableTag Tag name of the type of moveable being rendered
     */
    private void renderObjectRelativeToMainPlayer(
        Graphics graphics, BufferedImage sprite, GameState state, String sessionId,
        long x, long y, int renderWidth, int renderHeight, double angle,
        long playerX, long playerY, Long serverId, String moveableTag
    ) {
        renderObjectRelativeToMainPlayer(graphics, sprite, state, sessionId, x, y, renderWidth, renderHeight, angle, playerX, playerY, serverId, moveableTag, false);
    }

    /**
     * Render object when its position is relative to the player. This should be most objects since the player is the
     * center of attention.
     *
     * @param graphics The graphics
     * @param sprite The sprite being rendered
     * @param state The game state
     * @param sessionId The session of the main player
     * @param x The absolute X location, the relative coordinate will be calculated within this method
     * @param y The absolute Y location, the relative coordinate will be calculated within this method
     * @param renderWidth The width of the render
     * @param renderHeight The height of the render
     * @param angle The angle of the render
     * @param playerX X position the main player is at
     * @param playerY Y position the main player is at
     * @param serverId ID of the entity according to the connected server
     * @param moveableTag Tag name of the type of moveable being rendered
     * @param skipForceRotate Skip the step where the client forcefully rotates object to fit sprite specs
     */
    private void renderObjectRelativeToMainPlayer(
            Graphics graphics, BufferedImage sprite, GameState state, String sessionId,
            long x, long y, int renderWidth, int renderHeight, double angle,
            long playerX, long playerY, Long serverId, String moveableTag,
            boolean skipForceRotate
    ) {
        final int windowWidth = window.getWidth();
        final int windowHeight = window.getHeight();

        renderWidth = (int) (renderRatio.getWidthScale() * (double) renderWidth);
        renderHeight = (int) (renderRatio.getHeightScale() * (double) renderHeight);

        final int xOffset = (int)(renderRatio.getWidthScale() * (double)(x - playerX));
        final int yOffset = (int)(renderRatio.getHeightScale() * (double)(y - playerY));

        final int drawingX = windowWidth / 2 + xOffset;
        final int drawingY = windowHeight / 2 - yOffset;
        final AffineTransform transform = new AffineTransform();
        transform.translate(drawingX, drawingY);

        double spriteFitOffset = Math.PI / 2;
        if (skipForceRotate) {
            spriteFitOffset = 0;
        }

        transform.rotate((angle * -1) + spriteFitOffset);
        transform.translate(-(renderWidth / 2), -(renderHeight / 2));
        transform.scale(renderRatio.getWidthScale(), renderRatio.getHeightScale());

        ((Graphics2D)graphics).drawImage(sprite, transform, window);

        if (window.isAdvancedHudEnabled()) {
            int hudY = drawingY;
            graphics.setColor(ADVANCED_HUD_TEXT_COLOR);
            graphics.setFont(ADVANCED_HUD_TEXT_FONT);
            graphics.drawString("ID: " + serverId, drawingX, hudY);
            hudY += 30;
            graphics.drawString("(" + x + ", " + y + ")", drawingX, hudY);
            hudY += 30;
            String displayAngle = String.format("%.2f", Math.toDegrees(angle % (Math.PI * 2)));
            graphics.drawString("Angle: " + displayAngle, drawingX, hudY);
        }
        if (window.isCollisionWatch()) {
            graphics.setColor(Color.CYAN);
            final int collisionWatchX = drawingX - renderWidth / 2;
            final int collisionWatchY = drawingY - renderHeight / 2;
            graphics.drawRect(collisionWatchX, collisionWatchY, renderWidth, renderHeight);

            if (moveableTag != null) {
                final boolean isColliding = state
                    .getDebugSettings()
                    .getPlayerCollisionFlags()
                    .stream()
                    .anyMatch(flag -> flag.getSessionId().equals(sessionId) && flag.getCollisionType().equals(moveableTag) && flag.getCollisionId() == serverId);
                if (isColliding) {
                    graphics.setColor(COLLISION_MARKER_COLOR);
                    graphics.fillRect(collisionWatchX, collisionWatchY, renderWidth, renderHeight);
                }
            }
        }
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
}
