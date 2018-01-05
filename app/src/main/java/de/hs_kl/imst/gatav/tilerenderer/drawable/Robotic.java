package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.content.Context;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;

/**
 * Created by keven on 19.12.2017.
 */

public final class Robotic extends Enemies implements CollisionReactive, Destroyable {

    public Robotic(int x, int y, Context context){
        super(x,y, 60, 50,120f,40);
        try {
            InputStream is = context.getAssets().open("dynamics/enemys/robo/idle/Idle1.png");
            loadGraphic(is, 33, 33, ScaleHelper.getEntitiyScale());
            is.close();
            run = new Animations(1f / 8f);
            run.addAnimation(Animations.frameLoad("dynamics/enemys/robo/run/Run", 8, 33*ScaleHelper.getEntitiyScale(), 33*ScaleHelper.getEntitiyScale(),context));
            dieng = new Animations(1f / 8f);
            try {
                dieng.addAnimation(Animations.frameLoad("dynamics/enemys/robo/die/Dead", 10, 33*ScaleHelper.getEntitiyScale(), 33*ScaleHelper.getEntitiyScale(),context));
            }
            catch(Exception e){
                e.printStackTrace();
            }
            idle = run.getDrawable(0f);
            hitbox = new Rectangle(x, y, width/2 , height-4*ScaleHelper.getEntitiyScale());
            isActive = true;
            drawOffsetY = -3*ScaleHelper.getEntitiyScale();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
