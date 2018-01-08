package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Observable;

import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public abstract class MovableGraphics extends Observable implements Drawables, CollisionReactive {

    //aktuelle Geschwindigkeit des Objekts
    public boolean isOnGround = false;
    public boolean isRightColliding = false;
    public boolean isLeftColliding = false;
    public boolean isActive = false;
    protected BitmapDrawable bmp = null;
    protected boolean isFlipped = false;
    //koordinaten als Vektor
    protected Vector2 Position = new Vector2();
    protected float friction = 0.0f;
    protected Vector2 linearImpulse = new Vector2();
    protected int width, height;
    protected boolean isBouncing = false;
    protected boolean recentYImpulse = false;
    protected Direction previous;
    protected Rectangle hitbox = null;
    protected volatile Vector2 velocity = new Vector2();
    protected volatile Direction currentDirection = Direction.IDLE;
    public MovableGraphics(float x, float y) {
        this.Position = new Vector2(x, y);
    }
    public MovableGraphics(Vector2 pos) {
        this.Position = pos;
    }

    public Vector2 getLinearImpulse() {
        return linearImpulse;
    }

    public void setLinearImpulse(Vector2 linearImpulse) {
        this.linearImpulse = linearImpulse;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    //public void setSpeed(float speed) { this.speed = speed; }

    // RichtungsVektor

    public void setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
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

    /**
     * sets position and hitbox
     *
     * @param position
     */
    public void setPosition(Vector2 position) {
        Position = position;
        int offset = 0;
        if (width - hitbox.getWidth() != 0)
            offset = (width - hitbox.getWidth()) / 2;
        hitbox.setPos((int) Position.getX() + offset, (int) Position.getY());
    }

    public void impact(Vector2 direction) {
        this.velocity = Vector2.add(direction, this.velocity);
    }

    public void applyLinearImpulse(Vector2 v) {
        this.linearImpulse = Vector2.add(v, linearImpulse);
    }

    private void move(float delta) {
        Position = Vector2.add(Vector2.skalarMul(velocity, delta), Position);
    }

    private void impulse(float delta) {
        if (isOnGround && recentYImpulse && !isBouncing) {
            recentYImpulse = false;
            linearImpulse.y = 0;
        }
        if (linearImpulse.y < -40 || linearImpulse.y > 40) recentYImpulse = true;
        Position = Vector2.add(Vector2.skalarMul(linearImpulse, delta), Position);
        linearImpulse = Vector2.skalarMul(linearImpulse, 0.96f);
        if (isOnGround && linearImpulse.y > -0.01f && linearImpulse.y < 0.01f) {
            linearImpulse = Vector2.skalarMul(linearImpulse, friction);
        }
        if (linearImpulse.y > -10 && linearImpulse.y < 10) linearImpulse.y = 0;
        if (linearImpulse.x > -10 && linearImpulse.x < 10) linearImpulse.x = 0;
    }


    @Override
    public void update(float delta) {
        Rect temp = (hitbox.getRect());
        if (velocity.x < 1 && velocity.x > -1) velocity.x = 0f;
        isActive = GameContent.camera.isRectInView(temp);
        if (isActive) {
            move(delta);
            impulse(delta);
            bmp.setBounds((int) Position.getX(), (int) Position.getY() + height, (int) Position.getX() + width, (int) Position.getY());
            //offset for hitbox
            int offset = 0;
            if (width - hitbox.getWidth() != 0)
                offset = (width - hitbox.getWidth()) / 2;
            hitbox.setPos((int) Position.getX() + offset, (int) Position.getY());
            //hitbox.setX((int)Position.getX()+offset); hitbox.setY((int)Position.getY());
        }
    }

    /**
     * @param is
     * @param width
     * @param height
     * @param scale  also scales Player Hitbox
     */
    protected void loadGraphic(InputStream is, int width, int height, int scale) {
        Bitmap bMap = BitmapFactory.decodeStream(is);
        bMap = Bitmap.createBitmap(bMap, 0, 0, width, height);
        bmp = new BitmapDrawable(Bitmap.createScaledBitmap(bMap, width * scale, height * scale, false));
        this.width = width * scale;
        this.height = height * scale;
        bmp.setBounds((int) Position.getX(), ((int) Position.getY()) + this.height, ((int) Position.getX()) + this.width, (int) Position.getY());
    } //left top right bottom

    public void draw(Canvas canvas) {
        if (bmp != null && isActive) {
            canvas.drawBitmap(bmp.getBitmap(), Position.getX(), Position.getY(), null);
        }
    }

    /**
     * Bitmaps have to be directly neighboured (0 pixels distance)
     *
     * @param width
     * @param height
     * @param rows
     * @param columns
     * @return ArrayList of the textures (commonly for Animations)
     */
    public ArrayList<BitmapDrawable> loadTextures(InputStream is, int width, int height, int rows, int columns, int scale) {
        ArrayList<BitmapDrawable> frames = new ArrayList<BitmapDrawable>();
        Bitmap bMap = BitmapFactory.decodeStream(is);
        Bitmap map;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                map = Bitmap.createBitmap(bMap, j * width, i * height, width, height);
                frames.add(new BitmapDrawable(Bitmap.createScaledBitmap(map, width * scale, height * scale, false)));
            }
        }
        return frames;
    }

    protected BitmapDrawable flip(BitmapDrawable d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d.getBitmap();
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(dst);
    }
}