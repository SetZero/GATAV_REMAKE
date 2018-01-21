package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-18.
 */

public abstract class ParticlePrototype implements Particle {
    private Vector2 position;
    private boolean active = true;

    /**
     * Getter
     * @return  Current Position of Particle
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * Setter
     * @param position New position of Particle
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Deactives a Particle and removes it from the game.
     * Mark it for gargabe collection if isActive = false
     * @param active active status
     */
    protected void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Damage of a Particle
     * @return damage value
     */
    public abstract int getDamage();
}
