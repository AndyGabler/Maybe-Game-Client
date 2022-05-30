package com.andronikus.gameclient.ui.render.blackhole;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.MicroBlackHole;

/**
 * Animation controller for a black hole.
 *
 * @author Andronikus
 */
public class MicroBlackHoleStopMotionController extends StopMotionController<GameState, MicroBlackHole, MicroBlackHoleSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a black hole.
     *
     * @param blackHole Black hole being animated
     */
    public MicroBlackHoleStopMotionController(MicroBlackHole blackHole) {
        super(new MicroBlackHoleSpriteSheet());
        this.id = blackHole.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, MicroBlackHole, MicroBlackHoleSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, MicroBlackHole, MicroBlackHoleSpriteSheet> state = new StopMotionState<>(this)
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
    public boolean checkIfObjectIsRoot(MicroBlackHole object) {
        return object.getId() == id;
    }

}
