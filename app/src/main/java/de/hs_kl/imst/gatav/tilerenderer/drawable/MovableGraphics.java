package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public abstract class MovableGraphics implements Drawable {

    protected BitmapDrawable bmp = null;
    //koordinaten als Vektor
    protected Vector2 Position = new Vector2();
    //aktuelle Geschwindigkeit des Objekts
    protected float velocity = 0.0f;
    protected int width, height;

    public Collidable getHitbox() {
        return hitbox;
    }

    public void setHitbox(Collidable hitbox) {
        this.hitbox = hitbox;
    }

    protected Collidable hitbox = null;

    //public void setSpeed(float speed) { this.speed = speed; }

    // RichtungsVektor

    public Vector2 getDirectionVec() {
        return directionVec;
    }

    public void setDirectionVec(Vector2 directionVec) {
        this.directionVec = directionVec;
    }

    protected Vector2 directionVec = new Vector2();

    protected volatile Direction currentDirection = Direction.IDLE;
    synchronized public boolean isMoving() { return currentDirection != Direction.IDLE; }
    synchronized protected void setMovingDirection(Direction newDirection) { currentDirection = newDirection; }


    public MovableGraphics(float x, float y) {
        this.Position = new Vector2(x,y);
    }

    public MovableGraphics(Vector2 pos) {
        this.Position = pos;
    }

    public void move(Vector2 direction, float velocity) {

        if(0f > direction.getY() )
            setMovingDirection(Direction.UP);
        else if(0f > direction.getX() )
            setMovingDirection(Direction.LEFT);
        else if(0f < direction.getX() )
            setMovingDirection(Direction.RIGHT);
        else setMovingDirection(Direction.IDLE);

        this.directionVec = direction;
        this.velocity = velocity;
    }

    public float getVelocity(){
        return velocity;
    }
    public void setVelocity(float velocity){
        this.velocity = velocity;
    }

    private void move(float delta) {
        if(velocity > 1.0f ) {
            Position = Vector2.nextPoint(Position, directionVec, velocity * delta);
        }
    }

    @Override
    public void update(float delta) {
        move(delta);
        bmp.setBounds((int)Position.getX(), (int)Position.getY()+height, (int)Position.getX()+width, (int)Position.getY());
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