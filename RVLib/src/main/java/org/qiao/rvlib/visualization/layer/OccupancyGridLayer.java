package org.qiao.rvlib.visualization.layer;

import android.util.Log;

import org.qiao.rvlib.geometry.Quaternion;
import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.geometry.Vector3;
import org.qiao.rvlib.namespace.GraphName;
import org.qiao.rvlib.navigation.OccupancyGrid;
import org.qiao.rvlib.visualization.TextureBitmap;
import org.qiao.rvlib.visualization.VisualizationView;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class OccupancyGridLayer extends DefaultLayer {
    private static final String TAG = "OccupancyGridLayer";
    /**
     * Color of occupied cells in the map.
     */
    private static final int COLOR_OCCUPIED = 0xff111111;

    /**
     * Color of free cells in the map.
     */
    private static final int COLOR_FREE = 0xffffffff;

    /**
     * Color of unknown cells in the map.
     */
    private static final int COLOR_UNKNOWN = 0xffdddddd;

    /**
     * In order to draw maps with a size outside the maximum size of a texture,
     * we split the map into multiple tiles and draw one texture per tile.
     */
    private class Tile {
        static final int ESTIMATED_LENGTH = 256;
        private final ChannelBuffer pixelBuffer = ChannelBuffers.dynamicBuffer(ByteOrder.LITTLE_ENDIAN, ESTIMATED_LENGTH);
        private final TextureBitmap textureBitmap = new TextureBitmap();

        /**
         * Resolution of the {@link OccupancyGrid}.
         */
        private final float resolution;

        /**
         * Points to the top left of the {@link Tile}.
         */
        private Transform mapcenter;

        /**
         * Width of the {@link Tile}.
         */
        private int stride;

        /**
         * {@code true} when the {@link Tile} is ready to be drawn.
         */
        private boolean ready;

        public Tile(float resolution) {
            this.resolution = resolution;
            ready = false;
        }

        public void draw(VisualizationView view, GL10 gl) {
            if (ready) {
                textureBitmap.draw(view, gl);
            }
        }

        public void clearHandle() {
            textureBitmap.clearHandle();
        }

        public void writeInt(int value) {
            pixelBuffer.writeInt(value);
        }

        public void update() {
            Preconditions.checkNotNull(mapcenter);
            Preconditions.checkNotNull(stride);
            textureBitmap.updateFromPixelBuffer(pixelBuffer, stride, resolution, mapcenter, COLOR_UNKNOWN);
            pixelBuffer.clear();
            ready = true;
        }

        public void setMapCenter(Transform mapcenter) {
            this.mapcenter = mapcenter;
        }

        public void setStride(int stride) {
            this.stride = stride;
        }
    }

    private final List<Tile> tiles;

    private Transform mapcenter; // mapcenter of THIS LAYER should align to center of map
    private boolean ready;
    private GraphName frame;
    private GL10 previousGl;

    public OccupancyGridLayer(String topic) {
        this(GraphName.of(topic));
    }

    public OccupancyGridLayer(GraphName topic) {
        frame = topic;
        tiles = Lists.newCopyOnWriteArrayList();
        ready = false;
    }

    @Override
    public void draw(VisualizationView view, GL10 gl) {
        if (previousGl != gl) {
            for (Tile tile : tiles) {
                tile.clearHandle();
            }
            previousGl = gl;
        }
        if (ready) {
            for (Tile tile : tiles) {
                tile.draw(view, gl);
            }
        }
    }

    public GraphName getFrame() {
        return frame;
    }

    @Override
    public void onStart(VisualizationView view) {
        super.onStart(view);
        previousGl = null;
    }
    public void updateOccupancyGrid(OccupancyGrid occupancygrid) {
        final float resolution = occupancygrid.getInfo().getResolution();
        final float resolution_gl = resolution * gl_scale_factor;
        final float occ_upperbound = occupancygrid.getInfo().getOccThreshold();
        final float free_lowerbound = occupancygrid.getInfo().getFreeThreshold();
        final int imgWidth = occupancygrid.getInfo().getWidth();
        final int imgHeight = occupancygrid.getInfo().getHeight();
        final int numTilesWide = (int) Math.ceil(imgWidth / (float) TextureBitmap.STRIDE);
        final int numTilesHigh = (int) Math.ceil(imgHeight / (float) TextureBitmap.HEIGHT);
        final int numTiles = numTilesWide * numTilesHigh;
        Log.d(TAG, "numTiles: " + numTiles);

        // init mapcenter
        mapcenter = Transform.identity();
        mapcenter.getTranslation().setX(imgWidth * resolution);
        mapcenter.getTranslation().setY(imgHeight * resolution);
        mapcenter.getTranslation().setZ(0);
        Log.d(TAG, "map centerX: " + mapcenter.getTranslation().getX());
        Log.d(TAG, "map centerY: " + mapcenter.getTranslation().getY());

        // scale for GL rendering
        double tmpX = mapcenter.getTranslation().getX() * gl_scale_factor;
        double tmpY = mapcenter.getTranslation().getY() * gl_scale_factor;
        mapcenter.getTranslation().setX(-tmpX/2.f);
        mapcenter.getTranslation().setY(-tmpY/2.f);

        while (tiles.size() < numTiles) {
            tiles.add(new Tile(resolution_gl));
        }
        Log.d(TAG, "tiles.size = " + tiles.size());

        for (int y = 0; y < numTilesHigh; ++y) {
            for (int x = 0; x < numTilesWide; ++x) {
                final int tileIndex = y * numTilesWide + x;
                tiles.get(tileIndex).setMapCenter(mapcenter.multiply(
                        new Transform(
                                new Vector3(
                                        x * resolution_gl * TextureBitmap.STRIDE,
                                        y * resolution_gl * TextureBitmap.HEIGHT,
                                        0.),
                                Quaternion.identity())));
                if (x < numTilesWide - 1) {
                    tiles.get(tileIndex).setStride(TextureBitmap.STRIDE);
                } else {
                    tiles.get(tileIndex).setStride(imgWidth % TextureBitmap.STRIDE);
                }
            }
        }

        int x = 0;
        int y = 0;
        final ByteBuffer buffer = occupancygrid.getData();
        while (buffer.hasRemaining()) {
            Preconditions.checkState(y < imgHeight);
            int tileIndex;
            // order in ros...
            tileIndex = (y / TextureBitmap.STRIDE) * numTilesWide + x / TextureBitmap.STRIDE;
            //tileIndex = (numTilesHigh - 1 - y / TextureBitmap.STRIDE) * numTilesWide + x / TextureBitmap.STRIDE;

            final int pixel = (int)buffer.get() & 0xFF;
            // now pixel is [0, 255], normalize it to float point
            float occ = ((float)pixel) / 255.f;
            // revert the value
            occ = 1.f - occ;
            // Log.d(TAG, "pixel(" + x + "," + y + ") = " + occ + " tileIndex: " + tileIndex);
            if (occ >= occ_upperbound) {
                tiles.get(tileIndex).writeInt(COLOR_OCCUPIED);
            } else if (occ < free_lowerbound) {
                tiles.get(tileIndex).writeInt(COLOR_FREE);
            } else {
                tiles.get(tileIndex).writeInt(COLOR_UNKNOWN);
            }
            ++x;
            if (x == imgWidth) {
                x = 0;
                ++y;
            }
        }
        for (Tile tile : tiles) {
            tile.update();
        }
        ready = true;
    }
}
