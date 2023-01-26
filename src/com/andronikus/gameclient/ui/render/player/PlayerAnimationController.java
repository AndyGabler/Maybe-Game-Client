package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.animation.Animation;
import com.andronikus.animation4j.animation.AnimationController;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Player;
import com.andronikus.gameclient.ui.RenderRatio;

public class PlayerAnimationController extends AnimationController<GameState, Player> {

    public PlayerAnimationController(Player player, RenderRatio renderRatio) {
        super(new PlayerRig(player, renderRatio));
    }

    @Override
    protected Animation<GameState, Player> buildInitialStatesAndTransitions() {
        final Animation<GameState, Player> idleAnimation = createAnimation()
            .withInterruptableFlag(true);

        return idleAnimation.finishAnimating();
    }

    @Override
    public boolean checkIfObjectIsRoot(Player player) {
        return getRig().checkIfObjectIsAnimatedEntity(player);
    }
}
