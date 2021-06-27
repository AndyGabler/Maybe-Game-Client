package com.gabler.gameclient.ui.render.animation;

import com.gabler.game.model.server.GameState;
import com.gabler.gameclient.ui.render.SpriteSheet;
import com.gabler.util.Pair;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * State in an animation. The animation has a set of states. These states are just descriptions about the condition of
 * the of animated object. Within each state, are the frames that the state cycles through and transitions to other states.
 *
 * @param <ANIMATION_OF_TYPE> The type of object being animated
 * @param <SPRITE_SHEET_TYPE> The type of sprite sheet being used to pull frames from
 * @author Andy Gabler
 */
public class AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE extends SpriteSheet> {

    private AnimationController<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> controller;

    // List of pairings of transition functions and states to transition to
    private List<Pair<BiFunction<GameState, ANIMATION_OF_TYPE, Boolean>, AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE>>> transitions = new ArrayList<>();

    // Counter of render ticks on this animation state
    private long ticksOnState = 0;

    /**
     * List of frame states.
     *
     * First pair parameter is the amount of ticks spent on this state.
     * Second pair parameter is the function call to the sprite sheet
     */
    private List<Pair<Long, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage>>> animationFrames = new ArrayList<>();

    // Ticks until state is reset, maintained by additions to the frames
    private long frameResetTickCount = 0;

    /**
     * Instantiate a state in an animation.
     *
     * @param controller The animation controller
     */
    public AnimationState(AnimationController<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> controller) {
        this.controller = controller;
    }

    /**
     * Check if a transition to another state is appropriate given the game state and animated entity.
     *
     * @param gameState The state of the game
     * @param animatedEntity The object being animated
     * @return The state to transition to, null if current state remains appropriate
     */
    public AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> checkTransition(GameState gameState, ANIMATION_OF_TYPE animatedEntity) {
        final Pair<BiFunction<GameState, ANIMATION_OF_TYPE, Boolean>, AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE>> nextState =
                transitions.stream().filter(transition -> transition.getFirst().apply(gameState, animatedEntity)).findFirst().orElse(null);

        if (nextState == null) {
            return null;
        }

        return nextState.getSecond();
    }

    /**
     * Transition to this state.
     */
    public void transitionTo() {
        ticksOnState = 0;
    }

    /**
     * Get the next sprite in this state. This is a stateful method.
     *
     * @return The next sprite
     */
    public BufferedImage nextSprite() {
        long frameIndex = ticksOnState % frameResetTickCount;
        long tickCounter = 0;

        Pair<Long, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage>> activeFrame = null;
        int frameNumber = -1;
        for (Pair<Long, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage>> animationFrame : animationFrames) {

            // Frame index goes to the last frame it is mature enough for
            if (frameIndex >= tickCounter) {
                activeFrame = animationFrame;
                frameNumber++;
            }

            tickCounter += animationFrame.getFirst();
        }

        ticksOnState++;
        if (activeFrame == null) {
            return null;
        }

        return activeFrame.getSecond().apply(controller.getSpriteSheet(), frameNumber);
    }

    /**
     * Add a frame to the animation state.
     *
     * @param tickCount How many ticks this state will last.
     * @param spriteCallback Function that takes a sprite sheet and an integer and gives a Sprite
     * @return Self so that this can be called in a builder-like fashion
     */
    public AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> addFrame(long tickCount, BiFunction<SPRITE_SHEET_TYPE, Integer, BufferedImage> spriteCallback) {
        this.animationFrames.add(new Pair<>(tickCount, spriteCallback));
        frameResetTickCount += tickCount;
        return this;
    }

    /**
     * Create a state with a function call that is used to transition to it.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @return The newly created state
     */
    public AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> createTransitionState(BiFunction<GameState, ANIMATION_OF_TYPE, Boolean> transitionCheck) {
        return createTransition(transitionCheck, new AnimationState<>(controller));
    }

    /**
     * Create a transition between this state and another state.
     *
     * @param transitionCheck Condition for when to transition to the state
     * @param state The state to transition to when condition is met
     * @return The state that a transition was added for
     */
    public AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> createTransition(BiFunction<GameState, ANIMATION_OF_TYPE, Boolean> transitionCheck, AnimationState<ANIMATION_OF_TYPE, SPRITE_SHEET_TYPE> state) {
        this.transitions.add(new Pair<>(transitionCheck, state));
        return state;
    }
}
