package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public abstract class MovableGraphics implements Drawable {

    protected BitmapDrawable bmp = null;
    //koordinaten als Vektor
    protected Vector2 Position = new Vector2();
    //aktuelle Geschwindigkeit des Objekts
    public boolean isOnGround = false;
    protected int width, height;
    public boolean isActive = false;

    protected Rectangle hitbox = null;

	public void setPosition(Vector2 position) {
        Position = position;
    }
	
    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    //public void setSpeed(float speed) { this.speed = speed; }

    // RichtungsVektor


    public MovableGraphics(float x, float y) {
        this.Position = new Vector2(x,y);
    }


    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public Vector2 getPosition() {
        return Position;
    }


    protected volatile Vector2 velocity = new Vector2();
    protected volatile Direction currentDirection = Direction.IDLE;

    public MovableGraphics(Vector2 pos) {
        this.Position = pos;
    }

    @Deprecated
    public void move(Vector2 direction) {
        this.velocity = direction;
    }

    synchronized public void impact(Vector2 direction){
        this.velocity = Vector2.add(direction,this.velocity);
    }

    private void move(float delta) {
            Position = Vector2.add(Vector2.skalarMul(velocity,delta),Position);
    }

    @Override
    public void update(float delta) {
        move(delta);
        bmp.setBounds((int)Position.getX(), (int)Position.getY()+height, (int)Position.getX()+width, (int)Position.getY());
        hitbox.setX((int)Position.getX()); hitbox.setY((int)Position.getY());
        hitbox.setWidth(width/10);
        hitbox.setHeight(height/10);
    }

    protected void getDirection(){

    }

    protected void loadGraphic(InputStream is,int widht, int height){
        this.width = widht; this.height = height;
        Bitmap bMap = BitmapFactory.decodeStream(is);
        bMap = Bitmap.createBitmap(bMap,0,0,17,35);
        bmp = new BitmapDrawable(bMap);
        bmp.setBounds((int)Position.getX(), ((int)Position.getY())+height, ((int)Position.getX())+widht, (int)Position.getY());
    } //left top right bottom

    public void draw(Canvas canvas) {
        if(bmp != null) {
            //bmp.draw(canvas);
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
    }
}