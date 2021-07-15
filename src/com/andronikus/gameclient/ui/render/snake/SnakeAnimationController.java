package com.andronikus.gameclient.ui.render.snake;

import com.andronikus.game.model.server.Snake;
import com.andronikus.gameclient.ui.render.animation.AnimationController;
import com.andronikus.gameclient.ui.render.animation.AnimationState;

/**
 * Animation controller for a snake.
 *
 * @author Andronikus
 */
public class SnakeAnimationController extends AnimationController<Snake, SnakeSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a snake.
     *
     * @param snake The snake being animated
     */
    public SnakeAnimationController(Snake snake) {
        super(new SnakeSpriteSheet());
        this.id = snake.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<Snake, SnakeSpriteSheet> buildInitialStatesAndTransitions() {
        final AnimationState<Snake, SnakeSpriteSheet> idleState = new AnimationState<>(this)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite);

        final AnimationState<Snake, SnakeSpriteSheet> chasingState = idleState.createTransitionState((gameState, snake) -> snake.isChasing())
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite);

        final AnimationState<Snake, SnakeSpriteSheet> dyingState = chasingState.createTransitionState((gameState, snake) -> snake.getHealth() <= 0)
            .addFrame(5L, SnakeSpriteSheet::getDyingSprite)
            .addFrame(7L, SnakeSpriteSheet::getDyingSprite)
            .addFrame(9L, SnakeSpriteSheet::getDyingSprite)
            .addFrame(11L, SnakeSpriteSheet::getDyingSprite)
            .addFrame(null, SnakeSpriteSheet::getDyingSprite);

        idleState.createTransition((gameState, snake) -> snake.getHealth() <= 0, dyingState);

        return idleState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsAnimatedEntity(Snake object) {
        return id == object.getId();
    }
}
