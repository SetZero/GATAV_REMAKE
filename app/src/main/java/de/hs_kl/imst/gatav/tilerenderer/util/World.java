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
import de.hs_kl.imst.gatav.tilerenderer.drawable.enemies.Enemies;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleFactory;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.Spawner.ParticleSpawner;

import static de.hs_kl.imst.gatav.tilerenderer.util.Constants.enableEyeCandy;

/**
 * Main render and update loop
 * managing game objects to pass them to other objects
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

    private ParticleController particleController;
    private List<ParticleSpawner> particleSpawner = new ArrayList<>();
    private ParticleFactory particleFactory;

    /**
     * Constructor of World, loads all parameters as object variables
     * @param tileLoader The Tileloader used for the loading of a level
     * @param step inverse physics steps per second (e.g. 60steps/s = 1/60)
     * @param timer The global timer manager
     * @param executioner A executioner for View Events, like going back to title screen
     * @param audioPlayer A audio player for audio events (has separate thread!)
     */
    public World(TileLoader tileLoader, float step, Timer timer, GameEventExecutioner executioner, AudioPlayer audioPlayer) {
        this.tileLoader = tileLoader;
        this.objects = tileLoader.getObjectGroups();
        this.physics = new PhysicsController(this);
        this.gameEvents = new GameEventHandler(this.getObjects(), timer, executioner, audioPlayer, tileLoader.getAudioEventList(), particleSpawner, this);
        this.step = step;
        this.timer = timer;

        //simpleParticleSpawner = new SimpleParticleSpawner(new Vector2(400, 1400), particleController, timer);
    }

    /**
     * Adds collectables which are added while the game is running
     * @param collectable a collectable to add
     */
    public void addCollectables(Collectable collectable) {
        this.collectables.add(collectable);
        collectable.addObserver(gameEvents);
    }

    /**
     * Adds a Particle Spawner to the world
     * @param particleSpawner particle Spawner to add
     */
    public void addParticleSpawner(ParticleSpawner particleSpawner) {
        this.particleSpawner.add(particleSpawner);
    }

    /**
     * Sets the Particle Controler for this World
     * @param particleController the Particle Controller
     */
    public void setParticleController(ParticleController particleController) {
        this.particleController = particleController;
    }

    /**
     * Adds a Particle Factory which will generate all Particle Spawners
     * @param particleFactory a Particle Factory
     */
    public void setParticleFactory(ParticleFactory particleFactory) {
        this.particleFactory = particleFactory;
        gameEvents.setParticleFactory(particleFactory);
    }

    /**
     * gets all current active Objects (Zones, the Rectangles in Tiled), with their group as the key
     * @return the map
     */
    public Map<String, List<Collidable>> getObjects() {
        return objects;
    }

    /**
     * Executes Updates of all dynamic Objects, Collectables, Physics Events, gameEvents.
     * Also cleans up some unneeded resources after execution
     * @param delta inverse physics steps per second (e.g. 60steps/s = 1/60)
     * @param cam current game camera
     */
    public void update(float delta, GameCamera cam) {
        dynamicObjects.forEach(o -> o.update(delta));
        collectables.forEach(c -> c.update(delta));
        physics.Update(step, cam);
        gameEvents.update(cam);

        if(particleController != null) {
            //particleController.update(delta, cam);
            particleSpawner.removeIf(ps ->  {
                boolean remove = !ps.isActive();
                if(!remove)
                    ps.update(cam);
                return remove;
            });
        }

        dynamicObjects.removeAll(physics.getToRemove());
        gameEvents.getDynamics().removeAll(physics.getToRemove());
        physics.cleanup();
    }

    /**
     * Draws the level on the screen.
     * 1. Starting with the game camera (transform)
     * 2. It'll draw the background as one big bitmap (it's a lot faster than drawing every single
     *    tile, but also a lot more memory consuming)
     * 3. If debug build is enabled it'll draw all debug hitboxes.
     * 4. it'll draw all dynamic objects
     * 5. it'll draw all collectables (coins)
     * @param camera current game camera
     * @param canvas the canvas to draw on
     */
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
        dynamicObjects.forEach(o -> o.draw(canvas));
        collectables.forEach(c -> c.draw(canvas));

        if(particleController != null)
            particleController.draw(canvas);
    }

    /**
     * Adds a game object to the game and all elements which need it
     * @param object the object to add
     */
    public void addGameObject(MovableGraphics object) {
        dynamicObjects.add(object);
        physics.addPhysical(object);
        gameEvents.addDynamicObject(object);
        object.addObserver(gameEvents);
        if(object instanceof Enemies && particleController != null)
            particleController.addEnemy((Enemies)object);
    }

    /**
     * removes a game object
     * @param object the object to remove
     */
    public void removeGameObject(MovableGraphics object) {
        physics.removePhysical(object);
    }

    public void reset() {
        dynamicObjects.stream()
                .filter(Enemies.class::isInstance)
                .map(Enemies.class::cast)
                .forEach(enemy -> {
                    enemy.reset();
        });
    }

}
