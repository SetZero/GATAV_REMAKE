package de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner;

import android.graphics.Color;

import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.FancyParticle;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;

/**
 * Particle Spawner
 * Created by Sebastian on 2018-01-18.
 */

public class DeferredCircleParticleSpawner extends CircleParticleSpawner {
    private int currentCirclePosition = 0;
    private int maxCirclePosition = 50;

    public DeferredCircleParticleSpawner(Vector2 position, ParticleController controller, Timer timer) {
        super(position, controller, timer);
        super.particleTimeBetween = 0.7;
    }

    /**
     * Spawn 1 particle every {@link #particleTimeBetween} seconds. Will be Spawned in a circle
     *
     * @param cam Camera to check if spawn is needed
     */
    @Override
    public void update(GameCamera cam) {
        if (active) {
            if (cam.getCameraViewRect().contains((int) position.getX(), (int) position.getY())) {
                if (lastTimeParticleWave < timer.getElapsedTime() - particleTimeBetween) {
                    FancyParticle particle = new FancyParticle(position,
                            Color.rgb(currentCirclePosition * (360 / maxCirclePosition), 0, 255 - currentCirclePosition * (360 / maxCirclePosition)),
                            currentCirclePosition * (360 / maxCirclePosition));
                    controller.addParticle(particle);
                    lastTimeParticleWave = timer.getElapsedTime();
                    currentCirclePosition = (currentCirclePosition < maxCirclePosition ? currentCirclePosition + 1 : 0);
                }
            }
        }
    }
}
