package de.hs_kl.imst.gatav.tilerenderer.drawable.enemies;


import android.content.Context;
import android.graphics.Rect;
import android.util.Pair;

import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import de.hs_kl.imst.gatav.tilerenderer.drawable.CollisionReactive;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Destroyable;
import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;
import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;

/**
 * Dark Angel
 * not ready should be some bullet hell boss
 * Created by Sebastian on 2018-01-17.
 */

public final class DarkAngel extends Enemies implements CollisionReactive, Destroyable {

    public DarkAngel(int x, int y, Context context) {
        super(x, y, 9999, 9999, 190f, 30);
        try {
            InputStream is = context.getAssets().open("dynamics/enemys/darkAngle/attack/attack1.png");
            loadGraphic(is, 70, 56, ScaleHelper.getEntitiyScale());
            is.close();
            run = new Animations(1f / 8f);
            run.addAnimation(Animations.frameLoad("dynamics/enemys/darkAngle/attack/attack", 10, 70 * ScaleHelper.getEntitiyScale(), 56 * ScaleHelper.getEntitiyScale(), context));
            idle = run.getDrawable(0f);
            hitbox = new Rectangle(x, y, width / 2, height - 4 * ScaleHelper.getEntitiyScale());
            isActive = true;
            drawOffsetY = -3 * ScaleHelper.getEntitiyScale();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        int random = ThreadLocalRandom.current().nextInt(1000);
        if (random == 1) {
            setChanged();
            notifyObservers(new Pair<>(Sounds.WOOSH, Position));
        }
    }

    @Override
    public void aIHandle() {
        if (GameContent.player.isAlive()) {

        }
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
}
