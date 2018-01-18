package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import android.graphics.Canvas;

/**
 * Created by Sebastian on 2018-01-18.
 */

public interface Particle {
    void update(float delta);
    void draw(Canvas canvas);
}
