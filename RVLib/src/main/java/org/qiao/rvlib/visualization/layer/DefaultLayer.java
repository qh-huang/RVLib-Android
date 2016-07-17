package org.qiao.rvlib.visualization.layer;

import android.view.MotionEvent;

import org.qiao.rvlib.visualization.VisualizationView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class DefaultLayer implements Layer {

    protected float gl_scale_factor = 1.0f;
    public void setGlScale(float s) { gl_scale_factor = s; }

    @Override
    public void init() {
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
    }

    @Override
    public boolean onTouchEvent(VisualizationView view, MotionEvent event) {
        return false;
    }

    @Override
    public void onStart(VisualizationView view) {
    }

    @Override
    public void onShutdown(VisualizationView view) {
    }

    @Override
    public void onSurfaceChanged(VisualizationView view, GL10 gl, int width, int height) {
    }

    @Override
    public void onSurfaceCreated(VisualizationView view, GL10 gl, EGLConfig config) {
    }
}