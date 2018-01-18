package de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner;

import android.graphics.Color;

import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.FancyParticle;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;

/**
 * Created by Sebastian on 2018-01-18.
 */

public abstract class CircleParticleSpawner implements ParticleSpawner {
    protected Vector2 position;
    protected ParticleController controller;
    protected Timer timer;
    protected double lastTimeParticleWave = 0;
    protected double particleTimeBetween = 5;
    protected boolean active = true;

    public CircleParticleSpawner(Vector2 position, ParticleController controller, Timer timer) {
        this.position = position;
        this.controller = controller;
        this.timer = timer;
    }

    /**
     * Spawn 18 Particles with random color if the player is in the view of the spawner
     * @param cam Camera to check if spawn is needed
     */
    public abstract void update(GameCamera cam);

    /**
     * Updates Position (e.g. if attached to Dark Angle)
     * @param position
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void resetTimer() {
        lastTimeParticleWave = 0;
    }
}
