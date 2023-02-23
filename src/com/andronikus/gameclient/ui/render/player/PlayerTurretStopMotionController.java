package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;

public class PlayerTurretStopMotionController extends StopMotionController<GameState, Player, PlayerTurretSpriteSheet> {

    private final String sessionId;

    public PlayerTurretStopMotionController(Player player) {
        super(new PlayerTurretSpriteSheet());

        // Can't store player since it's reserialized every gamestate update. So store the session ID
        sessionId = player.getSessionId();
    }

    @Override
    protected StopMotionState<GameState, Player, PlayerTurretSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Player, PlayerTurretSpriteSheet> idleState = new StopMotionState<>(this)
            .addFrame(7L, PlayerTurretSpriteSheet::getIdleSprite)
            .addFrame(5L, PlayerTurretSpriteSheet::getIdleSprite)
            .addFrame(6L, PlayerTurretSpriteSheet::getIdleSprite)
            .addFrame(5L, PlayerTurretSpriteSheet::getIdleSprite)
            .addFrame(8L, PlayerTurretSpriteSheet::getIdleSprite)
            .addFrame(13L, PlayerTurretSpriteSheet::getIdleSprite);

        /*
         * Okay so this is cheating. We're counting on the internals of Animation4J to reset the state to the start
         * of the state, but not actually switch to it, kind of dangerous. So it will auto-switch back to the idle
         * state as soon as it gets to the blasting state. Then, when another laser is shot, it transitions to the blasting
         * state, which, technically, resets the frames on the blasting state to Frame 0.
         */
        final StopMotionState<GameState, Player, PlayerTurretSpriteSheet> blastingState = idleState
            .createTransitionState((gameState, player) -> player.getLaserShotTime() + 1 == gameState.getVersion()); // TODO not a huge fan we're relying on game state being a few ticks behind
        blastingState.createTransition((gameState, player) -> true, idleState);

        blastingState.withInterruptableFlag(false)
            .addFrame(2L, PlayerTurretSpriteSheet::getBlastingSprite)
            .addFrame(3L, PlayerTurretSpriteSheet::getBlastingSprite)
            .addFrame(3L, PlayerTurretSpriteSheet::getBlastingSprite)
            .addFrame(3L, PlayerTurretSpriteSheet::getBlastingSprite)
            .addFrame(2L, PlayerTurretSpriteSheet::getBlastingSprite)
            .addFrame(2L, PlayerTurretSpriteSheet::getBlastingSprite);

        return idleState;
    }

    @Override
    public boolean checkIfObjectIsRoot(Player player) {
        return player.getSessionId().equals(sessionId);
    }
}
