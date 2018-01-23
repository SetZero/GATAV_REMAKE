package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import android.graphics.Canvas;

/**
 * Created by Sebastian on 2018-01-18.
 */

public interface Particle {
    /**
     * Updates a Particle
     * @param delta delta time (in seconds)
     */
    void update(float delta);

    /**
     * Draws a particle to the canvas
     * @param canvas Canvas to draw on
     */
    void draw(Canvas canvas);

    /**
     * Check if particle is active
     * @return is particle active?
     */
    boolean isActive();

    //ignore list
    boolean isIgnoringPlayer();
    boolean isIgnoringCameraView();
    boolean isOriginatedFromPlayer();
}
