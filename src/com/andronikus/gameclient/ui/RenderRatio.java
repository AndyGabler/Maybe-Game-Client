package com.andronikus.gameclient.ui;

import lombok.Getter;

/**
 * Ratio of rendering from the canonical server stats and the screen portions. Keeps layers in sync.
 *
 * @author Andronikus
 */
@Getter
public class RenderRatio {

    public static final int OVERDRAW_WIDTH = 800;
    public static final int OVERDRAW_HEIGHT = 433;

    private double widthScale = 1.0;
    private double heightScale = 1.0;

    public RenderRatio(int width, int height) {
        calculate(width, height);
    }

    private RenderRatio() {}

    /**
     * Calculate the render ratio.
     *
     * @param width The width of the screen
     * @param height The height of the screen
     */
    public void calculate(int width, int height) {
        widthScale = ((double)width / 2) / (double) RenderRatio.OVERDRAW_WIDTH;
        heightScale = ((double)height / 2) / (double) RenderRatio.OVERDRAW_HEIGHT;
    }

    /**
     * Deep copy.
     *
     * @return The copy
     */
    public RenderRatio copy() {
        final RenderRatio copy = new RenderRatio();
        copy.widthScale = this.widthScale;
        copy.heightScale = this.heightScale;
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RenderRatio)) {
            return false;
        }

        final RenderRatio inputRatio = (RenderRatio) object;
        return inputRatio.widthScale == widthScale && inputRatio.heightScale == heightScale;
    }
}
