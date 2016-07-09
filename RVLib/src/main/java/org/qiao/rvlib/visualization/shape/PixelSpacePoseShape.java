package org.qiao.rvlib.visualization.shape;

import org.qiao.rvlib.visualization.VisualizationView;

import javax.microedition.khronos.opengles.GL10;

public class PixelSpacePoseShape extends MetricSpacePoseShape {
    private static final float PIXELS_PER_METER = 100.f;

    private float scale_factor = 1.0f;

    public void setScale(float scale) { this.scale_factor = scale; }

    @Override
    protected void scale(VisualizationView view, GL10 gl) {
        // Adjust for metric scale definition of MetricSpacePoseShape vertices.
        gl.glScalef(PIXELS_PER_METER * scale_factor, PIXELS_PER_METER * scale_factor, 1.f);
        // Counter adjust for the camera zoom.
        //gl.glScalef(1 / (float) view.getCamera().getZoom(), 1 / (float) view.getCamera().getZoom(), 1.0f);
    }
}
