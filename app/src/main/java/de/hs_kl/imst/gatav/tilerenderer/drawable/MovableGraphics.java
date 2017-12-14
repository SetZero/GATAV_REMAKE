package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public abstract class MovableGraphics implements Drawables {

    protected BitmapDrawable bmp = null;
    protected boolean isFlipped = false;
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

    public void impact(Vector2 direction){
        this.velocity = Vector2.add(direction,this.velocity);
    }

    private void move(float delta) {
            Position = Vector2.add(Vector2.skalarMul(velocity,delta),Position);
    }

    @Override
    public void update(float delta) {
        if(isActive){
        move(delta);
        bmp.setBounds((int)Position.getX(), (int)Position.getY()+height, (int)Position.getX()+width, (int)Position.getY());
        hitbox.setX((int)Position.getX()); hitbox.setY((int)Position.getY());
        hitbox.setWidth(width);
        hitbox.setHeight(height);}
    }

    protected void getDirection(){

    }

    protected void loadGraphic(InputStream is,int width, int height, int scale){
        Bitmap bMap = BitmapFactory.decodeStream(is);
        bMap = Bitmap.createBitmap(bMap,0,0,width,height);
        bmp = new BitmapDrawable(Bitmap.createScaledBitmap(bMap,width*scale,height*scale,false));
        this.width = width*scale;
        this.height = height*scale;
        bmp.setBounds((int)Position.getX(), ((int)Position.getY())+this.height, ((int)Position.getX())+this.width, (int)Position.getY());
    } //left top right bottom

    public void draw(Canvas canvas) {
        if(bmp != null && isActive) {
            //bmp.draw(canvas);

            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
    }

    /**
     * Bitmaps have to be directly neighboured (0 pixels distance)
     * @param width
     * @param height
     * @param rows
     * @param columns
     * @return ArrayList of the textures (commonly for Animations)
     */
    public ArrayList<BitmapDrawable> loadTextures(InputStream is, int width, int height, int rows, int columns, int scale){
        ArrayList<BitmapDrawable> frames = new ArrayList<BitmapDrawable>();
        Bitmap bMap = BitmapFactory.decodeStream(is);
        Bitmap map;
        for(int i =0;i<rows;i++){
            for(int j = 0; j< columns ; j++){
                //frames.add(new TextureRegion(getTexture(),j*width,i*height,width,height));
                Log.d("ss","");
                map = Bitmap.createBitmap(bMap,j*width,i*height,width,height);
                frames.add(new BitmapDrawable(Bitmap.createScaledBitmap(map,width*scale,height*scale,false)));
            }
        }
        return frames;
    }
    protected BitmapDrawable flip(BitmapDrawable d)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d.getBitmap();
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(dst);
    }
}