package com.andronikus.gameclient.ui.render.player;

import com.andronikus.animation4j.spritesheet.SpriteSheet;

import java.awt.image.BufferedImage;

public class PlayerTailSpriteSheet extends SpriteSheet {

    public PlayerTailSpriteSheet() {
        super("player/player-tail-spritesheet.png", PlayerRig.TAIL_WIDTH, PlayerRig.TAIL_HEIGHT);
    }

    public BufferedImage getIdleSprite() {
        return getTile(0, 0);
    }
}
