package org.qiao.rvlib.visualization.shape;

import org.qiao.rvlib.geometry.Transform;
import org.qiao.rvlib.visualization.Color;

import org.qiao.rvlib.visualization.OpenGlDrawable;

public interface Shape extends OpenGlDrawable {
    /**
     * @param color
     *          the {@link Color} of this {@link Shape}
     */
    void setColor(Color color);

    /**
     * @return the {@link Color} of this {@link Shape}
     */
    Color getColor();

    /**
     * @param transform
     *          the {@link Transform} that will be applied to this {@link Shape}
     *          before it is drawn
     */
    void setTransform(Transform transform);

    /**
     * @return the {@link Transform} that will be applied to this {@link Shape}
     *         before it is drawn
     */
    Transform getTransform();
}
