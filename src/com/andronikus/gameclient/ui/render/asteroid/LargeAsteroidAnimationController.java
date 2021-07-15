package com.andronikus.gameclient.ui.render.asteroid;

import com.andronikus.game.model.server.Asteroid;
import com.andronikus.gameclient.ui.render.animation.AnimationController;
import com.andronikus.gameclient.ui.render.animation.AnimationState;

/**
 * Animation controller for a large asteroid.
 *
 * @author Andronikus
 */
public class LargeAsteroidAnimationController extends AnimationController<Asteroid, LargeAsteroidSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a large asteroid.
     *
     * @param asteroid The asteroid being animated
     */
    public LargeAsteroidAnimationController(Asteroid asteroid) {
        super(new LargeAsteroidSpriteSheet());
        this.id = asteroid.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<Asteroid, LargeAsteroidSpriteSheet> buildInitialStatesAndTransitions() {
        final AnimationState<Asteroid, LargeAsteroidSpriteSheet> neutralState = new AnimationState<>(this)
            .addFrame(1L, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite())
            .addFrame(null, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite());

        final AnimationState<Asteroid, LargeAsteroidSpriteSheet> crackingState = neutralState
            .createTransitionState((gameState, asteroid) -> asteroid.getDurability() <= 0);

        crackingState
            .addFrame(3L, LargeAsteroidSpriteSheet::getCrackingSprite)
            .addFrame(3L, LargeAsteroidSpriteSheet::getCrackingSprite)
            .addFrame(3L, LargeAsteroidSpriteSheet::getCrackingSprite)
            .addFrame(null, LargeAsteroidSpriteSheet::getCrackingSprite);

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
