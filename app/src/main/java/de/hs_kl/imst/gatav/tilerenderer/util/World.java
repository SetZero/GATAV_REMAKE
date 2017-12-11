package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Drawable;
import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;
import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Created by Sebastian on 2017-12-06.
 */

public class World {
    private List<Drawable> dynamicObjects = new ArrayList<>();
    private Map<String, List<Collidable>> objects ;
    private float step;
    private PhysicsController physics;

    TileLoader tileLoader;

    public World(TileLoader tileLoader, float step) {
        this.tileLoader = tileLoader;
        objects = tileLoader.getObjectGroups();
        physics = new PhysicsController(this);
        this.step = step;
    }

    public Map<String, List<Collidable>> getObjects() {
        return objects;
    }

    public void update(float delta){
        physics.Update(step);
        for(Drawable x: dynamicObjects){
            x.update(delta);
        }
    }

    public void draw(GameCamera camera, Canvas canvas) {
        //1. Load All Tiles
        ArrayList<ArrayList<TileInformation>> map = tileLoader.getMap();
        camera.draw(canvas);
        for(ArrayList<TileInformation> currentLayerTiles : map) {
            for(TileInformation currentTile : currentLayerTiles) {
                Rect test = currentTile.getTileRect();
                if(camera.isRectInView(test)) {
                    Bitmap bmp = tileLoader.getTiles().get(currentTile.getTilesetPiece());
                    canvas.drawBitmap(bmp, test.left, test.top, null);
                }
            }
        }

        //2. Draw all Debug Hitboxes
        List<Collidable> collision = objects.get("Kollisionen");
       // Log.d("objectgroup size", ""+tileLoader.getObjectGroups().get("Kollisionen").size());
        //collision.add(GameContent.player.getHitbox());
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

    public void addGameObject(MovableGraphics object) {
        dynamicObjects.add(object);
        physics.addPhysical(object);
    }
}
