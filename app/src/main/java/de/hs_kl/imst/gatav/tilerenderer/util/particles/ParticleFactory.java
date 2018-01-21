package de.hs_kl.imst.gatav.tilerenderer.util.particles;

import java.lang.reflect.InvocationTargetException;

import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.World;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner.CircleParticleSpawner;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner.DeferredCircleParticleSpawner;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner.ParticleSpawner;

/**
 * Create Particle Spawners
 * Created by Sebastian on 2018-01-18.
 */

public class ParticleFactory {
    private ParticleController particleController;
    private Timer timer;
    private World world;

    public ParticleFactory(ParticleController controller, Timer timer, World world) {
        this.particleController = controller;
        this.timer = timer;
        this.world = world;
    }

    /**
     * Generate a particle Spawner and add it to the world for updates
     *
     * @param position the position of the Spawner
     * @return a particle Spawner
     */
    public ParticleSpawner generateParticleSpawnerAndAddToWorld(Vector2 position) {
        ParticleSpawner particleSpawner = new DeferredCircleParticleSpawner(position, particleController, timer);
        world.addParticleSpawner(particleSpawner);
        return particleSpawner;
    }

    public ParticleSpawner generateParticleSpawnerAndAddToWorld(Vector2 position, Class<? extends CircleParticleSpawner> particleClass) {
        try {
            CircleParticleSpawner particleSpawner = particleClass.getConstructor(Vector2.class, ParticleController.class, Timer.class).newInstance(position, particleController, timer);
            world.addParticleSpawner(particleSpawner);
            return particleSpawner;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generate a particle Spawner and add it to the world for updates
     *
     * @return a particle Spawner
     */
    public ParticleSpawner generateParticleSpawnerAndAddToWorld() {
        return generateParticleSpawnerAndAddToWorld(new Vector2(0, 0));
    }
}
