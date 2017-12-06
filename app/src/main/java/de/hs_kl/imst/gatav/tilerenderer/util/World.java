package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Drawable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Created by Sebastian on 2017-12-06.
 */

public class World {
    private List<Drawable> dynamicObjects = new ArrayList<>();
    TileLoader tileLoader;

    public World(TileLoader tileLoader) {
        this.tileLoader = tileLoader;
    }

    public void draw(GameCamera camera, Canvas canvas) {
        //1. Load All Tiles
        ArrayList<ArrayList<TileInformation>> map = tileLoader.getMap();
        for(ArrayList<TileInformation> currentLayerTiles : map) {
            for(TileInformation currentTile : currentLayerTiles) {

                int left = currentTile.getxPos() * tileLoader.getTileWidth();
                int top = currentTile.getyPos() * tileLoader.getTileHeight();
                int right = left + tileLoader.getTileWidth();
                int bottom = top + tileLoader.getTileHeight();
                Rect test = new Rect(left, top, right, bottom);
                if(camera.isRectInView(test)) {
                    //Log.d("GameContent", "In View: " + test.left + ", " + test.top);
                    Bitmap bmp = tileLoader.getTiles().get(currentTile.getTilesetPiece());
                    canvas.drawBitmap(bmp, left, top, null);
                }
            }
        }

        //2. Draw all Debug Hitboxes
        Map<String, List<Collidable>> groups =  tileLoader.getObjectGroups();
        List<Collidable> collision = groups.get("Kollisionen");
        Paint p = new Paint();
        p.setColor(Color.argb(128, 0, 65, 200));
        if(collision != null) {
            for(Collidable collidable : collision) {
                if(collidable instanceof Rectangle) {
                    Rect r = ((Rectangle) collidable).getRect();
                    if(camera.isRectInView(r)) {
                        canvas.drawRect(r, p);
                    }
                }
            }
        }

        //3. Draw all Dynamic Objects
        for(Drawable object : dynamicObjects) {
            object.draw(canvas);
        }

    }

    public void addGameObject(Drawable object) {
        dynamicObjects.add(object);
    }
}
