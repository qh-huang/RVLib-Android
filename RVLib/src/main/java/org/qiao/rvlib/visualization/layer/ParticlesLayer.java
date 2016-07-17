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
    private float icon_scale_factor = 1.f;
    private Color color = Color.fromHexAndAlpha("fa579d", 0.8f);

    public ParticlesLayer(GraphName frame) {
        this.frame = frame;
    }

    public ParticlesLayer(String frame) {
        this(GraphName.of(frame));
    }

    public void setIconScale(float s) { icon_scale_factor = s; }
    public void setColor(Color c) { color = c; }

    public void updateParticles(ArrayList<float[]> poses) {
        ArrayList<Shape> new_shapes = new ArrayList<>();
        int index = 0;
        for(float[] pose : poses) {
            Transform tf = Transform.fromXYPlanePose2D(pose[0] * gl_scale_factor, pose[1] * gl_scale_factor, pose[2]);
            PixelSpacePoseShape shape = new PixelSpacePoseShape();
            shape.setScale(icon_scale_factor);
            shape.setColor(color);
            shape.setTransform(tf);
            new_shapes.add(shape);
        }
        synchronized (shapes) {
            shapes = new_shapes;
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
