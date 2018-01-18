package de.hs_kl.imst.gatav.tilerenderer.util.particles;

import android.graphics.Color;

import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.FancyParticle;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Particle Spawner
 * Created by Sebastian on 2018-01-18.
 */

public class ParticleSpawner {
    private Vector2 position;
    private ParticleController controller;
    private Timer timer;
    private double lastTimeParticleWave = 0;
    private double particleTimeBetween = 5;

    public ParticleSpawner(Vector2 position, ParticleController controller, Timer timer) {
        this.position = position;
        this.controller = controller;
        this.timer = timer;
    }

    /**
     * Spawn 18 Particles with random color if the player is in the view of the spawner
     * @param cam Camera to check if spawn is needed
     */
    public void update(GameCamera cam) {
        if(cam.getCameraViewRect().contains((int)position.getX(), (int)position.getY())) {
            if (lastTimeParticleWave < timer.getElapsedTime() - particleTimeBetween) {
                for (int i = 0; i < 18; i++) {
                    FancyParticle particle = new FancyParticle(position, Color.rgb(i * 14, 0, 255 - i * 14), i * 10);
                    controller.addParticle(particle);
                }
                lastTimeParticleWave = timer.getElapsedTime();
            }
        }
    }

    /**
     * Updates Position (e.g. if attached to Dark Angle)
     * @param position
     */
    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
