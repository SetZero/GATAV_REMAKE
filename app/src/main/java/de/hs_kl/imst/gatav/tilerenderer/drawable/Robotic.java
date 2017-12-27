package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by keven on 19.12.2017.
 */

public final class Robotic extends Enemys implements CollisionReactive, Destroyable {

    public Robotic(int x, int y){
        super(x,y, 30, 20,120f);
        try {
            InputStream is = GameContent.context.getAssets().open("dynamics/enemys/robo/idle/Idle1.png");
            loadGraphic(is, 33, 33, 5);
            is.close();
            run = new Animations(1f / 4f);
            run.addAnimation(Animations.frameLoad("dynamics/enemys/robo/run/Run", 8, 175, 175));
            dieng = new Animations(1f / 4f);
            try {
                dieng.addAnimation(Animations.frameLoad("dynamics/enemys/robo/die/Dead", 10, 175, 175));
            }
            catch(Exception e){
                e.printStackTrace();
            }
            idle = run.getDrawable(0f);
            hitbox = new Rectangle((int) x, (int) y, width/2 , height);
            isActive = true;
            //hitboxOffsetX = 40;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
