package org.qiao.rvlib.visualization.layer;

import android.util.Log;

import org.qiao.rvlib.geometry.Quaternion;
import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.geometry.Vector3;
import org.qiao.rvlib.visualization.VisualizationView;
import org.qiao.rvlib.visualization.shape.PixelSpacePoseShape;
import org.qiao.rvlib.visualization.shape.Shape;
import org.qiao.rvlib.namespace.GraphName;

import javax.microedition.khronos.opengles.GL10;

public class RobotLayer extends DefaultLayer {
    private static final String TAG = "RobotLayer";
    private final GraphName frame;

    private Shape shape;

    public RobotLayer(GraphName frame) {
        this.frame = frame;
    }

    public RobotLayer(String frame) {
        this(GraphName.of(frame));
    }

    public void setRobotPose2D(float x, float y, float theta) {
        Transform tf = Transform.fromXYPlanePose2D(x, y, theta);
        //Log.d(TAG, tf.toString());
        shape.setTransform(tf);
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
        if (shape != null) {
            shape.draw(view, gl);
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
        shape = new PixelSpacePoseShape();
    }

    public GraphName getFrame() {
        return frame;
    }
}
