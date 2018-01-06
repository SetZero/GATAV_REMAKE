package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Constants;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;
import de.hs_kl.imst.gatav.tilerenderer.util.GameEntityFactory;
import de.hs_kl.imst.gatav.tilerenderer.util.LoadingScreenTexts;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.World;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

public class GameContent implements Drawables, Observer {
    public static GameCamera camera = new GameCamera();
    public static World world;
    //Not Thread safe! Can even return  old instance of Player (static player = evil!)
    public static Player player = null;
    public Context context;
    private static HUD hud;
    private final GameEventExecutioner executioner;
    /**
     * Breite und Höhe des Levels in Pixel
     */
    private int gameWidth = -1;
    private int gameHeight = -1;
    //TODO wieder entfernen
    private boolean test = true;
    /**
     * Der Tile Loader in welchem das Aktuelle Level geladen wird
     */
    private TileLoader tileLoader;
    private Random random = new Random();
    private AssetManager assetManager;
    private String levelName;
    private boolean finishedSetup = false;
    private double loadingScreenColor;
    private boolean loadingScreenFadeDirection = true;
    private String loadingScreenText;
    private Timer timer = new Timer();
    private AudioPlayer audioPlayer;

    public GameContent(Context context, String levelName, GameEventExecutioner executioner, AudioPlayer player) {
        this.context = context;
        this.assetManager = context.getAssets();
        this.levelName = levelName;
        this.executioner = executioner;
        this.audioPlayer = player;
        timer.setTotalLevelTime(getLevelTime(levelName));
        //Kamera setzen
        //camera.setCameraYCenter(450);
        //camera.setCameraXCenter(700);
        int rnd = random.nextInt(LoadingScreenTexts.text.length);
        loadingScreenText = LoadingScreenTexts.text[rnd];

        loadLevel();
        hud = new HUD(camera, timer, assetManager);
    }

    public static HUD getHud() {
        return hud;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public void movePlayer(Direction direction) {
        if (finishedSetup)
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
            if (!player.isAlive() && test) {
                test = false;
                getHud().drawPopupMessage("You Died", 5);
            }
        }
    }

    private int getLevelTime(String levelName) {
        try {
            InputStream is = assetManager.open(Constants.worldInfoSaveLocation + Constants.worldInfoFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray("maps");
            for (int i = 0; i < jArray.length(); i++){
                JSONObject oneObject = jArray.getJSONObject(i);
                String saveLocation = oneObject.getString("saveLocation");
                if(saveLocation.equals(levelName)) {
                    return oneObject.getInt("time");
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return -1;
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

        world = new World(tileLoader, 1f / 60f, timer, executioner, audioPlayer);
        generateGameElements();

        audioPlayer.setPlayerCharacter(player);
        timer.startTimeThread();
        finishedSetup = true;
    }

    public void generateGameElements() {
        GameEntityFactory factory = new GameEntityFactory(tileLoader.getObjectGroups());
        player = factory.generatePlayer(context, audioPlayer);
        world.addGameObject(player);
        camera.attach(player);

        List<Enemies> enemies = factory.generateEnemies(context);
        for (Enemies enemy : enemies) {
            world.addGameObject(enemy);
        }

        List<Coin> coins = factory.generateCoins(context);
        for(Coin coin : coins) {
            world.addCollectables(coin);
        }
    }

    public void showLoadingScreen(Canvas canvas) {
        if (loadingScreenFadeDirection) {
            if (loadingScreenColor < 255) {
                loadingScreenColor += 1;
            } else {
                loadingScreenFadeDirection = false;
            }
        } else {
            if (loadingScreenColor > 0) {
                loadingScreenColor -= 1;
            } else {
                loadingScreenFadeDirection = true;
            }
        }
        int loaded = (tileLoader != null ? tileLoader.getLoadingPercentage() : 0);
        canvas.drawARGB(255, 255, 255, 255);
        String fpsText = String.format(Locale.GERMAN, "Loading... (%d / 100)", loaded);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);

        paint.setTextSize(20 * ScaleHelper.getRatioY());

        Rect loadingRect = new Rect();
        loadingRect.top = (int) (250 * ScaleHelper.getRatioY());
        loadingRect.left = (int) (40 * ScaleHelper.getRatioX());
        loadingRect.bottom = (int) (120 * ScaleHelper.getRatioY());
        loadingRect.right = (int) (560 * ScaleHelper.getRatioX());

        canvas.drawRect(loadingRect, paint);
        paint.setColor(Color.argb(255, 0, (int) loadingScreenColor, (int) (255 - loadingScreenColor)));
        loadingRect.right = (int) ((40 + (560 * (loaded / 100f)) * ScaleHelper.getRatioY()));
        canvas.drawRect(loadingRect, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setFakeBoldText(true);
        canvas.drawText(fpsText, (canvas.getWidth() / 2), 200 * ScaleHelper.getRatioY(), paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(20 * ScaleHelper.getRatioY() * (float) Math.min(1.5, (50f / loadingScreenText.length())));
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
