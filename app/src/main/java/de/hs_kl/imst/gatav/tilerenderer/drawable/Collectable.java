package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Contact;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by keven on 31.12.2017.
 */

public abstract class Collectable implements  Drawables {

    private Vector2 Position;
    private BitmapDrawable bmp;
    protected int width,height;
    private boolean isCollected = false;
    protected Rectangle Hitbox;

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public Collectable(int x, int y, int width, int height, InputStream is){
        Position = new Vector2(x,y);
        loadGraphic(is,width,height, ScaleHelper.getEntitiyScale()/2);
        Hitbox = new Rectangle(x,y,this.width,this.height);
    }

    private void loadGraphic(InputStream is,int width, int height, int scale){
        Bitmap bMap = BitmapFactory.decodeStream(is);
        bMap = Bitmap.createBitmap(bMap,0,0,width,height);
        bmp = new BitmapDrawable(Bitmap.createScaledBitmap(bMap,width*scale,height*scale,false));
        this.width = width*scale;
        this.height = height*scale;
        bmp.setBounds((int)Position.getX(), ((int)Position.getY())+this.height, ((int)Position.getX())+this.width, (int)Position.getY());
    }

    @Override
    public void update(float delta){
        if(!isCollected && GameContent.player.getHitbox().isCollidingWith(Hitbox)){
            onCollect();
            this.isCollected = true;
        }
    }

    protected abstract void onCollect();

    @Override
    public void draw(Canvas canvas){
        if(!isCollected)
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
    }

    /*
    @Override
    public void onCollision(Contact c){
        if(c.collisionObject instanceof  Player && c.siteHidden != PhysicsController.intersectDirection.DONT){
            isCollected = true;
        }
    }
    */
}
