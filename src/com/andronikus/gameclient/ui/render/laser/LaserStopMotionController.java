package com.andronikus.gameclient.ui.render.laser;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Laser;

/**
 * Animation controller for a laser.
 *
 * @author Andronikus
 */
public class LaserStopMotionController extends StopMotionController<GameState, Laser, LaserSpriteSheet> {

    private final long animatedLaserId;

    /**
     * Instantiate an animation controller for a laser.
     *
     * @param laser The laser being animated
     */
    public LaserStopMotionController(Laser laser) {
        super(new LaserSpriteSheet());

        animatedLaserId = laser.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, Laser, LaserSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Laser, LaserSpriteSheet> travelingState = new StopMotionState<>(this)
            .addFrame((long) 5, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 6, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 7, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 6, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 4, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 5, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 8, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 4, LaserSpriteSheet::getTravelingSprite);

        final StopMotionState<GameState, Laser, LaserSpriteSheet> hitState =
            travelingState.createTransitionState(((gameState, laser) -> !laser.isActive()));
        hitState
            .addFrame(1L, LaserSpriteSheet::getHitSprite)
            .addFrame(2L, LaserSpriteSheet::getHitSprite)
            .addFrame(2L, LaserSpriteSheet::getHitSprite)
            .addFrame(2L, LaserSpriteSheet::getHitSprite)
            .addFrame(2L, LaserSpriteSheet::getHitSprite)
            .addFrame(3L, LaserSpriteSheet::getHitSprite)
            .addFrame(3L, LaserSpriteSheet::getHitSprite)
            .addFrame(null, LaserSpriteSheet::getHitSprite);

        return travelingState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsRoot(Laser laser) {
        return laser.getId() == animatedLaserId;
    }
}
