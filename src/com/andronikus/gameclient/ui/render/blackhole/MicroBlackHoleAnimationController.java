package com.andronikus.gameclient.ui.render.blackhole;

import com.andronikus.game.model.server.MicroBlackHole;
import com.andronikus.gameclient.ui.render.animation.AnimationController;
import com.andronikus.gameclient.ui.render.animation.AnimationState;

/**
 * Animation controller for a black hole.
 *
 * @author Andronikus
 */
public class MicroBlackHoleAnimationController extends AnimationController<MicroBlackHole, MicroBlackHoleSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a black hole.
     *
     * @param blackHole Black hole being animated
     */
    public MicroBlackHoleAnimationController(MicroBlackHole blackHole) {
        super(new MicroBlackHoleSpriteSheet());
        this.id = blackHole.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<MicroBlackHole, MicroBlackHoleSpriteSheet> buildInitialStatesAndTransitions() {
        final AnimationState<MicroBlackHole, MicroBlackHoleSpriteSheet> state = new AnimationState<>(this)
            .addFrame(3L, MicroBlackHoleSpriteSheet::getSprite)
            .addFrame(2L, MicroBlackHoleSpriteSheet::getSprite)
            .addFrame(3L, MicroBlackHoleSpriteSheet::getSprite)
            .addFrame(1L, MicroBlackHoleSpriteSheet::getSprite)
            .addFrame(3L, MicroBlackHoleSpriteSheet::getSprite)
            .addFrame(4L, MicroBlackHoleSpriteSheet::getSprite)
            .addFrame(3L, MicroBlackHoleSpriteSheet::getSprite);
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsAnimatedEntity(MicroBlackHole object) {
        return object.getId() == id;
    }

}
