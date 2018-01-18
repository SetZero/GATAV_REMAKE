package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-18.
 */

public abstract class ParticlePrototype implements Particle {
    private Vector2 position;

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public abstract int getDamage();
}
