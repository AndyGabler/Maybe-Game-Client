package com.andronikus.gameclient.ui.render.asteroid;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.Asteroid;
import com.andronikus.game.model.server.GameState;

/**
 * Animation controller for a small asteroid.
 *
 * @author Andronikus
 */
public class SmallAsteroidStopMotionController extends StopMotionController<GameState, Asteroid, SmallAsteroidSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a small asteroid.
     *
     * @param asteroid The asteroid being animated
     */
    public SmallAsteroidStopMotionController(Asteroid asteroid) {
        super(new SmallAsteroidSpriteSheet());
        this.id = asteroid.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, Asteroid, SmallAsteroidSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Asteroid, SmallAsteroidSpriteSheet> neutralState = new StopMotionState<>(this)
            .addFrame(1L, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite())
            .addFrame(null, (spriteSheet, counter) -> spriteSheet.getAsteroidSprite());

        final StopMotionState<GameState, Asteroid, SmallAsteroidSpriteSheet> crackingState = neutralState
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
    public boolean checkIfObjectIsRoot(Asteroid asteroid) {
        return asteroid.getId() == id;
    }
}
