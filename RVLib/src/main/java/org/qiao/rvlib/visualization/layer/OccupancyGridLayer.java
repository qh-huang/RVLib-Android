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
        private Transform origin;

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
            Preconditions.checkNotNull(origin);
            Preconditions.checkNotNull(stride);
            textureBitmap.updateFromPixelBuffer(pixelBuffer, stride, resolution, origin, COLOR_UNKNOWN);
            pixelBuffer.clear();
            ready = true;
        }

        public void setOrigin(Transform origin) {
            this.origin = origin;
        }

        public void setStride(int stride) {
            this.stride = stride;
        }
    }

    private final List<Tile> tiles;

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
//        getSubscriber().addMessageListener(new MessageListener<nav_msgs.OccupancyGrid>() {
//            @Override
//            public void onNewMessage(nav_msgs.OccupancyGrid message) {
//                update(message);
//            }
//        });
    }
    public void updateOccupancyGrid(OccupancyGrid occupancygrid) {
        final float resolution = occupancygrid.getInfo().getResolution();
        final int width = occupancygrid.getInfo().getWidth();
        final int height = occupancygrid.getInfo().getHeight();
        final int numTilesWide = (int) Math.ceil(width / (float) TextureBitmap.STRIDE);
        final int numTilesHigh = (int) Math.ceil(height / (float) TextureBitmap.HEIGHT);
        final int numTiles = numTilesWide * numTilesHigh;
        Log.d(TAG, "numTiles: " + numTiles);
        final Transform origin = occupancygrid.getInfo().getOrigin().toTransform();

        while (tiles.size() < numTiles) {
            tiles.add(new Tile(resolution));
        }
        Log.d(TAG, "tiles.size = " + tiles.size());
        for (int y = 0; y < numTilesHigh; ++y) {
            for (int x = 0; x < numTilesWide; ++x) {
                final int tileIndex = y * numTilesWide + x;
                tiles.get(tileIndex).setOrigin(origin.multiply(new Transform(new Vector3(x * resolution * TextureBitmap.STRIDE, y * resolution * TextureBitmap.HEIGHT, 0.), Quaternion.identity())));
                if (x < numTilesWide - 1) {
                    tiles.get(tileIndex).setStride(TextureBitmap.STRIDE);
                } else {
                    tiles.get(tileIndex).setStride(width % TextureBitmap.STRIDE);
                }
            }
        }

        int x = 0;
        int y = 0;
        final ByteBuffer buffer = occupancygrid.getData();
        while (buffer.hasRemaining()) {
            Preconditions.checkState(y < height);
            final int tileIndex = (y / TextureBitmap.STRIDE) * numTilesWide + x / TextureBitmap.STRIDE;
            final byte pixel = buffer.get();
            if (pixel != -51) {
                Log.d(TAG, "pixel(" + x + "," + y + ") = " + (int)pixel);
            }
            /*
            if (pixel == -1) {
                tiles.get(tileIndex).writeInt(COLOR_UNKNOWN);
            } else {
                if (pixel < 50) {
                    tiles.get(tileIndex).writeInt(COLOR_FREE);
                } else {
                    tiles.get(tileIndex).writeInt(COLOR_OCCUPIED);
                }
            }
            */
            if (pixel == -51) {
                tiles.get(tileIndex).writeInt(COLOR_UNKNOWN);
            } else {
                if (pixel == -2) {
                    tiles.get(tileIndex).writeInt(COLOR_FREE);
                } else {
                    tiles.get(tileIndex).writeInt(COLOR_OCCUPIED);
                }
            }
            ++x;
            if (x == width) {
                x = 0;
                ++y;
            }
        }
        for (Tile tile : tiles) {
            tile.update();
        }
        ready = true;
    }
    /* drop ChannelBuffer
    public void updateOccupancyGrid(OccupancyGrid occupancygrid) {
        final float resolution = occupancygrid.getInfo().getResolution();
        final int width = occupancygrid.getInfo().getWidth();
        final int height = occupancygrid.getInfo().getHeight();
        final int numTilesWide = (int) Math.ceil(width / (float) TextureBitmap.STRIDE);
        final int numTilesHigh = (int) Math.ceil(height / (float) TextureBitmap.STRIDE);
        final int numTiles = numTilesWide * numTilesHigh;
        final Transform origin = occupancygrid.getInfo().getOrigin().toTransform();

        while (tiles.size() < numTiles) {
            tiles.add(new Tile(resolution));
        }

        for (int y = 0; y < numTilesHigh; ++y) {
            for (int x = 0; x < numTilesWide; ++x) {
                final int tileIndex = y * numTilesWide + x;
                tiles.get(tileIndex).setOrigin(origin.multiply(new Transform(new Vector3(x *
                        resolution * TextureBitmap.STRIDE,
                        y * resolution * TextureBitmap.HEIGHT, 0.), Quaternion.identity())));
                if (x < numTilesWide - 1) {
                    tiles.get(tileIndex).setStride(TextureBitmap.STRIDE);
                } else {
                    tiles.get(tileIndex).setStride(width % TextureBitmap.STRIDE);
                }
            }
        }

        int x = 0;
        int y = 0;
        final ChannelBuffer buffer = occupancygrid.getData();
        while (buffer.readable()) {
            Preconditions.checkState(y < height);
            final int tileIndex = (y / TextureBitmap.STRIDE) * numTilesWide + x / TextureBitmap.STRIDE;
            final byte pixel = buffer.readByte();
            if (pixel == -1) {
                tiles.get(tileIndex).writeInt(COLOR_UNKNOWN);
            } else {
                if (pixel < 50) {
                    tiles.get(tileIndex).writeInt(COLOR_FREE);
                } else {
                    tiles.get(tileIndex).writeInt(COLOR_OCCUPIED);
                }
            }

            ++x;
            if (x == width) {
                x = 0;
                ++y;
            }
        }

        for (Tile tile : tiles) {
            tile.update();
        }

        ready = true;
    }
    */
}
