package org.qiao.rvlib.navigation;

import android.util.Log;

import org.qiao.rvlib.geometry.Quaternion;
import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.geometry.Vector3;

public class Pose {
    private static final String TAG = "Pose";
    private Vector3 position;
    private Quaternion orientation;

    public Pose(double x, double y, double theta) {
        setPosition(new Vector3(x,y,0));
        setOrientation(Quaternion.fromAxisAngle(Vector3.zAxis(), theta));
    }
    public Vector3 getPosition() { return position; }
    public void setPosition(Vector3 v) {
        position = v;
        if (position.getZ() != 0.0) {
            Log.e(TAG, "Z is not equal to 0, not on XY plane");
        }
    }
    public Quaternion getOrientation() { return orientation; }
    public void setOrientation(Quaternion q) {
        orientation = q;
    }
    public Transform toTransform() { return new Transform(position, orientation); }
}
