package org.qiao.rvlib.visualization;

import com.google.common.base.Preconditions;

import javax.microedition.khronos.opengles.GL10;

public class Color {

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public static Color copyOf(Color color) {
        return new Color(color.red, color.green, color.blue, color.alpha);
    }

    public static Color fromHexAndAlpha(String hex, float alpha) {
        Preconditions.checkArgument(hex.length() == 6);
        float red = Integer.parseInt(hex.substring(0, 2), 16) / 255.0f;
        float green = Integer.parseInt(hex.substring(2, 4), 16) / 255.0f;
        float blue = Integer.parseInt(hex.substring(4), 16) / 255.0f;
        return new Color(red, green, blue, alpha);
    }

    public Color(float red, float green, float blue, float alpha) {
        Preconditions.checkArgument(0.0f <= red && red <= 1.0f);
        Preconditions.checkArgument(0.0f <= green && green <= 1.0f);
        Preconditions.checkArgument(0.0f <= blue && blue <= 1.0f);
        Preconditions.checkArgument(0.0f <= alpha && alpha <= 1.0f);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void apply(GL10 gl) {
        gl.glColor4f(red, green, blue, alpha);
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}