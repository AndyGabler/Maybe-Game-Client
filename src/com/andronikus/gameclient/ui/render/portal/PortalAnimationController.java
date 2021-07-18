package com.andronikus.gameclient.ui.render.portal;

import com.andronikus.game.model.server.Portal;
import com.andronikus.gameclient.ui.render.animation.AnimationController;
import com.andronikus.gameclient.ui.render.animation.AnimationState;

/**
 * Animation controller for a portal.
 *
 * @author Andronikus
 */
public class PortalAnimationController extends AnimationController<Portal, PortalSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a portal
     *
     * @param portal Portal being animated
     */
    public PortalAnimationController(Portal portal) {
        super(new PortalSpriteSheet());
        this.id = portal.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AnimationState<Portal, PortalSpriteSheet> buildInitialStatesAndTransitions() {
        // TODO
        final AnimationState<Portal, PortalSpriteSheet> idleState = new AnimationState<>(this)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite)
            .addFrame(3L, PortalSpriteSheet::getIdleSprite);

        idleState.createTransitionState((gameState, portal) -> portal.getTicksSinceCollision() != null)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite)
            .addFrame(3L, PortalSpriteSheet::getWarpingSprite);

        return idleState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkIfObjectIsAnimatedEntity(Portal object) {
        return object.getId() == id;
    }
}
