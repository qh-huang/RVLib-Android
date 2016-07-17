package org.qiao.rvlib.visualization;

import org.qiao.rvlib.geometry.Transform;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class OpenGlTransform {

    private static final ThreadLocal<FloatBuffer> buffer = new ThreadLocal<FloatBuffer>() {
        @Override
        protected FloatBuffer initialValue() {
            return FloatBuffer.allocate(16);
        };

        @Override
        public FloatBuffer get() {
            FloatBuffer buffer = super.get();
            buffer.clear();
            return buffer;
        };
    };

    private OpenGlTransform() {
        // Utility class.
    }

    /**
     * Applies a {@link Transform} to an OpenGL context.
     *
     * @param gl
     *          the context
     * @param transform
     *          the {@link Transform} to apply
     */
    public static void apply(GL10 gl, Transform transform) {
        FloatBuffer matrix = buffer.get();
        for (double value : transform.toMatrix()) {
            matrix.put((float) value);
        }
        matrix.position(0);
        gl.glMultMatrixf(matrix);
    }
}
