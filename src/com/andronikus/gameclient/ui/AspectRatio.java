package com.andronikus.gameclient.ui;

import lombok.Getter;

// TODO rename?
@Getter
public class AspectRatio {

    // TODO this is super hacky, to calculate aspect ratio, we use the amount that the Background renderer overdraws
    public static final int OVERDRAW_WIDTH = 800;
    public static final int OVERDRAW_HEIGHT = 433;

    private double widthScale = 1.0;
    private double heightScale = 1.0;

    public AspectRatio(int width, int height) {
        calculate(width, height);
    }

    private AspectRatio() {}

    public void calculate(int width, int height) {
        widthScale = ((double)width / 2) / (double) AspectRatio.OVERDRAW_WIDTH;
        heightScale = ((double)height / 2) / (double) AspectRatio.OVERDRAW_HEIGHT;
    }

    public AspectRatio copy() {
        final AspectRatio copy = new AspectRatio();
        copy.widthScale = this.widthScale;
        copy.heightScale = this.heightScale;
        return copy;
    }
}
