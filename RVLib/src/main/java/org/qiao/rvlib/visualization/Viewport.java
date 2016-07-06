package org.qiao.rvlib.visualization;

import javax.microedition.khronos.opengles.GL10;

public class Viewport {

    private final int width;
    private final int height;

    public Viewport(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void apply(GL10 gl) {
        gl.glViewport(0, 0, width, height);
        // Set the perspective projection to be orthographic.
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        // This corrects for the aspect ratio of the viewport. The viewport can now
        // be reasoned about in pixels. The zNear and zFar only need to be
        // sufficiently large to avoid clipping. The z-buffer is not otherwise used.
        gl.glOrthof(-width / 2.0f, width / 2.0f, -height / 2.0f, height / 2.0f, -1e4f, 1e4f);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}