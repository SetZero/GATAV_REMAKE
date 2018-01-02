package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Collectable;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Drawables;
import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Created by Sebastian on 2017-12-06.
 */

public class World {
    private final Timer timer;
    TileLoader tileLoader;
    private List<Drawables> dynamicObjects = new ArrayList<>();
    private List<Collectable> collectables = new ArrayList<>();
    private Map<String, List<Collidable>> objects;
    private float step;
    private PhysicsController physics;
    private GameEventHandler gameEvents;

    public World(TileLoader tileLoader, float step, Timer timer) {
        this.tileLoader = tileLoader;
        objects = tileLoader.getObjectGroups();
        physics = new PhysicsController(this);
        gameEvents = new GameEventHandler(this.getObjects(), timer);
        this.step = step;
        this.timer = timer;
    }

    public List<Collectable> getCollectables() {
        return collectables;
    }

    public void addCollectables(Collectable collectable) {
        this.collectables.add(collectable);
    }

    public Map<String, List<Collidable>> getObjects() {
        return objects;
    }

    public void update(float delta, GameCamera cam) {
        for (Drawables x : dynamicObjects) {
            x.update(delta);
        }
        for (Collectable x : collectables) {
            x.update(delta);
        }
        physics.Update(step, cam);
        gameEvents.update(cam);

        dynamicObjects.removeAll(physics.getToRemove());
        gameEvents.getDynamics().removeAll(physics.getToRemove());
        physics.cleanup();
    }

    public void draw(GameCamera camera, Canvas canvas) {
        //1. Load All Tiles
        //List<List<TileInformation>> map = tileLoader.getMap();
        camera.draw(canvas);
        /* Why is this so slow? Why is transparency so slow... :-/
        for (List<TileInformation> currentLayerTiles : map) {
            for (TileInformation currentTile : currentLayerTiles) {
                Rect test = currentTile.getTileRect();
                if (camera.isRectInView(test)) {
                    Bitmap bmp = tileLoader.getTiles().get(currentTile.getTilesetPiece());
                    if (bmp != null)
                        canvas.drawBitmap(bmp, test.left, test.top, null);
                }
            }
        }*/
        //Screw RAM, modern Smartphones have 4GB of RAM anyways...
        canvas.drawBitmap(tileLoader.getSceneBitmap(), 0, 0, null);

        //2. Draw all Debug Hitboxes
        if (Constants.debugBuild) {
            for (Map.Entry<String, List<Collidable>> entry : objects.entrySet()) {
                Paint color = new Paint();
                switch (entry.getKey()) {
                    case Constants.collisionObjectGroupString:
                        color.setColor(Color.argb(128, 0, 65, 200));
                        break;
                    case Constants.finishObjectGroupString:
                        color.setColor(Color.argb(128, 255, 255, 0));
                        break;
                    case Constants.checkpointsObjectGroupString:
                        color.setColor(Color.argb(128, 0, 185, 0));
                        break;
                    default:
                        color.setColor(Color.argb(128, 255, 255, 255));
                }
                if (entry.getValue() != null) {
                    for (Collidable collidable : entry.getValue()) {
                        if (collidable instanceof Rectangle) {
                            Rect r = ((Rectangle) collidable).getRect();
                            if (camera.isRectInView(r)) {
                                canvas.drawRect(r, color);
                            }
                        }
                    }
                }
            }
        }

        //3. Draw all Dynamic Objects
        for (Drawables object : dynamicObjects) {
            object.draw(canvas);
        }
        for (Collectable x : collectables) {
            x.draw(canvas);
        }
    }

    public void addGameObject(MovableGraphics object) {
        dynamicObjects.add(object);
        physics.addPhysical(object);
        gameEvents.addDynamicObject(object);
    }

    public void removeGameObject(MovableGraphics object) {
        physics.removePhysical(object);
    }

}
