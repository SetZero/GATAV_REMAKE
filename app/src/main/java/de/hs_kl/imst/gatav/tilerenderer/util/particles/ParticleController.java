package de.hs_kl.imst.gatav.tilerenderer.util.particles;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.drawable.enemies.Enemies;
import de.hs_kl.imst.gatav.tilerenderer.drawable.particles.ParticlePrototype;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Manages Spawns of all Particles in the game. Every Particle Spawner manages its own chunk of Particles
 * Created by Sebastian on 2018-01-18.
 */

public class ParticleController implements Runnable {
    private Queue<ParticlePrototype> particles = new ConcurrentLinkedQueue<>();
    private List<Collidable> collidables = new ArrayList<>();
    private Player player;
    private List<Enemies> enemies = new ArrayList<>();
    private GameCamera camera;
    private boolean running = true;

    public ParticleController(List<Collidable> collidables, Player player, GameCamera cam) {
        this.collidables = collidables;
        this.player = player;
        this.camera = cam;
    }

    /**
     * executes update on all Particles, checks if they collide with terrain, are out of camera or with the player
     *
     * @param delta delta time
     * @param cam   the camera object (for out of view check)
     */
    public synchronized void update(float delta, GameCamera cam) {
        particles.removeIf(p -> {
            if(!p.isActive()) return true;
            if (!p.isIgnoringPlayer() && player.getHitbox().getRect().contains((int) p.getPosition().getX(), (int) p.getPosition().getY())) {
                player.setLifePoints(player.getLifePoints() - p.getDamage());
                return true;
            }
            if (!p.isIgnoringCameraView() && !cam.getCameraViewRect().contains((int) p.getPosition().getX(), (int) p.getPosition().getY())) {
                return true;
            }
            boolean toBeRemoved = false;
            for (Collidable c : collidables) {
                if (c instanceof Rectangle) {
                    Rectangle r = (Rectangle) c;
                    if (r.getRect().contains((int) p.getPosition().getX(), (int) p.getPosition().getY())) {
                        toBeRemoved = true;
                        break;
                    }
                }
            }
            if(p.isOriginatedFromPlayer()) {
                for(Enemies e : enemies) {
                    if(e.getHitbox().getRect().contains((int) p.getPosition().getX(), (int) p.getPosition().getY())) {
                        toBeRemoved = true;
                        e.processHit(p.getDamage());
                        break;
                    }
                }
            }
            if(!toBeRemoved)
                p.update(delta);
            return toBeRemoved;
        });
    }

    /**
     * draws all particles
     *
     * @param canvas canvas to draw on
     */
    public void draw(Canvas canvas) {
        particles.forEach(p -> p.draw(canvas));
    }

    /**
     * Adds a Single Particle
     *
     * @param p one Particle
     */
    public synchronized void addParticle(ParticlePrototype p) {
        particles.add(p);
    }

    public void addEnemy(Enemies enemy) {
        enemies.add(enemy);
    }

    @Override
    public void run() {
        while (running) {
            try {
                this.update(0.016f, camera);
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanup() {
        running = false;
    }
}
