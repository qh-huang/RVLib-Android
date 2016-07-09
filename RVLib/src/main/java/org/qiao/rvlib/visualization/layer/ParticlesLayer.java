package org.qiao.rvlib.visualization.layer;

import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.namespace.GraphName;
import org.qiao.rvlib.visualization.Color;
import org.qiao.rvlib.visualization.VisualizationView;
import org.qiao.rvlib.visualization.shape.PixelSpacePoseShape;
import org.qiao.rvlib.visualization.shape.Shape;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class ParticlesLayer extends DefaultLayer {
    private static final String TAG = "ParticlesLayer";
    private ArrayList<Shape> shapes;

    private final GraphName frame;
    private float scale_factor = 0.7f;
    private Color color = Color.fromHexAndAlpha("0a0b0c", 1.0f);

    public ParticlesLayer(GraphName frame) {
        this.frame = frame;
    }

    public ParticlesLayer(String frame) {
        this(GraphName.of(frame));
    }

    public void setScale(float s) { scale_factor = s; }
    public void setColor(Color c) { color = c; }

    public void addParticle(float x, float y, float theta) {
        Transform tf = Transform.fromXYPlanePose2D(x, y, theta);
        //Log.d(TAG, tf.toString());
        PixelSpacePoseShape shape = new PixelSpacePoseShape();
        shape.setScale(scale_factor);
        shape.setColor(color);
        shape.setTransform(tf);
        synchronized (shapes) {
            shapes.add(shape);
        }
    }

    public void clearParticles() {
        synchronized (shapes) {
            shapes.clear();
        }
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
        synchronized (shapes) {
            if (!shapes.isEmpty()) {
                for (Shape shape : shapes) {
                    shape.draw(view, gl);
                }
            }
        }
    }

    /*
    @Override
    public void onStart(VisualizationView view, ConnectedNode connectedNode) {
        shape = new PixelSpacePoseShape();
    }
    */
    @Override
    public void onStart(VisualizationView view) {
        shapes = new ArrayList<>();
    }

    public GraphName getFrame() {
        return frame;
    }
}
