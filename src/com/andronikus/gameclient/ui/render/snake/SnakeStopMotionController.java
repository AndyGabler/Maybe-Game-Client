package com.andronikus.gameclient.ui.render.snake;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Snake;

/**
 * Animation controller for a snake.
 *
 * @author Andronikus
 */
public class SnakeStopMotionController extends StopMotionController<GameState, Snake, SnakeSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a snake.
     *
     * @param snake The snake being animated
     */
    public SnakeStopMotionController(Snake snake) {
        super(new SnakeSpriteSheet());
        this.id = snake.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, Snake, SnakeSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Snake, SnakeSpriteSheet> idleState = new StopMotionState<>(this)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite)
            .addFrame(6L, SnakeSpriteSheet::getIdleSprite);

        final StopMotionState<GameState, Snake, SnakeSpriteSheet> chasingState = idleState.createTransitionState((gameState, snake) -> snake.isChasing())
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite)
            .addFrame(3L, SnakeSpriteSheet::getChasingSprite);

        final StopMotionState<GameState, Snake, SnakeSpriteSheet> dyingState = chasingState.createTransitionState((gameState, snake) -> snake.getHealth() <= 0)
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
    public boolean checkIfObjectIsRoot(Snake object) {
        return id == object.getId();
    }
}
