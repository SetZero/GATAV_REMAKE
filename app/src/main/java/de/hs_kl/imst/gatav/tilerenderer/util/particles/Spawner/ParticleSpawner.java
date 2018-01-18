package de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner;

import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-18.
 */

public interface ParticleSpawner {
    void update(GameCamera cam);
    void setPosition(Vector2 position);
    boolean isActive();
    void setActive(boolean active);
}
