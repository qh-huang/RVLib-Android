package org.qiao.rvlib.visualization.shape;

import org.qiao.rvlib.visualization.VisualizationView;
import org.qiao.rvlib.visualization.shape.MetricSpaceAnchorShape;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by qiao on 7/13/16.
 */
public class PixelSpaceAnchorShape extends MetricSpaceAnchorShape {

    private static float PIXELS_PER_METER = 100.f;
    private float scale_factor = 1.f;

    public void setScale(float scale) { this.scale_factor = scale; }
    public void setPixelsPerMeter(float s) {
        PIXELS_PER_METER = s;
    }

    @Override
    protected void scale(VisualizationView view, GL10 gl) {
        gl.glScalef(PIXELS_PER_METER * scale_factor, PIXELS_PER_METER * scale_factor, 1.f);
    }
}
