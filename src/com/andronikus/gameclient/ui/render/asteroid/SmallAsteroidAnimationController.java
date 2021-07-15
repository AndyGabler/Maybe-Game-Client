package com.andronikus.gameclient.ui.render.asteroid;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.gameclient.ui.render.animation.AnimationController;
import com.andronikus.gameclient.ui.render.animation.AnimationState;

/**
 * Animation controller for a small asteroid.
 *
 * @author Andronikus
 */
public class SmallAsteroidAnimationController extends AnimationController<Asteroid, SmallAsteroidSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a small asteroid.
     *
     * @param asteroid The asteroid being animated
     */
    public SmallAsteroidAnimationController(Asteroid asteroid) {
        super(new SmallAsteroidSpriteSheet());
        this.id = asteroid.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<Asteroid, SmallAsteroidSpriteSheet> buildInitialStatesAndTransitions() {
        final AnimationState<Asteroid, SmallAsteroidSpriteSheet> neutralState = new AnimationState<>(this)
            .addFrame(1L, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite())
            .addFrame(null, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite());

        final AnimationState<Asteroid, SmallAsteroidSpriteSheet> crackingState = neutralState
            .createTransitionState((gameState, asteroid) -> asteroid.getDurability() <= 0);

        crackingState
            .addFrame(3L, SmallAsteroidSpriteSheet::getCrackingSprite)
            .addFrame(3L, SmallAsteroidSpriteSheet::getCrackingSprite)
            .addFrame(3L, SmallAsteroidSpriteSheet::getCrackingSprite)
            .addFrame(null, SmallAsteroidSpriteSheet::getCrackingSprite);

        return neutralState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsAnimatedEntity(Asteroid asteroid) {
        return asteroid.getId() == id;
    }
}
