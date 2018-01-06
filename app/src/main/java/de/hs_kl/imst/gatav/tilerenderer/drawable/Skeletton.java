package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;

/**
 * Created by keven on 27.12.2017.
 */

public final class Skeletton extends Enemies implements Destroyable, CollisionReactive {
    public Skeletton(float x, float y, Context context) {
        super(x, y, 50, 50, 140f, 60);
        try {
            InputStream is = context.getAssets().open("dynamics/enemys/robo/idle/Idle1.png");
            loadGraphic(is, 33, 33, ScaleHelper.getEntitiyScale());
            is.close();
            run = new Animations(1f / 4f);
            run.addAnimation(Animations.frameLoad("dynamics/enemys/robo/run/Run", 8, 175, 175, context));
            dieng = new Animations(1f / 4f);
            try {
                dieng.addAnimation(Animations.frameLoad("dynamics/enemys/robo/die/Dead", 10, 175, 175, context));
            } catch (Exception e) {
                e.printStackTrace();
            }
            idle = run.getDrawable(0f);
            hitbox = new Rectangle((int) x, (int) y, width / 2, height);
            isActive = true;
            //hitboxOffsetX = 40;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
