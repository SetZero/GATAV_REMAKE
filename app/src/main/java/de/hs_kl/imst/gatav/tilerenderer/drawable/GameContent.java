package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.service.quicksettings.Tile;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.World;

public class GameContent implements Drawables, Observer {
    /**
     * Breite und Höhe des Levels in Pixel
     */
    private int gameWidth = -1;
    private int gameHeight = -1;
    public int getGameWidth() { return gameWidth; }
    public int getGameHeight() { return gameHeight; }

    /**
     * Der Tile Loader in welchem das Aktuelle Level geladen wird
     */
    private TileLoader tileLoader;
    public static GameCamera camera = new GameCamera();
    private ArrayList<MovableGraphics> dynamics = new ArrayList<>();
    private int collectedTargets = 0;
    public int getCollectedTargets() { return collectedTargets; }
    public static World world;
    private int collectedScore = 0;
    public int getCollectedScore() { return collectedScore; }
    public static Player player = null;

    Robotic skelett;

    private Random random = new Random();
    public static Context context;
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
    }


    public boolean movePlayer(Direction direction) {
        if(finishedSetup)
            player.move(direction);
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        if(finishedSetup) {
            world.draw(camera, canvas);
        } else {
            showLoadingScreen(canvas);
        }
    }

    @Override
    public void update(float delta) {
        if(finishedSetup)
            world.update(delta,camera);
   }

    private void loadLevel() {
        tileLoader = new TileLoader(context, levelName);
        tileLoader.addObserver(this);
        new Thread(tileLoader).start();
    }

    private void finishLoading() {
        gameHeight = tileLoader.getHeight();
        gameWidth = tileLoader.getWidth();
        camera.setLevelHeight(gameHeight * tileLoader.getTileHeight());
        camera.setLevelWidth(gameWidth * tileLoader.getTileWidth());

        world = new World(tileLoader,1f/60f);
        player = new Player(350, 1050);
        skelett = new Robotic(600,1050);
        world.addGameObject(player);
        world.addGameObject(skelett);
        camera.attach(player);

        finishedSetup = true;
    }

    public void showLoadingScreen(Canvas canvas) {
        String fpsText = String.format("Loading...");

        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(fpsText, 10, 50, paint);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof TileLoader) {
            TileLoader tl = (TileLoader) o;
            if(tl.isFinishedLoading()) {
                finishLoading();
            }
        }
    }
}
