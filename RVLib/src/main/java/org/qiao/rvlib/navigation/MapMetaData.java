package org.qiao.rvlib.navigation;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class MapMetaData {
    private float resolution;
    private int width, height;
    private float occ_thresh;
    private float free_thresh;
    private Pose origin;

    public float getResolution() { return resolution; }
    public void setResolution(float res) { resolution = res; }
    public int getWidth() { return width; }
    public void setWidth(int w) { width = w; }
    public int getHeight() { return height; }
    public void setHeight(int h) { height = h; }
    public Pose getOrigin() { return origin; }
    public void setOrigin(Pose orig) { origin = orig; }
    public void setOccThreshold(float occ_th) { occ_thresh = occ_th; }
    public float getOccThreshold() { return occ_thresh; }
    public void setFreeThreshold(float free_th) { free_thresh = free_th; }
    public float getFreeThreshold() { return free_thresh; }

    public static MapMetaData fromYamlFile(String filepath) throws IOException {
        InputStream input = new FileInputStream(new File(filepath));
        Yaml yaml = new Yaml();

        // [NOTE] debug only!! yaml.load(input) can be invoke just once!!
        //Log.d(TAG, "yaml: " + yaml.dumpAsMap(yaml.load(input)));
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) yaml.load(input);

        ArrayList poseArr = (ArrayList)map.get("origin");
        double x, y, theta;
        x = (double)poseArr.get(0) * 100; // scale to cm
        y = (double)poseArr.get(1) * 100; // scale to cm
        theta = (double)poseArr.get(2);
        Pose pose = new Pose(x, y, theta);
        double resolution = (Double)map.get("resolution");
        double occupy_threshold = (Double)map.get("occupied_thresh");
        double free_threshold = (Double)map.get("free_thresh");
        MapMetaData info = new MapMetaData();
        info.setOrigin(pose);
        info.setResolution((float)resolution);
        info.setOccThreshold((float)occupy_threshold);
        info.setFreeThreshold((float)free_threshold);
        return info;
    }
}
