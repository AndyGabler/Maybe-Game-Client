package com.andronikus.gameclient.ui.render.asteroid;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.GameState;

/**
 * Animation controller for a large asteroid.
 *
 * @author Andronikus
 */
public class LargeAsteroidStopMotionController extends StopMotionController<GameState, Asteroid, LargeAsteroidSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a large asteroid.
     *
     * @param asteroid The asteroid being animated
     */
    public LargeAsteroidStopMotionController(Asteroid asteroid) {
        super(new LargeAsteroidSpriteSheet());
        this.id = asteroid.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, Asteroid, LargeAsteroidSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Asteroid, LargeAsteroidSpriteSheet> neutralState = new StopMotionState<>(this)
            .addFrame(1L, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite())
            .addFrame(null, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite());

        final StopMotionState<GameState, Asteroid, LargeAsteroidSpriteSheet> crackingState = neutralState
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
    public boolean checkIfObjectIsRoot(Asteroid asteroid) {
        return asteroid.getId() == id;
    }
}
