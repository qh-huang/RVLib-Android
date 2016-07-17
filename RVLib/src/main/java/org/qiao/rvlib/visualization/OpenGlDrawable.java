package org.qiao.rvlib.visualization;

import javax.microedition.khronos.opengles.GL10;

public interface OpenGlDrawable {
    void draw(VisualizationView view, GL10 gl);
}
