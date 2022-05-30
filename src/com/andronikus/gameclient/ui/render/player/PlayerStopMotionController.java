package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;

/**
 * Animation controller for a player.
 *
 * @author Andronikus
 */
public class PlayerStopMotionController extends StopMotionController<GameState, Player, PlayerSpriteSheet> {

    private final String sessionId;

    /**
     * Instantiate an animation controller for a player.
     *
     * @param player The player being animated
     */
    public PlayerStopMotionController(Player player) {
        super(new PlayerSpriteSheet());

        // Can't store player since it's reserialized every gamestate update. So store the
        sessionId = player.getSessionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, Player, PlayerSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Player, PlayerSpriteSheet> idleState = new StopMotionState<>(this);

        idleState
            .addFrame((long)12, PlayerSpriteSheet::getIdleSprite)
            .addFrame((long)12, PlayerSpriteSheet::getIdleSprite)
            .addFrame((long)12, PlayerSpriteSheet::getIdleSprite)
            .addFrame((long)12, PlayerSpriteSheet::getIdleSprite);

        final StopMotionState<GameState, Player, PlayerSpriteSheet> thrustingState = idleState.createTransitionState((gameState, player) -> player.isThrusting() && !player.isBoosting())
            .addFrame((long)8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getThrustingSprite);

        thrustingState.createTransition((gameState, player) -> !player.isThrusting() && !player.isBoosting(), idleState);

        final StopMotionState<GameState, Player, PlayerSpriteSheet> boostingState = idleState.createTransitionState((gameState, player) -> player.isBoosting())
            .addFrame((long)8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame((long)8, PlayerSpriteSheet::getBoostingSprite);

        thrustingState.createTransition((gameState, player) -> player.isBoosting(), boostingState);
        idleState.createTransition((gameState, player) -> player.isBoosting(), boostingState);

        boostingState.createTransition((gameState, player) -> !player.isBoosting() && player.isThrusting(), thrustingState);
        boostingState.createTransition((gameState, player) -> !player.isBoosting() && !player.isThrusting(), idleState);

        final StopMotionState<GameState, Player, PlayerSpriteSheet> deathState = idleState.createTransitionState(((gameState, player) -> player.isDead()))
            .addFrame((long)2, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)2, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)3, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)3, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)4, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)4, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)3, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)2, PlayerSpriteSheet::getDeathSprite)
            .addFrame((long)2, PlayerSpriteSheet::getDeathSprite)
            .addFrame(null, PlayerSpriteSheet::getDeathSprite);

        thrustingState.createTransition((gameState, player) -> player.isDead(), deathState);
        boostingState.createTransition((gameState, player) -> player.isDead(), deathState);

        final StopMotionState<GameState, Player, PlayerSpriteSheet> warpingState = idleState.createTransitionState((gameState, player) -> player.getCollidedPortalId() != null)
            .addFrame(5L, PlayerSpriteSheet::getWarpingSprite)
            .addFrame(4L, PlayerSpriteSheet::getWarpingSprite)
            .addFrame(3L, PlayerSpriteSheet::getWarpingSprite)
            .addFrame(6L, PlayerSpriteSheet::getWarpingSprite);

        boostingState.createTransition((gameState, player) -> player.getCollidedPortalId() != null, warpingState);
        thrustingState.createTransition((gameState, player) -> player.getCollidedPortalId() != null, warpingState);

        final StopMotionState<GameState, Player, PlayerSpriteSheet> reappearingState = warpingState.createTransitionState((gameState, player) -> player.isPerformedWarp() || player.getCollidedPortalId() == null)
            .addFrame(6L, PlayerSpriteSheet::getReappearingSprite)
            .addFrame(3L, PlayerSpriteSheet::getReappearingSprite)
            .addFrame(4L, PlayerSpriteSheet::getReappearingSprite)
            .addFrame(5L, PlayerSpriteSheet::getReappearingSprite);

        reappearingState.createTransition((gameState, player) -> player.getCollidedPortalId() == null, idleState);

        return idleState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsRoot(Player player) {
        return player.getSessionId().equals(sessionId);
    }
}
