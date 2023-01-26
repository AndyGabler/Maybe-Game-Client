package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.rig.AnimationLimb;
import com.andronikus.animation4j.rig.AnimationRig;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameclient.ui.RenderRatio;
import com.andronikus.gameclient.ui.render.GameWindowRenderer;

import java.util.Collections;
import java.util.List;

public class PlayerRig extends AnimationRig<GameState, Player> {

    private final String sessionId;
    private RenderRatio renderRatio;

    public PlayerRig(Player player, RenderRatio renderRatio) {
        super(player, renderRatio);

        // Can't store player since it's reserialized every gamestate update. So store the session ID
        sessionId = player.getSessionId();
    }

    @Override
    protected void preLimbBuild(Object callbackParameter) {
        renderRatio = (RenderRatio) callbackParameter;
    }

    @Override
    protected List<AnimationLimb<GameState, Player>> buildLimbs(Player player) {
        final AnimationLimb<GameState, Player> shipLimb = new AnimationLimb<GameState, Player>()
            .setWidth(scaleWidth(GameWindowRenderer.PLAYER_SIZE))
            .setHeight(scaleHeight(GameWindowRenderer.PLAYER_SIZE))
            .setStopMotionController(new PlayerStopMotionController(player))
            .finishRigging();

        return Collections.singletonList(shipLimb);
    }

    private int scaleWidth(int width) {
        return (int) (renderRatio.getWidthScale() * (double) width);
    }

    private int scaleHeight(int height) {
        return (int) (renderRatio.getHeightScale() * (double) height);
    }

    @Override
    public boolean checkIfObjectIsAnimatedEntity(Player player) {
        return player.getSessionId().equals(sessionId);
    }
}
