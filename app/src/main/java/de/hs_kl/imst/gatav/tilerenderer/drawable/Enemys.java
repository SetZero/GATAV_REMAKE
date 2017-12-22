package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import de.hs_kl.imst.gatav.tilerenderer.util.Contact;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController;

/**
 * Created by keven on 17.12.2017.
 */

public abstract class Enemys extends MovableGraphics implements Destroyable, CollisionReactive  {
    protected float lifePoints;
    public static int hitPoints;
    public Enemys(float x, float y, float lifePoints, int hitPoints){
        super(x,y);
        this.lifePoints = lifePoints;
        this.hitPoints = hitPoints;
    }

    @Override
    public void processHit(float hit) {
        this.lifePoints -= hit;
    }

    @Override
    public boolean isDestroyed() {
        if(lifePoints <= 0) return true;
        return false;
    }

    @Override
    public void update(float delta){
        if(this.lifePoints <= 0) isActive =false;
        if(isActive) {
            super.update(delta);
            aIHandle();
            stateHandle();
            animationHandle(delta);
        }
    }

    public abstract void stateHandle();
    public abstract void animationHandle(float delta);
    public abstract void aIHandle();

    @Override
    public void onCollision(Contact c) {
        if(c.movable instanceof Player && c.siteHidden == PhysicsController.intersectDirection.TOP){
            this.lifePoints -= Player.hitPoints;
        }
    }

    @Override
    public void draw(Canvas canvas){
        if(bmp != null && isActive && !isFlipped) {
            Paint p = new Paint();
            p.setColor(Color.argb(128, 0, 65, 200));
            canvas.drawRect(hitbox.getRect(),p);
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
        else if (isFlipped){
            Paint p = new Paint();
            p.setColor(Color.argb(128, 0, 65, 200));
            canvas.drawRect(hitbox.getRect(),p);
            BitmapDrawable bmp = flip(this.bmp);
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
    }
}
