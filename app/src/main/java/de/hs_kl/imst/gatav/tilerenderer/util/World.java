package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Collectable;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Drawables;
import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

import static de.hs_kl.imst.gatav.tilerenderer.util.Constants.enableEyeCandy;

/**
 * Created by Sebastian on 2017-12-06.
 */

public class World {
    private final Timer timer;
    private TileLoader tileLoader;
    private List<Drawables> dynamicObjects = new ArrayList<>();
    private List<Collectable> collectables = new ArrayList<>();
    private Map<String, List<Collidable>> objects;
    private float step;
    private PhysicsController physics;
    private GameEventHandler gameEvents;

    public World(TileLoader tileLoader, float step, Timer timer, GameEventExecutioner executioner, AudioPlayer audioPlayer) {
        this.tileLoader = tileLoader;
        objects = tileLoader.getObjectGroups();
        physics = new PhysicsController(this);
        gameEvents = new GameEventHandler(this.getObjects(), timer, executioner, audioPlayer, tileLoader.getAudioEventList());
        this.step = step;
        this.timer = timer;
    }

    public void addCollectables(Collectable collectable) {
        this.collectables.add(collectable);
        collectable.addObserver(gameEvents);
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
        camera.draw(canvas);
        /* Why is this so slow? Why is transparency so slow... :-/
        List<List<TileInformation>> map = tileLoader.getMap();
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

        //Scaling is slow...
        /*Rect tmp = camera.getCameraViewRect();
        Matrix matrix = new Matrix();
        matrix.postScale(ScaleHelper.getRatioX(), ScaleHelper.getRatioY());
        Bitmap bmp = tileLoader.getSceneBitmap();
        Bitmap croppedBitmap = Bitmap.createBitmap(bmp, (int)(tmp.left / ScaleHelper.getRatioX()), (int)(tmp.top / ScaleHelper.getRatioY()), (int)(tmp.width() / ScaleHelper.getRatioX()), (int)(tmp.height() / ScaleHelper.getRatioY()), matrix, true);
        canvas.drawBitmap(croppedBitmap, tmp.left, tmp.top, null);*/

        //Screw RAM, modern Smartphones have 4GB of RAM anyways...

        //Draw parallax background
        if (enableEyeCandy) {
            canvas.drawARGB(255, 109, 165, 255);
            Bitmap background = tileLoader.getBackgroundBitmap();
            if (background != null) {
                int firstBackgroundOffset = 0;
                int secondBackgroundOffset = 0;
                final double parallaxOffset = 1.2;
                int backgroundLeftPosition = (int) (camera.getCameraViewRect().left / parallaxOffset);
                if (firstBackgroundOffset + backgroundLeftPosition + background.getWidth() < camera.getCameraViewRect().right) {
                    secondBackgroundOffset = secondBackgroundOffset + background.getWidth();
                }
                if (secondBackgroundOffset + backgroundLeftPosition + background.getWidth() < camera.getCameraViewRect().right) {
                    firstBackgroundOffset = secondBackgroundOffset + background.getWidth();
                }

                final int firstLeftPosition = firstBackgroundOffset + backgroundLeftPosition;
                final int firstTopPosition = camera.getLevelHeight() - background.getHeight();
                canvas.drawBitmap(background, firstLeftPosition, firstTopPosition, null);

                final int secondLeftPosition = secondBackgroundOffset + backgroundLeftPosition;
                final int secondTopPosition = camera.getLevelHeight() - background.getHeight();
                canvas.drawBitmap(background, secondLeftPosition, secondTopPosition, null);
            }
        }
        //draw main level
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
        object.addObserver(gameEvents);
    }

    public void removeGameObject(MovableGraphics object) {
        physics.removePhysical(object);
    }

}
