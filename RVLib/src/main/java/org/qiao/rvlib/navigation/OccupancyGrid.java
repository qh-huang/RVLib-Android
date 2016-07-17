package org.qiao.rvlib.navigation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

public class OccupancyGrid {
    private static final String TAG = "OccupancyGrid";
    //private ChannelBuffer data;
    private ByteBuffer data;
    private MapMetaData info;

    public MapMetaData getInfo() { return info; }
    public void setInfo(MapMetaData mmd) { info = mmd; }

    //public ChannelBuffer getData() { return data; }
    //public void setData(ChannelBuffer d) { data = d; }
    public ByteBuffer getData() { return data; }
    public void setData(ByteBuffer d) { data = d; }

    /**
     *
     * @param filepath the YAML file path
     * @return if nothing wrong with file and bitmap, a valid OccupancyGrid will return
     * @throws IOException
     */
    public static OccupancyGrid fromYamlFile(String filepath) throws IOException {
        InputStream input = new FileInputStream(new File(filepath));
        Yaml yaml = new Yaml();

        // [NOTE] debug only!! yaml.load(input) can be invoke just once!!
        //Log.d(TAG, "yaml: " + yaml.dumpAsMap(yaml.load(input)));
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) yaml.load(input);
        /*
        Log.d(TAG, "image: " + map.get("image") + " type: " + map.get("image").getClass().toString());
        Log.d(TAG, "resolution: " + map.get("resolution") + " type: " + map.get("resolution").getClass().toString());
        Log.d(TAG, "origin: " + map.get("origin") + " type: " + map.get("origin").getClass().toString());
        Log.d(TAG, "negate: " + map.get("negate") + " type: " + map.get("negate").getClass().toString());
        Log.d(TAG, "occupied_thresh: " + map.get("occupied_thresh") + " type: " + map.get("occupied_thresh").getClass().toString());
        Log.d(TAG, "free_thresh: " + map.get("free_thresh") + " type: " + map.get("free_thresh").getClass().toString());
        */
        //if(map == null) {
        //    Log.e(TAG, "Load YAML file Failed, exit program");
        //    System.exit(1);
        //}
        String map_filepath = (String)map.get("image");
        Bitmap bitmap = BitmapFactory.decodeFile(map_filepath);
        Bitmap.Config config = bitmap.getConfig();
        //Log.d(TAG, "bitmap Config: " + config);
        int map_width = bitmap.getWidth();
        int map_height = bitmap.getHeight();
        ArrayList poseArr = (ArrayList)map.get("origin");
        double x, y, theta;
        x = (double)poseArr.get(0);
        y = (double)poseArr.get(1);
        theta = (double)poseArr.get(2);
        Pose pose = new Pose(x, y, theta);
        double resolution = (Double)map.get("resolution");
        double occupy_threshold = (Double)map.get("occupied_thresh");
        double free_threshold = (Double)map.get("free_thresh");
        MapMetaData info = new MapMetaData();
        info.setHeight(map_height);
        info.setWidth(map_width);
        info.setOrigin(pose);
        info.setResolution((float)resolution);
        info.setOccThreshold((float)occupy_threshold);
        info.setFreeThreshold((float)free_threshold);
        int bytes = bitmap.getByteCount();
        //Log.d(TAG, "width height = " + map_width + " " + map_height);
        //Log.d(TAG, "bytes = " + bytes);
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] bufferArr = buffer.array();
        byte[] byte_arr = new byte[bytes / 4]; // only need 1 channel
        for (int row=0; row < map_height; row++) {
            for (int col=0; col < map_width; col++) {
                int i = row*map_width + col;
                //int index = (row)*map_width + col; // ros-order: origin is left-down
                int index = (map_height-1-row)*map_width + col; // cv-order: upside-down, orgin is left-up
                byte_arr[index] = (byte)((int)bufferArr[i*4 + 2] & 0xFF); // config is ARGB, take G channel
            }
        }
        ByteBuffer bufChannelG = ByteBuffer.wrap(byte_arr);
        OccupancyGrid og = new OccupancyGrid();
        og.setInfo(info);
        og.setData(bufChannelG);
        return og;
    }
}
