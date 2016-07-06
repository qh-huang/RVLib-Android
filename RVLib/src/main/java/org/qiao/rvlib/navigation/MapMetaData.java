package org.qiao.rvlib.navigation;

public class MapMetaData {
    private float resolution;
    private int width, height;
    private Pose origin;

    public float getResolution() { return resolution; }
    public void setResolution(float res) { resolution = res; }
    public int getWidth() { return width; }
    public void setWidth(int w) { width = w; }
    public int getHeight() { return height; }
    public void setHeight(int h) { height = h; }
    public Pose getOrigin() { return origin; }
    public void setOrigin(Pose orig) { origin = orig; }
}
