package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;

import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;

/**
 * Created by keven on 19.12.2017.
 */

public final class Walker extends Enemies implements CollisionReactive, Destroyable {

    private Rect walkArea;
    private Direction walkingDirection = Direction.LEFT;

    public Walker(int x, int y, Context context, Rect walkArea) {
        super(x, y, 30, 50, 190f, 30);
        this.walkArea = walkArea;
        try {
            InputStream is = context.getAssets().open("dynamics/enemys/robo/idle/Idle1.png");
            loadGraphic(is, 21, 27, ScaleHelper.getEntitiyScale());
            is.close();
            run = new Animations(1f / 8f);
            run.addAnimation(Animations.frameLoad("dynamics/enemys/ninja/run/Run", 8, 21 * ScaleHelper.getEntitiyScale(), 27 * ScaleHelper.getEntitiyScale(), context));
            dying = new Animations(1f / 8f);
            try {
                dying.addAnimation(Animations.frameLoad("dynamics/enemys/ninja/die/Dead", 10, 33 * ScaleHelper.getEntitiyScale(), 33 * ScaleHelper.getEntitiyScale(), context));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            Log.d("Walker", "Current: " + walkingDirection + ", Position X: " + Position.x + "(" + walkArea.left + "|" + walkArea.right + ")");

            if(walkingDirection == Direction.LEFT && Position.x  + hitbox.getWidth() < walkArea.left) {
                walkingDirection = Direction.RIGHT;
            } else if(walkingDirection == Direction.RIGHT && Position.x + hitbox.getWidth()  > walkArea.right) {
                walkingDirection = Direction.LEFT;
            }

            if (walkingDirection == Direction.LEFT) {
                move(Direction.LEFT);
                walkingDirection = Direction.LEFT;
            } else if (walkingDirection == Direction.RIGHT) {
                move(Direction.RIGHT);
                walkingDirection = Direction.RIGHT;
            }
        }
    }
}
