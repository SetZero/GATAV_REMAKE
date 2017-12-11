package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public class Player extends MovableGraphics {

    public Player(float x, float y) {
        super(x, y);
            try {
                InputStream is = GameContent.context.getAssets().open("dynamics/player/Player.png");
                loadGraphic(is,170,350);
                hitbox = new Rectangle((int)x,(int)y,width/10,height/10);

                isActive = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void move(Vector2 v2){
        super.move(v2);
    }
    /**
     * {@inheritDoc}
     */

    public boolean isPassable() {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
