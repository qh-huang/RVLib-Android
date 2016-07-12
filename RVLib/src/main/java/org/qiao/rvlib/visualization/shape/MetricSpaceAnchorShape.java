package org.qiao.rvlib.visualization.shape;

import org.qiao.rvlib.visualization.Color;

/**
 * Created by qiao on 7/13/16.
 */
public class MetricSpaceAnchorShape extends TriangleFanShape {
    private static final Color COLOR = Color.fromHexAndAlpha("808080", 0.8f);
    private static final float VERTICES[] = {
            0.1f, 0.f, 0.f,
            0.f, 0.1f, 0.f,
            -0.1f, 0.f, 0.f,
            0.f, -0.1f, 0.f
    };

    public MetricSpaceAnchorShape() {
        super(VERTICES, COLOR);
    }
}
