package com.andronikus.gameclient.ui.render.hud;

import com.andronikus.gameclient.ui.GameWindow;
import com.andronikus.gameclient.ui.ImagesUtil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Renderer for the HUD.
 *
 * @author Andronikus
 */
public class HudRenderer {

    private static final int HUD_OFFSET = 125;
    private static final int HUD_WIDTH = 175;
    private static final int HUD_SECTION_OFFSET = 62;

    // TODO Maybe not hardcode and take some credense from the server?
    private static final int PLAYER_MAX_HEALTH = 100;
    private static final double SHIELD_RECHARGE_CAP = 1000;
    private static final int MAX_SHIELD_COUNT = 4;
    private static final double BOOSTING_RECHARGE_CAP = 200.0;

    private final BufferedImage laserChargeImage;
    private final ShieldSpriteSheet shieldSpriteSheet;

    public HudRenderer() {
        shieldSpriteSheet = new ShieldSpriteSheet();
        laserChargeImage = ImagesUtil.getImage("hud/LaserChargeIcon.png");
    }

    /**
     * Draw the HUD onto the graphic.
     *
     * @param graphics The graphics
     * @param health The health of the player
     * @param shieldCount The count of the shields
     * @param shieldRecharge Shield recharge
     * @param boostCharge Boost charge
     * @param boostRecharge Boost recharge
     * @param laserCharge Laser charge
     * @param observer HUD Observer
     */
    public void drawHud(
        Graphics graphics, int health, int shieldCount, int shieldRecharge,
        int boostCharge, int boostRecharge, int laserCharge, GameWindow observer
    ) {
        // Draw the health bar
        if (health < 0) {
            health = 0;
        }

        graphics.setColor(new Color(255, 250, 250));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD + Font.ITALIC, 20));
        graphics.drawString(health + "", HUD_OFFSET, HUD_OFFSET + 20);

        graphics.setColor(Color.LIGHT_GRAY);
        ((Graphics2D) graphics).setStroke(new BasicStroke(4));
        graphics.drawRect(HUD_OFFSET + 2, HUD_OFFSET + 22, HUD_WIDTH - 2, 40);

        graphics.setColor(getHealthColor(health));
        final double healthLength = (double)health / (double)PLAYER_MAX_HEALTH * (double)(HUD_WIDTH - 6);
        graphics.fillRect(HUD_OFFSET + 4, HUD_OFFSET + 24, (int)healthLength, 36);

        // Draw the shield status
        int shieldStatusIconNumber = 12;
        if (shieldCount < MAX_SHIELD_COUNT) {
            shieldStatusIconNumber = (int)((double)shieldRecharge / (SHIELD_RECHARGE_CAP / 12.0));
        }
        final BufferedImage shieldStatusSprite = shieldSpriteSheet.getStatusIcon(shieldStatusIconNumber);
        graphics.drawImage(shieldStatusSprite, HUD_OFFSET, HUD_OFFSET + 66, 62, 60, observer);

        graphics.setColor(new Color(0, 148, 255));
        graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD + Font.ITALIC, 60));
        graphics.drawString(shieldCount + "", HUD_OFFSET + HUD_SECTION_OFFSET, HUD_OFFSET + (HUD_SECTION_OFFSET * 2));

        // Draw the boost meter
        graphics.setColor(Color.LIGHT_GRAY);
        ((Graphics2D) graphics).setStroke(new BasicStroke(4));
        graphics.drawRect(HUD_OFFSET + 2, HUD_OFFSET + 146, HUD_WIDTH - 2, 40);

        graphics.setColor(new Color(0, 250, 250));
        final double boostRechargeLength = (double)boostRecharge / BOOSTING_RECHARGE_CAP * (double)(HUD_WIDTH - 6);
        graphics.fillRect(HUD_OFFSET + 4, HUD_OFFSET + 22 + (HUD_SECTION_OFFSET * 2), (int)boostRechargeLength, 36);

        graphics.setColor(Color.BLUE);
        final double boostChargeLength = (double)boostCharge / BOOSTING_RECHARGE_CAP * (double)(HUD_WIDTH - 6);
        graphics.fillRect(HUD_OFFSET + 4, HUD_OFFSET + 148, (int)boostChargeLength, 36);

        // Draw a laser counter
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawLine(HUD_OFFSET, HUD_OFFSET + 200, HUD_OFFSET, HUD_OFFSET + 200 + 15);
        graphics.drawLine(HUD_OFFSET, HUD_OFFSET + 200 + 15, HUD_OFFSET + HUD_WIDTH, HUD_OFFSET + 200 + 15);

        final int laserChargeY = HUD_OFFSET + 200 + 4;
        int laserChargeX = HUD_OFFSET + 3;

        while (laserCharge > 0) {
            laserCharge--;
            graphics.drawImage(laserChargeImage, laserChargeX, laserChargeY, 20, 6, observer);
            laserChargeX += 18;
        }
    }

    /**
     * Get color of the health bar based on how of the players health remains.
     *
     * @param health The health that remains.
     * @return Color of the health bar.
     */
    private Color getHealthColor(int health) {
        final double quarterMaxHealth = (double) PLAYER_MAX_HEALTH / 4;
        int green = (int)(255.0 * (((double)health - quarterMaxHealth) / (PLAYER_MAX_HEALTH - quarterMaxHealth)));
        int red = (int)(255.0 * (1.0 - (((double)health - quarterMaxHealth) / (PLAYER_MAX_HEALTH - quarterMaxHealth))));

        if (green < 0) {
            green = 0;
        }

        if (red > 255) {
            red = 255;
        }

        return new Color(red, green, 0);
    }
}
