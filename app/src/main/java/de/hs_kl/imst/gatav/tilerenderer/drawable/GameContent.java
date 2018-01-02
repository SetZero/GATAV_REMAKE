package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;
import de.hs_kl.imst.gatav.tilerenderer.util.LoadingScreenTexts;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.World;

public class GameContent implements Drawables, Observer {
    private final GameEventExecutioner executioner;
    /**
     * Breite und Höhe des Levels in Pixel
     */
    private int gameWidth = -1;
    private int gameHeight = -1;
	
    //TODO wieder entfernen
    private boolean test = true;

    public static HUD getHud() {
        return hud;
    }

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
    private static HUD hud;

    private Random random = new Random();
    public static Context context; //TODO!!!!!!!!!!111111111elf: ___DON'T___ MAKE THIS STATIC: Memory leak!
    private AssetManager assetManager;
    private String levelName;

    private boolean finishedSetup = false;

    private double loadingScreenColor;
    private boolean loadingScreenFadeDirection = true;
    private String loadingScreenText;

    private Timer timer = new Timer();

    public GameContent(Context context, String levelName, GameEventExecutioner executioner) {
        this.context = context;
        this.assetManager = context.getAssets();
        this.levelName = levelName;
        this.executioner = executioner;

        //Kamera setzen
        //camera.setCameraYCenter(450);
        //camera.setCameraXCenter(700);
        int rnd = random.nextInt(LoadingScreenTexts.text.length);
        loadingScreenText = LoadingScreenTexts.text[rnd];

        loadLevel();
        hud = new HUD(camera, timer);
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
            getHud().update(delta);
            if (!player.isAlive()&& test){
                test = false;
                getHud().drawPopupMessage("You Died", 5);
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

        world = new World(tileLoader, 1f / 60f, timer, executioner);
        player = new Player(350, 500*ScaleHelper.getRatioY());
        skelett = new Robotic(900, (int) (400 * ScaleHelper.getRatioY()));
        try {
            world.addCollectables(new Coin(1200, (int) (500 * ScaleHelper.getRatioY())));
        }
        catch  (Exception e){
            e.printStackTrace();
        }
        world.addGameObject(player);
        world.addGameObject(skelett);
        camera.attach(player);
        timer.startTimeThread();
        finishedSetup = true;
    }

    public void showLoadingScreen(Canvas canvas) {
        if (loadingScreenFadeDirection) {
            if (loadingScreenColor < 255) {
                loadingScreenColor += 1;
            } else {
                loadingScreenFadeDirection = !loadingScreenFadeDirection;
            }
        } else {
            if (loadingScreenColor > 0) {
                loadingScreenColor -= 1;
            } else {
                loadingScreenFadeDirection = !loadingScreenFadeDirection;
            }
        }
        int loaded = (tileLoader != null ? tileLoader.getLoadingPercentage() : 0);
        canvas.drawARGB(255, 255, 255, 255);
        String fpsText = String.format("Loading... (%d / 100)", loaded);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);

        paint.setTextSize(20 * ScaleHelper.getRatioY());

        Rect loadingRect = new Rect();
        loadingRect.top = (int) (250 * ScaleHelper.getRatioY());
        loadingRect.left = (int) (40 * ScaleHelper.getRatioX());
        loadingRect.bottom = (int) (120 * ScaleHelper.getRatioY());
        loadingRect.right = (int)(560 *ScaleHelper.getRatioX());

        canvas.drawRect(loadingRect, paint);
        paint.setColor(Color.argb(255,  0, (int)loadingScreenColor, (int)(255 - loadingScreenColor)));
        loadingRect.right = (int) ((40 + (560 * (loaded / 100f)) * ScaleHelper.getRatioY()));
        canvas.drawRect(loadingRect, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setFakeBoldText(true);
        canvas.drawText(fpsText, (canvas.getWidth() / 2), 200 * ScaleHelper.getRatioY(), paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(20 * ScaleHelper.getRatioY() *  (float)Math.min(1.5, (50f / loadingScreenText.length())));
        canvas.drawText(loadingScreenText, (canvas.getWidth() / 2), 300 * ScaleHelper.getRatioY(), paint);

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

    public void cleanup() {
        tileLoader.cleanup();
        timer.stopTimeThread();
    }
}
