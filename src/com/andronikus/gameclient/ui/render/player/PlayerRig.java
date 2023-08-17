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

    public static final int TURRET_SIZE = 26;
    public static final int TAIL_WIDTH = 64;
    public static final int TAIL_HEIGHT = 192;
    public static final short TURRET_LIMB_ID = 1;
    private static final short TAIL_LIMB_ID = 2;

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
        // TODO distance from fulcrum problem, needs to factor in render ratio
        final AnimationLimb<GameState, Player> shipLimb = new AnimationLimb<GameState, Player>()
            .setWidth(scaleWidth(GameWindowRenderer.PLAYER_SIZE))
            .setHeight(scaleHeight(GameWindowRenderer.PLAYER_SIZE))
            .setStopMotionController(new PlayerStopMotionController(player))
            .finishRigging();

        // Turret joint
        shipLimb.registerJoint(TURRET_LIMB_ID, Math.PI * 3 / 2, 10, false)
            .getLimb()
            .setWidth(scaleWidth(TURRET_SIZE))
            .setHeight(scaleHeight(TURRET_SIZE))
            .setStopMotionController(new PlayerTurretStopMotionController(player))
            .finishRigging();

        shipLimb.registerJoint(TAIL_LIMB_ID, 3 * Math.PI / 2, 120, true)
            .getLimb()
            .setWidth(scaleWidth(TAIL_WIDTH))
            .setHeight(scaleHeight(TAIL_HEIGHT))
            .setStopMotionController(new PlayerTailStopMotionController(player))
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
