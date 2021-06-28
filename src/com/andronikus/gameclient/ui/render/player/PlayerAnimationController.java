package com.andronikus.gameclient.ui.render.player;

import com.andronikus.game.model.server.Player;
import com.andronikus.gameclient.ui.render.animation.AnimationState;
import com.andronikus.gameclient.ui.render.animation.AnimationController;

/**
 * Animation controller for a player.
 *
 * @author Andronikus
 */
public class PlayerAnimationController extends AnimationController<Player, PlayerSpriteSheet> {

    private final String sessionId;

    /**
     * Instantiate an animation controller for a player.
     *
     * @param player The player being animated
     */
    public PlayerAnimationController(Player player) {
        super(new PlayerSpriteSheet());

        // Can't store player since it's reserialized every gamestate update. So store the
        sessionId = player.getSessionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<Player, PlayerSpriteSheet> buildInitialStatesAndTransitions() {
        final AnimationState<Player, PlayerSpriteSheet> idleState = new AnimationState<>(this);

        idleState
            .addFrame(12, PlayerSpriteSheet::getIdleSprite)
            .addFrame(12, PlayerSpriteSheet::getIdleSprite)
            .addFrame(12, PlayerSpriteSheet::getIdleSprite)
            .addFrame(12, PlayerSpriteSheet::getIdleSprite);

        final AnimationState<Player, PlayerSpriteSheet> thrustingState = idleState.createTransitionState((gameState, player) -> player.getAcceleration() > 0 && !player.isBoosting())
            .addFrame(8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame(8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame(8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame(8, PlayerSpriteSheet::getThrustingSprite)
            .addFrame(8, PlayerSpriteSheet::getThrustingSprite);

        thrustingState.createTransition((gameState, player) -> player.getAcceleration() <= 0, idleState);

        final AnimationState<Player, PlayerSpriteSheet> boostingState = idleState.createTransitionState((gameState, player) -> player.isBoosting())
            .addFrame(8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame(8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame(8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame(8, PlayerSpriteSheet::getBoostingSprite)
            .addFrame(8, PlayerSpriteSheet::getBoostingSprite);

        thrustingState.createTransition((gameState, player) -> player.isBoosting(), boostingState);
        idleState.createTransition((gameState, player) -> player.isBoosting(), boostingState);

        boostingState.createTransition((gameState, player) -> !player.isBoosting() && player.getAcceleration() > 0, thrustingState);
        boostingState.createTransition((gameState, player) -> !player.isBoosting() && player.getAcceleration() <= 0, idleState);

        return idleState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsAnimatedEntity(Player player) {
        return player.getSessionId().equals(sessionId);
    }
}
