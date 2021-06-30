package com.andronikus.gameclient.ui.render.laser;

import com.andronikus.game.model.server.Laser;
import com.andronikus.gameclient.ui.render.animation.AnimationController;
import com.andronikus.gameclient.ui.render.animation.AnimationState;

/**
 * Animation controller for a laser.
 *
 * @author Andronikus
 */
public class LaserAnimationController extends AnimationController<Laser, LaserSpriteSheet> {

    private final long animatedLaserId;

    /**
     * Instantiate an animation controller for a laser.
     *
     * @param laser The laser being animated
     */
    public LaserAnimationController(Laser laser) {
        super(new LaserSpriteSheet());

        animatedLaserId = laser.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<Laser, LaserSpriteSheet> buildInitialStatesAndTransitions() {
        final AnimationState<Laser, LaserSpriteSheet> travelingState = new AnimationState<>(this)
            .addFrame((long) 5, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 6, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 7, LaserSpriteSheet::getTravelingSprite)
            .addFrame((long) 6, LaserSpriteSheet::getTravelingSprite);

        final AnimationState<Laser, LaserSpriteSheet> hitState =
            travelingState.createTransitionState(((gameState, laser) -> laser.getXVelocity() == 0 && laser.getYVelocity() == 0));
        hitState
            .addFrame((long) 3, LaserSpriteSheet::getHitSprite)
            .addFrame((long) 2, LaserSpriteSheet::getHitSprite)
            .addFrame((long) 2, LaserSpriteSheet::getHitSprite)
            .addFrame(null, LaserSpriteSheet::getHitSprite);

        return travelingState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsAnimatedEntity(Laser laser) {
        return laser.getId() == animatedLaserId;
    }
}
