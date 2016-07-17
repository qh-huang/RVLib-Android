package org.qiao.rvlib.visualization;

import android.opengl.GLSurfaceView;
import android.util.Log;

import org.qiao.rvlib.visualization.layer.Layer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class XYOrthographicRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "XYOrthographicRenderer";
    private static final Color BACKGROUND_COLOR = new Color(0.87f, 0.87f, 0.87f, 1.f);

    private final VisualizationView view;

    public XYOrthographicRenderer(VisualizationView view) {
        this.view = view;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Viewport viewport = new Viewport(width, height);
        viewport.apply(gl);
        //view.getCamera().setViewport(viewport);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glClearColor(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(),
                BACKGROUND_COLOR.getBlue(), BACKGROUND_COLOR.getAlpha());
        for (Layer layer : view.getLayers()) {
            layer.onSurfaceChanged(view, gl, width, height);
        }
    }
    /*
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        view.getCamera().apply(gl);
        drawLayers(gl);
    }
    */
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        drawLayers(gl);
    }

    /*
    private void drawLayers(GL10 gl) {
        for (Layer layer : view.getLayers()) {
            gl.glPushMatrix();
            if (layer instanceof TfLayer) {
                GraphName layerFrame = ((TfLayer) layer).getFrame();
                if (layerFrame != null && view.getCamera().applyFrameTransform(gl, layerFrame)) {
                    layer.draw(view, gl);
                }
            } else {
                layer.draw(view, gl);
            }
            gl.glPopMatrix();
        }
    }
    */

    private void drawLayers(GL10 gl) {
        for (Layer layer : view.getLayers()) {
            gl.glPushMatrix();
            layer.draw(view, gl);
            gl.glPopMatrix();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        for (Layer layer : view.getLayers()) {
            layer.onSurfaceCreated(view, gl, config);
        }
    }
}