package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-18.
 */

public abstract class ParticlePrototype implements Particle {
    private Vector2 position;
    private boolean active = true;

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    public abstract int getDamage();
}
