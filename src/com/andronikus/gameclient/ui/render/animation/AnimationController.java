package com.andronikus.gameclient.ui.render.animation;

import com.andronikus.game.model.server.GameState;
import com.andronikus.gameclient.ui.render.SpriteSheet;
import lombok.Getter;

import java.awt.image.BufferedImage;

/**
 * Controller for animation of some kind of object.
 *
 * @param <ANIMATION_OF_TYPE> Type of object being animated
 * @param <SPRITE_SHEET_TYPE> Sprite sheet to pull animation states from
 * @author Andronikus
 */
public abstract class AnimationController<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE extends SpriteSheet> {

    private final AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> initialState;
    private AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> activeState;

    /**
     * The sprite sheet that is used to pull animations from.
     */
    @Getter
    private SPRITE_SHEET_TYPE spriteSheet;

    /**
     * Instantiate a controller for animation of some kind of object.
     *
     * @param spriteSheet The sprite sheet to use in the animations
     */
    public AnimationController(SPRITE_SHEET_TYPE spriteSheet) {
        this.spriteSheet = spriteSheet;
        initialState = buildInitialStatesAndTransitions();
        this.activeState = initialState;
    }

    /**
     * For an animated entity, which is presumed to be the entity this controller is for, and a game state, get the
     * next sprite that should be rendered in the animation.
     *
     * @param gameState The game state
     * @param animatedEntity The animated entity
     * @return The sprite to render
     */
    public BufferedImage nextSprite(GameState gameState, ANIMATION_OF_TYPE animatedEntity) {
        final AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> nextState = activeState.checkTransition(gameState, animatedEntity);

        if (nextState != null) {
            activeState = nextState;
            nextState.transitionTo();
        }

        return activeState.nextSprite();
    }

    /**
     * Build the initial animation state. This is expected to have, in its transitions, every state that is possible.
     * Note, states never return to their initial state unless the a child state makes a cyclic transition. Therefore,
     * the resulting initial state must include every transition and each transition must be deliberate.
     *
     * @return The initial animation state
     */
    protected abstract AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> buildInitialStatesAndTransitions();

    /**
     * Check if the object we are attempting to render and get the next sprite for is the correct sprite to render. This
     * is appropriate and essential to call since the sprite retrieval function is stateful, so it is important that it
     * is only called when intended.
     *
     * @param object The object that may or may not be the object this controller is animating
     * @return Whether or not this controller animates given object
     */
    public abstract boolean checkIfObjectIsAnimatedEntity(ANIMATION_OF_TYPE object);
}
