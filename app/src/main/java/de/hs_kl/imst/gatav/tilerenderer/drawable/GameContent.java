package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.TileInformation;

public class GameContent implements Drawable {
    /**
     * Breite und Höhe des Spielfeldes in Pixel
     */
    private int gameWidth = -1;
    private int gameHeight = -1;
    public int getGameWidth() { return gameWidth; }
    public int getGameHeight() { return gameHeight; }

    /**
     * Der Tile Loader in welchem das Aktuelle Level geladen wird
     */
    private TileLoader tileLoader;

    /**
     * Game Camera
     */
    private GameCamera camera = new GameCamera();

    /**
     * Beinhaltet Referenzen auf alle dynamischen Kacheln, deren {@link Drawable#update(float)} Methode
     * aufgerufen werden muss. Damit lassen sich Kachel-Animationen durchführen.
     */
    private ArrayList<TileGraphics> dynamicTiles = new ArrayList<>();

    /**
     * Beinhaltet alle Ziele. Diese werden als zweites und somit über die in
     * definierten Elemente gezeichnet.
     */
    private TileGraphics[][] targetTiles;   // [zeilen][spalten]

    /**
     * Beinhaltet Referenzen auf alle Ziele
     */
    private ArrayList<TileGraphics> targets = new ArrayList<>();

    /**
     * Beinhaltet Referenzen auf Kacheln (hier alle vom Typ {@link Floor}), auf welchen ein Ziel
     * erscheinen kann.
     */
    private ArrayList<TileGraphics> possibleTargets = new ArrayList<>();

    /**
     * Anzahl der eingesammelten Ziele
     */
    private int collectedTargets = 0;
    public int getCollectedTargets() { return collectedTargets; }

    /**
     * Anzahl der gesammelten Punkte
     */
    private int collectedScore = 0;
    public int getCollectedScore() { return collectedScore; }

    /**
     * Beinhaltet Referenz auf Spieler, der bewegt wird.
     */
    private Player player = null;

    /**
     * Dynamisches Ziel
     */
    private DynamicTarget dynTarget = null;

    public DynamicTarget getDynTarget() { return dynTarget;}

    /**
     * Wird in {@link GameContent#movePlayer(Direction)} verwendet, um dem Game Thread
     * die Bewegungsrichtung des Players zu übergeben.
     * Wird vom Game Thread erst auf IDLE zurückgesetzt, sobald die Animation abgeschlossen ist
     */
    private volatile Direction playerDirection = Direction.IDLE;
    synchronized public void resetPlayerDirection() { playerDirection = Direction.IDLE;}
    synchronized public boolean isPlayerDirectionIDLE() { return playerDirection == Direction.IDLE; }
    synchronized public void setPlayerDirection(Direction newDirection) { playerDirection = newDirection;}
    synchronized public Direction getPlayerDirection() { return playerDirection; }

    /**
     * Zufallszahlengenerator zum Hinzufügen neuer Ziele
     */
    private Random random = new Random();


    private Context context;

    /**
     * {@link AssetManager} über den wir unsere Leveldaten beziehen
     */
    private AssetManager assetManager;

    /**
     * Name des Levels
     */
    private String levelName;


    /**
     * @param context TODO <insert wise words here :-)/>
     * @param levelName Name des zu ladenden Levels
     */
    public GameContent(Context context, String levelName) {
        this.context = context;
        this.assetManager = context.getAssets();
        this.levelName = levelName;

        //Kamera setzen
        camera.setCameraYCenter(300);
        camera.setCameraXCenter(700);

        // Level laden mit Wall (W), Floor (F) und Player (P)
        // Target wird im geladenen Level zum Schluss zusätzlich gesetzt
        loadLevel();

        // Player ist animiert und muss deshalb updates auf seine Position erfahren
        //dynamicTiles.add(player);
    }


    /**
     * Überprüfung der Möglichkeit einer Verschiebung des Players in eine vorgegebene Richtung
     * Geprüft wird auf Spielfeldrand und Hindernisse.
     * Falls das zulässige Zielfeld ein Target ist, wird dieses konsumiert und ein neues Target gesetzt.
     * Dann wird die Bewegung des Players durchgeführt bzw. angestoßen (Animation)
     *
     * @param direction Richtung in die der Player bewegt werden soll
     * @return true falls Zug erfolgreich durchgeführt bzw. angestoßen, false falls Zug nicht durchgeführt
     */
    public boolean movePlayer(Direction direction) {

        // Erster Schritt: Basierend auf Zugrichtung die Zielposition bestimmen
        int newX = -1;
        int newY = -1;
        switch(direction) {
            case UP: newX = player.getX(); newY = player.getY() - 1; break;
            case DOWN: newX = player.getX(); newY = player.getY() + 1; break;
            case RIGHT: newX = player.getX() + 1; newY = player.getY(); break;
            case LEFT: newX = player.getX() - 1; newY = player.getY(); break;
        }
        if ((!(newX >= 0 && newX < gameWidth && newY >= 0 && newY < gameHeight)))
            throw new AssertionError("Spieler wurde außerhalb des Spielfeldes bewegt. Loch im Level?");

        // Zweiter Schritt: Prüfen ob Spieler sich an Zielposition bewegen kann (Zielkachel.isPassable())

        // Dritter Schritt: Spieler verschieben bzw. Verschieben starten.
        // Hinterher steht der Spieler logisch bereits auf der neuen Position

        // Vierter Schritt: Prüfen ob auf der Zielkachel ein Target existiert


        // Prüfen ob auf der Zielposition das dynamische Target existert => Sonderpunkte :-)


        return true;
    }


    /**
     * Spielinhalt zeichnen
     * @param canvas Zeichenfläche, auf die zu Zeichnen ist
     */
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


        // Zweite Ebene zeichnen

        // Dynamisches Ziel zeichnen
        // Spieler zeichnen
    }


    /**
     * Spielinhalt aktualisieren (hier Player und Animation dynamischer Kacheln)
     * @param fracsec Teil einer Sekunde, der seit dem letzten Update des gesamten Spielzustandes vergangen ist
     */
    @Override
    public void update(float fracsec) {
        if(camera.getCameraXCenter() < 1400) {
            camera.setCameraXCenter(camera.getCameraXCenter() + 1);
        } else {
            camera.setCameraXCenter(700);
        }
        // 1. Schritt: Auf mögliche Player Bewegung prüfen und ggf. durchführen/anstoßen
        // vorhandenen Player Move einmalig ausführen bzw. anstoßen, falls
        // PlayerDirection nicht IDLE ist und Player aktuell nicht in einer Animation
        //Log.d("updateGameContent", ""+isPlayerDirectionIDLE()+" "+player.isMoving());

        // Dynamisches Ziel vielleicht erzeugen


        // 2. Schritt: Updates bei allen dynamischen Kacheln durchführen (auch Player)


        // 3. Schritt: Animationen auf Ende überprüfen und ggf. wieder freischalten
        // Player Move fertig ausgeführt => Sperre für neues Player Event freischalten

        // Animation des dynamischen Ziels abgeschlossen

   }


    /**
     * Level aus Stream laden und Datenstrukturen entsprechend initialisieren
     * @throws IOException falls beim Laden etwas schief geht (IO Fehler, Fehler in Leveldatei)
     */
    private void loadLevel() {
        // Erster Schritt: Leveldatei zeilenweise lesen und Inhalt zwischenspeichern. Zudem ermitteln, wie breit der Level maximal ist.
        // Spielfeldgröße ermitteln

        tileLoader = new TileLoader(context, "test");
        gameHeight = tileLoader.getHeight();
        gameWidth = tileLoader.getWidth();


        // Zweiter Schritt: basierend auf dem Inhalt der Leveldatei die Datenstrukturen befüllen


        // Dritter Schritt: erste Ziele erzeugen und platzieren
    }


    /**
     * Erzeugt ein dynamisches Ziel TODO
     *
     *
     * Ansonsten befindet sich das dynamische Ziel logisch "über" der Ebene der anderen Ziele.
     * Nach erfolgreichem Anlegen wird der Move direkt initiiert.
     * @return dynamisches Ziel, kann null sein, falls es von einer gewählten Source nicht erzeugt werden konnte
     */
    @Nullable
    public void createAndMoveDynamicTarget() {

    }


    /**
     * Erzeugt ein neues Ziel und sorgt dafür, dass dieses sich nicht auf der Position des Spielers
     * oder eines vorhandenen Ziels befindet
     * @return neues Ziel
     */
    private void createNewTarget() {
        //TODO: spawn enemies
    }


    /**
     * Prüft ob zwei Kacheln auf den gleichen Koordinaten liegen
     * @param a erste Kachel
     * @param b zweite Kachel
     * @return true wenn Position gleich, andernfalls false
     */
    private boolean samePosition(TileGraphics a, TileGraphics b) {
        //TODO
        return false;
    }

    /**
     * Besorgt Inputstream einer Grafikdatei eines bestimmten Levels aus den Assets
     * @param levelName     Levelname
     * @param graphicsName  Grafikname
     * @return Inputstream
     */
    private InputStream getGraphicsStream(String levelName, String graphicsName) {
       //TODO
        return null;
    }

}
