package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public abstract class MovableGraphics implements Drawable {
    //private float speed = 10f;
    protected BitmapDrawable bmp = null;
    protected Vector2 Position = new Vector2();
    protected float velocity = 0.0f;
    protected int width, height;

    //public void setSpeed(float speed) { this.speed = speed; }

    // Bewegungskoordinaten
    // Gleitkomma-Koordinaten zur Bewegung zwischen zwei Kacheln
    protected Vector2 directionVec = new Vector2();

    protected volatile Direction currentDirection = Direction.IDLE;  // aktuell keine Bewegung
    synchronized public boolean isMoving() { return currentDirection != Direction.IDLE; }
    synchronized protected void setMovingDirection(Direction newDirection) { currentDirection = newDirection; }


    public MovableGraphics(float x, float y) {
        this.Position = new Vector2(x,y);
    }

    public MovableGraphics(Vector2 pos) {
        this.Position = pos;
    }

    public void move(Vector2 direction, float velocity) {
        // einmalig die Bewegung festlegen
        // mittels der gesetzten Direction lassen sich auch weitere Eingaben blocken,
        // bis die Bewegung schließlich (mittels updates) komplett durchgeführt wurde
        if(0f > direction.getY() )
            setMovingDirection(Direction.UP);
        else if(0f > direction.getX() )
            setMovingDirection(Direction.LEFT);
        else if(0f < direction.getX() )
            setMovingDirection(Direction.RIGHT);
        else setMovingDirection(Direction.IDLE);


        // Quelle und Zielblock festlegen

        this.directionVec = direction;
        this.velocity = velocity;

        // normaler Move,  vorab logisch schon einmal auf die neue Kachel vornehmen
    }

    private void move(float delta) {
        if(velocity > 1.0f ) {
            Position = Vector2.nextPoint(Position, directionVec, velocity * delta);

            //velocity *= delta;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(float delta) {
        move(delta);
        bmp.setBounds((int)Position.getX(), (int)Position.getY()+height, (int)Position.getX()+width, (int)Position.getY());
    }

    protected void getDirection(){

    }

    /**
     * {@inheritDoc}
     */
    protected void loadGraphic(InputStream is,int widht, int height){
        this.width = widht; this.height = height;
        Bitmap bMap = BitmapFactory.decodeStream(is);
        bmp = new BitmapDrawable(bMap);
        bmp.setBounds((int)Position.getX(), ((int)Position.getY())+height, ((int)Position.getX())+widht, (int)Position.getY());
    } //left top right bottom

    public void draw(Canvas canvas) {
        if(bmp != null) {
            //bmp.draw(canvas);
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
       /* // Aktuelle Transformationsmatrix speichern<
        canvas.save();
        // Transformationsmatrix an Pixel-Koordinate von Block verschieben
        canvas.translate(Vector2.nextPoint(), currentY * velocity);
        // An der aktuellen Position ein Rechteck entsprechender Größe oder die existierende Bitmap
        if(bmp == null)
            //canvas.drawRect(0, 0, tileSize, tileSize, tilePaint);
        else
            canvas.drawBitmap(bmp, 0 , 0, null);
        // Transformationsmatrix auf den Stand von vorherigem canvas.save() zurücksetzen
        canvas.restore();
        */
    }
}