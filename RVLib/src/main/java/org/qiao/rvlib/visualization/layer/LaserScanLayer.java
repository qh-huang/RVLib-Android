package org.qiao.rvlib.visualization.layer;

import android.util.Log;

import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.namespace.GraphName;
import org.qiao.rvlib.sensor.LaserScan;
import org.qiao.rvlib.visualization.Color;
import org.qiao.rvlib.visualization.OpenGlTransform;
import org.qiao.rvlib.visualization.Vertices;
import org.qiao.rvlib.visualization.VisualizationView;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class LaserScanLayer extends DefaultLayer {
    private static final String TAG = "LaserScanLayer";

    public static final Color FREE_SPACE_COLOR = Color.fromHexAndAlpha("377dfa", 0.7f);
    public static final Color OCCUPIED_SPACE_COLOR = Color.fromHexAndAlpha("377dfa", 0.9f);
    public static final float LASER_SCAN_POINT_SIZE = 3.f;
    public static final int LASER_SCAN_STRIDE = 1;

    private static final float PIXELS_PER_METER = 20.f; // 1 pixel = 0.05m

    private final Object mutex;

    private GraphName frame;
    private FloatBuffer vertexFrontBuffer;
    private FloatBuffer vertexBackBuffer;

    private Transform transform;

    public LaserScanLayer(String topicName) {
        this(GraphName.of(topicName));
    }

    public LaserScanLayer(GraphName topicName) {
        this.frame = topicName;
        mutex = new Object();
        transform = Transform.identity();
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
        if (vertexFrontBuffer != null) {
            synchronized (mutex) {
                // qiao@2016.07.06
                // [NOTE] since we don't have a transform tree, apply transform here
                gl.glPushMatrix();
                OpenGlTransform.apply(gl, transform);
                //gl.glScalef(PIXELS_PER_METER, PIXELS_PER_METER, 1.f);
                // End applying transform
                Vertices.drawTriangleFan(gl, vertexFrontBuffer, FREE_SPACE_COLOR);
                // Drop the first point which is required for the triangle fan but is
                // not a range reading.
                FloatBuffer pointVertices = vertexFrontBuffer.duplicate();
                pointVertices.position(3);
                Vertices.drawPoints(gl, pointVertices, OCCUPIED_SPACE_COLOR, LASER_SCAN_POINT_SIZE);
                gl.glPopMatrix();
            }
        }
    }

    @Override
    public void onStart(VisualizationView view) {
        super.onStart(view);
    }

    public void updateLaserScan(LaserScan laserScan) {
        updateVertexBuffer(laserScan, LASER_SCAN_STRIDE);
    }

    public void setLaserPose(float x, float y, float theta) {
        transform = Transform.fromXYPlanePose2D(x * gl_scale_factor, y * gl_scale_factor, theta);
        //transform = Transform.fromXYPlanePose2D(x, y, theta);
    }

    private void updateVertexBuffer(LaserScan laserScan, int stride) {
        Log.d(TAG, "updateVertexBuffer++");
        float[] ranges = laserScan.getRanges();
        Log.d(TAG, "ranges.length = " + ranges.length);
        int size = ((ranges.length / stride) + 2) * 3;
        if (vertexBackBuffer == null || vertexBackBuffer.capacity() < size) {
            vertexBackBuffer = Vertices.allocateBuffer(size);
        }
        vertexBackBuffer.clear();
        // We start with the origin of the triangle fan.
        vertexBackBuffer.put(0);
        vertexBackBuffer.put(0);
        vertexBackBuffer.put(0);
        float minimumRange = laserScan.getRangeMin();
        float maximumRange = laserScan.getRangeMax();
        float angle = laserScan.getAngleMin();
        float angleIncrement = laserScan.getAngleIncrement();
        Log.d(TAG, "rmin rmax amin ainc = " + minimumRange + " " + maximumRange + " " + angle + " " + angleIncrement);
        // Calculate the coordinates of the laser range values.
        for (int i = 0; i < ranges.length; i += stride) {
            float range = ranges[i];
            // Ignore ranges that are outside the defined range. We are not overly
            // concerned about the accuracy of the visualization and this is makes it
            // look a lot nicer.
            if (minimumRange < range && range < maximumRange) {
                // x, y, z
                //Log.d(TAG, "drawing angle = " + angle + ", range = " + range);
                vertexBackBuffer.put((float) (range * Math.cos(angle)) * gl_scale_factor);
                vertexBackBuffer.put((float) (range * Math.sin(angle)) * gl_scale_factor);
                vertexBackBuffer.put(0);
            } else {
                vertexBackBuffer.put(0);
                vertexBackBuffer.put(0);
                vertexBackBuffer.put(0);
                //Log.d(TAG, "skipping angle = " + angle + ", range = " + range);
            }
            angle += angleIncrement * stride;
        }
        vertexBackBuffer.position(0);
        synchronized (mutex) {
            FloatBuffer tmp = vertexFrontBuffer;
            vertexFrontBuffer = vertexBackBuffer;
            vertexBackBuffer = tmp;
        }
    }

    public GraphName getFrame() {
        return frame;
    }
}