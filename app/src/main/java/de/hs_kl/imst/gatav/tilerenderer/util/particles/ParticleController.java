package de.hs_kl.imst.gatav.tilerenderer.util.particles;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.ParticlePrototype;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Manages all Particles in the game
 * Created by Sebastian on 2018-01-18.
 */

public class ParticleController {
    private List<ParticlePrototype> particles = new ArrayList<>();
    private List<Collidable> collidables = new ArrayList<>();
    private Player player;

    public ParticleController(List<Collidable> collidables, Player player) {
        this.collidables = collidables;
        this.player = player;
    }

    /**
     * executes update on all Particles, checks if they collide with terrain, are out of camera or with the player
     * @param delta delta time
     * @param cam the camera object (for out of view check)
     */
    public void update(float delta, GameCamera cam) {
        particles.removeIf(p -> {
            boolean containing = false;
            for(Collidable c : collidables) {
                if(c instanceof Rectangle) {
                    Rectangle r = (Rectangle) c;
                    if(r.getRect().contains((int)p.getPosition().getX(), (int)p.getPosition().getY())) {
                        containing = true;
                        break;
                    } else if(!cam.getCameraViewRect().contains((int)p.getPosition().getX(), (int)p.getPosition().getY())) {
                        containing = true;
                        break;
                    }
                    if(player.getHitbox().getRect().contains((int)p.getPosition().getX(), (int)p.getPosition().getY())) {
                        containing = true;
                        player.setLifePoints(player.getLifePoints() - p.getDamage());
                        break;
                    }
                }
            }
            return containing;
        });
        particles.forEach(p -> p.update(delta));
    }

    /**
     * draws all particles
     * @param canvas canvas to draw on
     */
    public void draw(Canvas canvas) {
        particles.forEach(p -> p.draw(canvas));
    }

    /**
     * Adds a Single Particle
     * @param p one Particle
     */
    public void addParticle(ParticlePrototype p) {
        particles.add(p);
    }
}
