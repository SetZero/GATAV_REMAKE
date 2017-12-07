package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.TileInformation;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public class GameContent implements Drawable {
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

    private int collectedScore = 0;
    public int getCollectedScore() { return collectedScore; }
    private Player player = null;
    private volatile Direction playerDirection = Direction.IDLE;
    synchronized public void resetPlayerDirection() { playerDirection = Direction.IDLE;}
    synchronized public boolean isPlayerDirectionIDLE() { return playerDirection == Direction.IDLE; }
    synchronized public void setPlayerDirection(Direction newDirection) { playerDirection = newDirection;}
    synchronized public Direction getPlayerDirection() { return playerDirection; }

    private Random random = new Random();
    public static Context context;
    private AssetManager assetManager;
    private String levelName;


    public GameContent(Context context, String levelName) {
        this.context = context;

        this.assetManager = context.getAssets();
        this.levelName = levelName;

        //Kamera setzen
        camera.setCameraYCenter(300);
        camera.setCameraXCenter(700);

        loadLevel();
        player = new Player(100, 100);
        dynamics.add(player);
    }


    public boolean movePlayer(Direction direction) {

        // Erster Schritt: Basierend auf Zugrichtung die Zielposition bestimmen
        int newX = -1;
        int newY = -1;
        switch(direction) {
            case UP: player.move(new Vector2(0,90),100); break;
            case RIGHT: player.move(new Vector2(900,0),100); break;
            case LEFT: player.move(new Vector2(-900,0),100); break;
        }

        return true;
    }


    @Override
    public void draw(Canvas canvas) {

        // Erste Ebene zeichnen (Wände und Boden)
        ArrayList<ArrayList<TileInformation>> map = tileLoader.getMap();
        camera.draw(canvas);
        for(ArrayList<TileInformation> currentLayerTiles : map) {
            for(TileInformation currentTile : currentLayerTiles) {

                int left = currentTile.getxPos() * tileLoader.getTileWidth();
                int top = currentTile.getyPos() * tileLoader.getTileHeight() - 150;
                int right = left + tileLoader.getTileWidth();
                int bottom = top + tileLoader.getTileHeight();
                Rect test = new Rect(left, top, right, bottom);
                if(camera.isRectInView(test)) {
                    Bitmap bmp = tileLoader.getTiles().get(currentTile.getTilesetPiece());
                    canvas.drawBitmap(bmp, left, top, null);
                }
            }
        }

        for(MovableGraphics x: dynamics){
            x.draw(canvas);
        }
    }

    @Override
    public void update(float fracsec) {
        if(camera.getCameraXCenter() < 1400) {
            camera.setCameraXCenter(camera.getCameraXCenter() + 1);
        } else {
            camera.setCameraXCenter(700);
        }

        for(MovableGraphics x: dynamics){
            x.update(fracsec);
        }

   }

    private void loadLevel() {
        tileLoader = new TileLoader(context, levelName);
        gameHeight = tileLoader.getHeight();
        gameWidth = tileLoader.getWidth();
    }

    private void spawnEnemys() {

    }
}
