package de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner;

import android.graphics.Color;

import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.WaterParticle;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.CustomMathUtils;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;

/**
 * Particle Spawner
 * Created by Sebastian on 2018-01-18.
 */

public class WaterParticleSpawner extends CircleParticleSpawner {

    /**
     * Constructor for a Water Particle Spawner
     * @param position the Position of the Spawner
     * @param controller the Particle Controller
     * @param timer a global timer
     */
    public WaterParticleSpawner(Vector2 position, ParticleController controller, Timer timer) {
        super(position, controller, timer);
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
                for (int i = -180; i < 0; i += 10) {
                    WaterParticle particle = new WaterParticle(position, Color.rgb(17, 152, 224), CustomMathUtils.degreeToRadians(i), 3);
                    controller.addParticle(particle);
                }
                lastTimeParticleWave = timer.getElapsedTime();
                setActive(false);
            }
        }
    }
}
