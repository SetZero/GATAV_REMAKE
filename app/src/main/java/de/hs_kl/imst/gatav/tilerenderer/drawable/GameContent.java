package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.hs_kl.imst.gatav.tilerenderer.drawable.enemies.DarkAngel;
import de.hs_kl.imst.gatav.tilerenderer.util.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.drawable.enemies.Enemies;
import de.hs_kl.imst.gatav.tilerenderer.util.Constants;
import de.hs_kl.imst.gatav.tilerenderer.util.LoadingScreen;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleController;
import de.hs_kl.imst.gatav.tilerenderer.util.particles.ParticleSpawner;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;
import de.hs_kl.imst.gatav.tilerenderer.util.GameEntityFactory;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.World;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

public class GameContent implements Drawables, Observer {
    //warum muss alles static sein? :-(
    //es gibt so viele andere möglichkeiten das zu lösen...
    //-> faulheit xD
    public static GameCamera camera = new GameCamera();
    public static World world;
    //Not Thread safe! Can even return  old instance of Player (static player = evil!)
    //Spieler wird nur in einem Thread behandelt :)
    public static Player player = null;
    public Context context;
    private static HUD hud;
    private final GameEventExecutioner executioner;
    /**
     * Breite und Höhe des Levels in Pixel
     */
    private int gameWidth = -1;
    private int gameHeight = -1;
    /**
     * Der Tile Loader in welchem das Aktuelle Level geladen wird
     */
    private TileLoader tileLoader;
    private AssetManager assetManager;
    private String levelName;
    private boolean finishedSetup = false;
    private LoadingScreen loadingScreen;
    private Timer timer = new Timer();
    private AudioPlayer audioPlayer;
    private ParticleController particleController;

    /**
     * Sets up everything and starts loading of level
     * @param context the context
     * @param levelName the name of the level to load from
     * @param executioner the event executioner (e.g. return to main menu)
     * @param player Audio Player
     */
    public GameContent(Context context, String levelName, GameEventExecutioner executioner, AudioPlayer player) {
        this.context = context;
        this.assetManager = context.getAssets();
        this.levelName = levelName;
        this.executioner = executioner;
        this.audioPlayer = player;
        timer.setTotalLevelTime(getLevelTime(levelName));

        loadLevel();
        hud = new HUD(camera, timer, assetManager);
    }

    /**
     * returns the HUD
     * @return
     */
    public static HUD getHud() {
        return hud;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    /**
     * Moves the player in a direction if the setup is finished
     * @param direction
     */
    public void movePlayer(Direction direction) {
        if (finishedSetup)
            player.move(direction);
    }


    /**
     * Calls world.draw as world is managing the draw
     * @param canvas Zeichenfläche, auf die zu zeichnen ist
     */
    @Override
    public void draw(Canvas canvas) {
        if (finishedSetup) {
            world.draw(camera, canvas);
        } else {
            loadingScreen.showLoadingScreen(canvas);
        }
    }

    /**
     * calls world.update, as world is managing the update
     * @param delta
     */
    @Override
    public void update(float delta) {
        if (finishedSetup) {
            world.update(delta, camera);
            getHud().update(delta);
        }
    }

    /**
     * Loads the time of the level from the json file
     * @param levelName the name of the current level
     * @return time in seconds
     */
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

    /**
     * Starts the Tileloader and adds itself as an observer of it
     * Sets up the loading screen while the tileLoader is loading
     */
    private void loadLevel() {
        tileLoader = new TileLoader(context, levelName);
        tileLoader.addObserver(this);
        loadingScreen = new LoadingScreen(tileLoader);
        new Thread(tileLoader).start();
    }

    /**
     * Finished the loading of the level?
     * start the world and set up the rest
     */
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
        player.isActive = true;
    }

    /**
     * Generates and pass all enemies to the classes which need them
     */
    public void generateGameElements() {
        GameEntityFactory factory = new GameEntityFactory(tileLoader.getObjectGroups(), timer, world);
        player = factory.generatePlayer(context, audioPlayer);
        world.addGameObject(player);
        camera.attach(player);

        particleController = new ParticleController(tileLoader.getObjectGroups().get(Constants.collisionObjectGroupString), player);
        world.setParticleController(particleController);
        factory.setParticleController(particleController);

        //Vector2 position = new Vector2( 400, 1400);
        //ParticleSpawner particleSpawner = new ParticleSpawner(position, particleController, timer);
        //DarkAngel angle = new DarkAngel((int)position.getX(), (int)position.getY(), context, particleSpawner);
        //world.addGameObject(angle);
        //world.addParticleSpawner(particleSpawner);

        List<Enemies> enemies = factory.generateEnemies(context);
        for (Enemies enemy : enemies) {
            world.addGameObject(enemy);
        }
        List<Coin> coins = factory.generateCoins(context);
        for(Coin coin : coins) {
            world.addCollectables(coin);
        }
    }

    /**
     * Called if the TileLoader is finsiehd with loading
     * @param o most likely the TileLOader
     * @param arg Arguments
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof TileLoader) {
            TileLoader tl = (TileLoader) o;
            if (tl.isFinishedLoading()) {
                finishLoading();
            }
        }
    }

    /**
     * clean up after TileLoader is finished
     */
    public void cleanup() {
        tileLoader.cleanup();
        timer.stopTimeThread();
    }
}
