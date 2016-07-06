package org.qiao.rvlib.sensor;

public class LaserScan {
    private float[] ranges; // in meter
    private float range_max, range_min, angle_min, angle_increment;

    public LaserScan(float[] ranges, float rmax, float rmin, float amin, float ainc) {
        this.ranges = ranges;
        this.range_max = rmax;
        this.range_min = rmin;
        this.angle_min = amin;
        this.angle_increment = ainc;
    }

    /*
    public void setRangeMax(float rmax) { range_max = rmax; }
    public void setRangeMin(float rmin) { range_min = rmin; }
    public void setAngleMin(float amin) { angle_min = amin; }
    public void setAngleIncrement(float ainc) { angle_increment = ainc; }
    */
    public float getRangeMax() { return range_max; }
    public float getRangeMin() { return range_min; }
    public float getAngleMin() { return angle_min; }
    public float getAngleIncrement() { return angle_increment; }

    public float[] getRanges() {
        return ranges;
    }
}
