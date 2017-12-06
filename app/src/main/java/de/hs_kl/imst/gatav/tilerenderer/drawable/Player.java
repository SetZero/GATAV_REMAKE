package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public class Player extends MovableGraphics {

    public Player(float x, float y) {
        super(x, y);
            try {
                InputStream is = GameContent.context.getAssets().open("dynamics/player/Player.png");
                loadGraphic(is,170,350);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public void move(Vector2 v2, float velocity){
        super.move(v2,velocity);
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
