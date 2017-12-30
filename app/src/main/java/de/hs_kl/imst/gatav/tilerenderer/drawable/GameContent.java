package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.codemonkeylabs.fpslibrary.TinyDancer;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.World;

public class GameContent implements Drawables, Observer {
    public static GameCamera camera = new GameCamera();
    public static World world;
    public static Player player = null;
    public static Context context;
    public Robotic skelett;
    /**
     * Breite und Höhe des Levels in Pixel
     */
    private int gameWidth = -1;
    private int gameHeight = -1;
	
    //TODO wieder entfernen
    private boolean test = true;
    public int getGameWidth() { return gameWidth; }
    public int getGameHeight() { return gameHeight; }

    /**
     * Der Tile Loader in welchem das Aktuelle Level geladen wird
     */
    private TileLoader tileLoader;

    public static GameCamera camera = new GameCamera();
    public static World world;
    public static Player player = null;

    public Robotic skelett;
    public  HUD hud;

    private Random random = new Random();
    private AssetManager assetManager;
    private String levelName;
    private boolean finishedSetup = false;
    public GameContent(Context context, String levelName) {
        this.context = context;
        this.assetManager = context.getAssets();
        this.levelName = levelName;

        //Kamera setzen
        //camera.setCameraYCenter(450);
        //camera.setCameraXCenter(700);

        loadLevel();
        hud = new HUD(camera);
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public int getCollectedTargets() {
        return collectedTargets;
    }

    public int getCollectedScore() {
        return collectedScore;
    }


    public void movePlayer(Direction direction) {
        if(finishedSetup)
            player.move(direction);
    }


    @Override
    public void draw(Canvas canvas) {
        if (finishedSetup) {
            world.draw(camera, canvas);
        } else {
            showLoadingScreen(canvas);
        }
    }

    @Override
    public void update(float delta) {
        if (finishedSetup) {
            world.update(delta, camera);
            hud.update(delta);
            if (!player.isAlive()&& test){
                test = false;
                hud.drawPopupMessage("You Died", 5);
            }
        }
    }

    private void loadLevel() {
        tileLoader = new TileLoader(context, levelName);
        tileLoader.addObserver(this);
        new Thread(tileLoader).start();
    }

    private void finishLoading() {
        Log.d("GameContent", "" + tileLoader.getTileHeight() * tileLoader.getTileHeight());
        gameHeight = tileLoader.getHeight();
        gameWidth = tileLoader.getWidth();
        camera.setLevelHeight(gameHeight * tileLoader.getTileHeight());
        camera.setLevelWidth(gameWidth * tileLoader.getTileWidth());
        world = new World(tileLoader, 1f / 60f);
        player = new Player(350, 500*ScaleHelper.getRatioY());
        skelett = new Robotic(100, (int) (400 * ScaleHelper.getRatioY()));
        for(int i=0;i<20;i++) {
            world.addGameObject(new Robotic(100 + (i*20), (int) (400 * ScaleHelper.getRatioY())));
        }
        world.addGameObject(player);
        world.addGameObject(skelett);
        camera.attach(player);
        finishedSetup = true;
    }

    public void showLoadingScreen(Canvas canvas) {
        String fpsText = String.format("Loading... (%d / 100)", tileLoader.getLoadingPercentage());

        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20 * ScaleHelper.getRatioY());
        canvas.drawText(fpsText, 10 * ScaleHelper.getRatioY(), 50 * ScaleHelper.getRatioY(), paint);

        Rect loadingRect = new Rect();
        loadingRect.top = (int) (100 * ScaleHelper.getRatioY());
        loadingRect.left = (int) (40 * ScaleHelper.getRatioX());
        loadingRect.bottom = (int) (120 * ScaleHelper.getRatioY());
        loadingRect.right = (int) ((40 + (560 * (tileLoader.getLoadingPercentage() / 100f)) * ScaleHelper.getRatioY()));

        canvas.drawRect(loadingRect, paint);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof TileLoader) {
            TileLoader tl = (TileLoader) o;
            if (tl.isFinishedLoading()) {
                finishLoading();
            }
        }
    }
}
