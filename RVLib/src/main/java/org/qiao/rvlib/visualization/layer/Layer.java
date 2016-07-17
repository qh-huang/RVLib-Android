package org.qiao.rvlib.visualization.layer;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import org.qiao.rvlib.visualization.OpenGlDrawable;
import org.qiao.rvlib.visualization.VisualizationView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Interface for a drawable layer on a VisualizationView.
 *
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public interface Layer extends OpenGlDrawable {

    //void init(NodeMainExecutor nodeMainExecutor);
    void init();

    /**
     * Event handler for touch events.
     *
     * @param view
     *          the view generating the event
     * @param event
     *          the touch event
     * @return true if the event has been handled
     */
    boolean onTouchEvent(VisualizationView view, MotionEvent event);

    /**
     * Called when the layer is added to the {@link VisualizationView}.
     */
    //void onStart(VisualizationView view, ConnectedNode connectedNode);
    void onStart(VisualizationView view);

    /**
     * Called when the view is removed from the {@link VisualizationView}.
     */
    //void onShutdown(VisualizationView view, Node node);
    void onShutdown(VisualizationView view);

    /**
     * @param view
     *          the {@link VisualizationView} associated with the
     *          {@link GLSurfaceView.Renderer}
     * @see GLSurfaceView.Renderer#onSurfaceCreated(GL10, EGLConfig)
     */
    void onSurfaceCreated(VisualizationView view, GL10 gl, EGLConfig config);

    /**
     * @param view
     *          the {@link VisualizationView} associated with the
     *          {@link GLSurfaceView.Renderer}
     * @see GLSurfaceView.Renderer#onSurfaceChanged(GL10, int, int)
     */
    void onSurfaceChanged(VisualizationView view, GL10 gl, int width, int height);
}