package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;

public class PlayerTailStopMotionController extends StopMotionController<GameState, Player, PlayerTailSpriteSheet> {

    private final String sessionId;

    public PlayerTailStopMotionController(Player player) {
        super(new PlayerTailSpriteSheet());

        // Can't store player since it's reserialized every gamestate update. So store the session ID
        sessionId = player.getSessionId();
    }

    @Override
    protected StopMotionState<GameState, Player, PlayerTailSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Player, PlayerTailSpriteSheet> idleState = new StopMotionState<>(this)
                .addFrame(1L, (spriteSheet, state) -> spriteSheet.getIdleSprite())
                .addFrame(null, (spriteSheet, state) -> spriteSheet.getIdleSprite());
        return idleState;
    }

    @Override
    public boolean checkIfObjectIsRoot(Player player) {
        return player.getSessionId().equals(sessionId);
    }
}
