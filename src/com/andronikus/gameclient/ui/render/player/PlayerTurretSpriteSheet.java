package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

public class PlayerTurretSpriteSheet extends SpriteSheet {

    public PlayerTurretSpriteSheet() {
        super("player/player-turret-spritesheet.png", PlayerRig.TURRET_SIZE);
    }

    public BufferedImage getIdleSprite(int animationState) {
        return getTile(0, animationState);
    }

    public BufferedImage getBlastingSprite(int animationState) {
        return getTile(1, animationState);
    }
}
