package com.andronikus.gameclient.ui.render.portal;

import com.andronikus.animation4j.stopmotion.StopMotionController;
import com.andronikus.animation4j.stopmotion.StopMotionState;
import com.andronikus.game.model.server.GameState;
import com.andronikus.game.model.server.Portal;

/**
 * Animation controller for a portal.
 *
 * @author Andronikus
 */
public class PortalStopMotionController extends StopMotionController<GameState, Portal, PortalSpriteSheet> {

    private final long id;

    /**
     * Instantiate an animation controller for a portal
     *
     * @param portal Portal being animated
     */
    public PortalStopMotionController(Portal portal) {
        super(new PortalSpriteSheet());
        this.id = portal.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StopMotionState<GameState, Portal, PortalSpriteSheet> buildInitialStatesAndTransitions() {
        final StopMotionState<GameState, Portal, PortalSpriteSheet> idleState = new StopMotionState<>(this)
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
    public boolean checkIfObjectIsRoot(Portal object) {
        return object.getId() == id;
    }
}
