package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.World;

public class GameContent implements Drawables {
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
    private GameCamera camera = new GameCamera();
    private ArrayList<MovableGraphics> dynamics = new ArrayList<>();
    private int collectedTargets = 0;
    public int getCollectedTargets() { return collectedTargets; }
    private World world;
    private int collectedScore = 0;
    public int getCollectedScore() { return collectedScore; }
    public static Player player = null;
    private volatile Direction playerDirection = Direction.IDLE;
    synchronized public void resetPlayerDirection() { playerDirection = Direction.IDLE;}
    synchronized public boolean isPlayerDirectionIDLE() { return playerDirection == Direction.IDLE; }
    synchronized public void setPlayerDirection(Direction newDirection) { playerDirection = newDirection;}
    synchronized public Direction getPlayerDirection() { return playerDirection; }
    Skeletton skelett;

    private Random random = new Random();
    public static Context context;
    private AssetManager assetManager;
    private String levelName;


    public GameContent(Context context, String levelName) {
        this.context = context;

        this.assetManager = context.getAssets();
        this.levelName = levelName;

        //Kamera setzen
        //camera.setCameraYCenter(450);
        //camera.setCameraXCenter(700);

        loadLevel();
        world = new World(tileLoader,1f/60f);
        player = new Player(350, 650);
        skelett = new Skeletton(900,650);
        world.addGameObject(player);
        world.addGameObject(skelett);
        camera.attach(player);
    }


    public boolean movePlayer(Direction direction) {
        player.move(direction);
        return true;
    }


    @Override
    public void draw(Canvas canvas) {
        world.draw(camera,canvas);
    }

    @Override
    public void update(float delta) {
        world.update(delta,camera);
   }

    private void loadLevel() {
        tileLoader = new TileLoader(context, levelName);
        gameHeight = tileLoader.getHeight();
        gameWidth = tileLoader.getWidth();
        camera.setLevelHeight(gameHeight * tileLoader.getTileHeight());
        camera.setLevelWidth(gameWidth * tileLoader.getTileWidth());
    }

    private void spawnEnemys() {

    }
}
