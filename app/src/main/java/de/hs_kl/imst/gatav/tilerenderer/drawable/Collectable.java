package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

/**
 * Created by keven on 31.12.2017.
 */

public abstract class Collectable extends Observable implements Drawables {

    protected int width, height;
    protected Rectangle Hitbox;
    protected Vector2 Position;
    private BitmapDrawable bmp;
    private boolean isCollected = false;

    public Collectable(int x, int y, int width, int height, Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            Position = new Vector2(x, y);
            loadGraphic(is, width, height, ScaleHelper.getEntitiyScale() / 2);
            Hitbox = new Rectangle(x, y, this.width, this.height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public Rectangle getHitbox() {
        return Hitbox;
    }

    public void setPosition(Vector2 position) {
        Position = position;
        int offset = 0;
        if (width - Hitbox.getWidth() != 0)
            offset = (width - Hitbox.getWidth()) / 2;
        Hitbox.setPos((int) Position.getX() + offset, (int) Position.getY());
    }

    private void loadGraphic(InputStream is, int width, int height, int scale) {
        Bitmap bMap = BitmapFactory.decodeStream(is);
        bMap = Bitmap.createBitmap(bMap, 0, 0, width, height);
        bmp = new BitmapDrawable(Bitmap.createScaledBitmap(bMap, width * scale, height * scale, false));
        this.width = width * scale;
        this.height = height * scale;
        bmp.setBounds((int) Position.getX(), ((int) Position.getY()) + this.height, ((int) Position.getX()) + this.width, (int) Position.getY());
    }

    @Override
    public void update(float delta) {
        if (!isCollected && GameContent.player.getHitbox().isCollidingWith(Hitbox)) {
            onCollect();
            this.isCollected = true;
        }
    }

    protected abstract void onCollect();

    @Override
    public void draw(Canvas canvas) {
        if (!isCollected)
            canvas.drawBitmap(bmp.getBitmap(), Position.getX(), Position.getY(), null);
    }

    /*
    @Override
    public void onCollision(Contact c){
        if(c.collisionObject instanceof  Player && c.siteHit != PhysicsController.intersectDirection.DONT){
            isCollected = true;
        }
    }
    */
}
