package  org.qiao.rvlib.visualization.layer;

import android.util.Log;


import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.namespace.GraphName;
import org.qiao.rvlib.visualization.Color;
import org.qiao.rvlib.visualization.VisualizationView;
import org.qiao.rvlib.visualization.shape.PixelSpaceAnchorShape;
import org.qiao.rvlib.visualization.shape.Shape;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class ScaleReferenceLayer extends DefaultLayer {
    private static final String TAG = "ScaleRefLayer";
    private ArrayList<Shape> anchors;

    private final GraphName frame;
    private float icon_scale_factor = 1.f;
    private float mDistance = 0.1f; // distance between anchors
    private Color color = Color.fromHexAndAlpha("0a0b0c", 1.0f);

    private float mMinX, mMaxX, mMinY, mMaxY;

    public ScaleReferenceLayer(GraphName frame) {
        this.frame = frame;
    }

    public ScaleReferenceLayer(String frame, float x_min, float y_min, float x_max, float y_max) {
        this(GraphName.of(frame));
        anchors = new ArrayList<>();
        setBoundInMeter(x_min, y_min, x_max, y_max);
    }

    public void setIconScale(float s) {
        icon_scale_factor = s;
        if(!anchors.isEmpty()){
            setAnchorDistInMeter(mDistance);
        }
    }
    public void setColor(Color c) { color = c; }
    public void setBoundInMeter(float x_min, float y_min, float x_max, float y_max) {
        mMinX = x_min;
        mMinY = y_min;
        mMaxX = x_max;
        mMaxY = y_max;
    }

    public void setAnchorDistInMeter(float distance) {
        mDistance = distance;
        ArrayList<Shape> new_anchors = new ArrayList<>();
        for (float x=mMinX; x<=mMaxX; x+=mDistance) {
            for (float y=mMinY; y<=mMaxY; y+=mDistance) {
                Transform tf = Transform.fromXYPlanePose2D(x * gl_scale_factor, y * gl_scale_factor, 0);
                PixelSpaceAnchorShape shape = new PixelSpaceAnchorShape();
                shape.setPixelsPerMeter(250);
                shape.setScale(icon_scale_factor);
                shape.setColor(color);
                shape.setTransform(tf);
                new_anchors.add(shape);
            }
        }
        Log.d(TAG, "anchors: " + new_anchors.size());
        synchronized (anchors) {
            anchors = new_anchors;
        }
    }

    @Override
    public void setGlScale(float s) {
        super.setGlScale(s);
        if (!anchors.isEmpty()) {
            // gl_scale_factor changed, refresh anchors
            setAnchorDistInMeter(mDistance);
        }
    }

    public void clearAnchors() {
        synchronized (anchors) {
            anchors.clear();
        }
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
        synchronized (anchors) {
            if (!anchors.isEmpty()) {
                for (Shape anchor : anchors) {
                    anchor.draw(view, gl);
                }
            }
        }
    }

    @Override
    public void onStart(VisualizationView view) {}

    public GraphName getFrame() {
        return frame;
    }
}
