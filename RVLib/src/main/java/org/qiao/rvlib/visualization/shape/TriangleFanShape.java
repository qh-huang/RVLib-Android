package org.qiao.rvlib.visualization.shape;

import org.qiao.rvlib.visualization.Color;
import org.qiao.rvlib.visualization.Vertices;
import org.qiao.rvlib.visualization.VisualizationView;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class TriangleFanShape extends BaseShape {
    private final FloatBuffer vertices;

    /**
     * @param vertices
     *          an array of vertices as defined by OpenGL's GL_TRIANGLE_FAN method
     * @param color
     *          the {@link Color} of the {@link Shape}
     */
    public TriangleFanShape(float[] vertices, Color color) {
        super();
        this.vertices = Vertices.toFloatBuffer(vertices);
        setColor(color);
    }

    @Override
    public void drawShape(VisualizationView view, GL10 gl) {
        Vertices.drawTriangleFan(gl, vertices, getColor());
    }
}
