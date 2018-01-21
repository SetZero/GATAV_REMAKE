package de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner;

import android.graphics.Color;

import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.FancyParticle;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.CustomMathUtils;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;

/**
 * Particle Spawner
 * Created by Sebastian on 2018-01-18.
 */

public class ExtremeDeferredCircleParticleSpawner extends CircleParticleSpawner {
    private int currentCirclePosition = 0;
    private int maxCirclePosition = 360;

    public ExtremeDeferredCircleParticleSpawner(Vector2 position, ParticleController controller, Timer timer) {
        super(position, controller, timer);
        super.particleTimeBetween = 1;
    }

    /**
     * Spawn 18 Particles with random color if the player is in the view of the spawner
     *
     * @param cam Camera to check if spawn is needed
     */
    @Override
    public void update(GameCamera cam) {
        if (active) {
            if (cam.getCameraViewRect().contains((int) position.getX(), (int) position.getY())) {
                if (lastTimeParticleWave < timer.getElapsedTime() - particleTimeBetween) {
                    for(int i=0;i<360;i+=180) {
                        FancyParticle particle = new FancyParticle(position,
                                Color.rgb(currentCirclePosition * (360 / maxCirclePosition), 0, 255 - currentCirclePosition * (360 / maxCirclePosition)),
                                CustomMathUtils.degreeToRadians(currentCirclePosition - i));
                        controller.addParticle(particle);
                    }
                    lastTimeParticleWave = timer.getElapsedTime();
                    currentCirclePosition = (currentCirclePosition <= maxCirclePosition ? currentCirclePosition + 30 : 0);
                }
            }
        }
    }
}
