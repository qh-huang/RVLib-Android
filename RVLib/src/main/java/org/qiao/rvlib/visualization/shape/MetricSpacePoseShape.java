package org.qiao.rvlib.visualization.shape;

import org.qiao.rvlib.visualization.Color;

public class MetricSpacePoseShape extends TriangleFanShape {
    private static final Color COLOR = Color.fromHexAndAlpha("377dfa", 0.8f);
    private static final float VERTICES[] = {
            0.2f, 0.f, 0.f,
            -0.2f, -0.15f, 0.f,
            -0.05f, 0.f, 0.f,
            -0.2f, 0.15f, 0.f,
            0.2f, 0.f, 0.f
    };

    public MetricSpacePoseShape() {
        super(VERTICES, COLOR);
    }
}
