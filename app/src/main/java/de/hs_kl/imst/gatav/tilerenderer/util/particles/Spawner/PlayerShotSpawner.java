package de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner;

import android.graphics.Color;

import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.PlayerParticle;
import de.hs_kl.imst.gatav.tilerenderer.util.CustomMathUtils;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;

/**
 * Particle Spawner
 * Created by Sebastian on 2018-01-18.
 */

public class PlayerShotSpawner extends CircleParticleSpawner {

    private int totalShots = 3;
    private Direction direction = Direction.RIGHT;
    private static final  int maximumSpawner = 2;
    private static int totalPlayerShotSpawner = 0;

    public PlayerShotSpawner(Vector2 position, ParticleController controller, Timer timer) {
        super(position, controller, timer);
        super.particleTimeBetween = 0.2;


        PlayerShotSpawner.totalPlayerShotSpawner++;
        if(PlayerShotSpawner.totalPlayerShotSpawner > PlayerShotSpawner.maximumSpawner)
            setActive(false);
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
                if (totalShots > 0 && lastTimeParticleWave < timer.getElapsedTime() - particleTimeBetween) {
                    PlayerParticle particle = new PlayerParticle(position, Color.rgb(17, 152, 224), (direction == Direction.RIGHT ? 0 : (float)Math.PI));
                    controller.addParticle(particle);
                    lastTimeParticleWave = timer.getElapsedTime();
                    totalShots--;
                } else if(totalShots <= 0) {
                    setActive(false);
                }
            }
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if(!active)
            PlayerShotSpawner.totalPlayerShotSpawner--;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
