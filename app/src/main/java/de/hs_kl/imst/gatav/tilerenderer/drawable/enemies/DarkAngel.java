package de.hs_kl.imst.gatav.tilerenderer.drawable.enemies;


import android.content.Context;
import android.util.Pair;

import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import de.hs_kl.imst.gatav.tilerenderer.drawable.CollisionReactive;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Destroyable;
import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;
import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner.ParticleSpawner;

/**
 * Dark Angel
 * Bullet Hell Boss, crazy strong
 * Created by Sebastian on 2018-01-17.
 */

public final class DarkAngel extends Enemies implements CollisionReactive, Destroyable {

    private ParticleSpawner spawner;

    public DarkAngel(int x, int y, Context context, ParticleSpawner spawner) {
        super(x, y, 100, 15, 0f, 150);
        try {
            InputStream is = context.getAssets().open("dynamics/enemys/darkAngle/attack/attack1.png");
            loadGraphic(is, 70, 56, ScaleHelper.getEntitiyScale());
            is.close();
            run = new Animations(1f / 8f);
            run.addAnimation(Animations.frameLoad("dynamics/enemys/darkAngle/attack/attack", 10, 70 * ScaleHelper.getEntitiyScale(), 56 * ScaleHelper.getEntitiyScale(), context));
            dying = new Animations(1f / 4f);
            try {
                dying.addAnimation(Animations.frameLoad("dynamics/enemys/darkAngle/die/death", 4, 70 * ScaleHelper.getEntitiyScale(), 56 * ScaleHelper.getEntitiyScale(), context));
            } catch (Exception e) {
                e.printStackTrace();
            }
            idle = run.getDrawable(0f);
            hitbox = new Rectangle(x, y, width / 2, height - 4 * ScaleHelper.getEntitiyScale());
            isActive = true;
            drawOffsetY = -3 * ScaleHelper.getEntitiyScale();
            this.spawner = spawner;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public DarkAngel(int x, int y, Context context, ParticleSpawner spawner, float lifePoints) {
        this(x, y, context, spawner);
        super.lifePoints = lifePoints;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        int random = ThreadLocalRandom.current().nextInt(1000);
        if (random == 1) {
            setChanged();
            notifyObservers(new Pair<>(Sounds.WOOSH, Position));
        }
        spawner.setPosition(new Vector2(getPosition().getX() + getHitbox().getWidth() / 2, getPosition().getY() - 10 * ScaleHelper.getRatioY()));
    }

    @Override
    public void aIHandle() {
        if (GameContent.player.isAlive()) {

        }
    }

    @Override
    public Sounds getHitSound() {
        return Sounds.ROBOT_HIT_BY_PLAYER;
    }

    @Override
    protected Sounds getDeathSound() {
        return Sounds.ENEMY_DEATH;
    }

    @Override
    protected void animationHandle(float delta) {
        if (run.isFinished(animTime)) animTime = 0;
        animTime += delta;

        // running right
        bmp = run.getDrawable(animTime);
        isFlipped = false;
        /*if(currentDirection == Direction.IDLE){
            bmp = idle;
        }*/
    }

    @Override
    protected void cleanup() {
        spawner.setActive(false);
    }
}
