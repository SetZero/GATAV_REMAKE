package de.hs_kl.imst.gatav.tilerenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.FPSHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;


/**
 * {@link SurfaceView} welches sich um die Darstellung des Spiels und Interaktion mit diesem kümmert.
 * Erzeugt eine Gameloop ({@link GameView#gameThread}), welcher die Aktualisierung von Spielzustand
 * und -darstellung regelt.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable, GestureDetector.OnGestureListener {

    public static boolean isTouched = false;
    private final GameEventExecutioner executioner;
    public boolean gameOver = false;
    private SurfaceHolder surfaceHolder;
    private Thread gameThread;
    private boolean runningRenderLoop = false;
    private String levelName;
    private AudioPlayer audioPlayer;


    private GestureDetectorCompat gestureDetector;

    private GameContent gameContent;
    /**
     * Um den GestureDetector verwenden zu können, müssen die Touch-Events an diesen weitergeleitet werden
     * Hier wäre evtl. eine geeignete Stelle, um Eingaben vorrübergehend
     * (bspw. während Animationen) zu deaktivieren, indem eben dieses Weiterleiten deaktiviert wird
     *
     * @param event Aktuelles {@link MotionEvent}
     * @return true wenn das Event verarbeitet wurde, andernfalls false
     */
    private boolean leftmove = false;
    private boolean rightmove = false;

    private Context context;

    /**
     * Konstruktor, initialisiert surfaceHolder und setzt damit den Lifecycle des SurfaceViews in Gang
     *
     * @param context Kontext
     */
    public GameView(Context context, String level, GameEventExecutioner executioner) {
        super(context);
        levelName = level;
        this.context = context;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gestureDetector = new GestureDetectorCompat(context, this);
        gestureDetector.setIsLongpressEnabled(true);
        this.executioner = executioner;
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

    }

    /**
     * Aktualisiert die grafische Darstellung; wird von Gameloop aufgerufen
     *
     * @param canvas Zeichenfläche
     */
    void updateGraphics(Canvas canvas) {
        // Layer 0 (clear background)
        canvas.drawColor(Color.parseColor("#9bd3ff"));


        // Layer 1 (Game content)
        if (gameContent == null) return;
        canvas.save();
        gameContent.draw(canvas);
        canvas.restore();
        GameContent.getHud().draw(canvas);
        // Layer 2 (Collected Targets, Score and Elapsed Time)
        if (BuildConfig.DEBUG) {
            FPSHelper.draw(canvas);
        }
    }

    /**
     * Aktualisiert den Spielzustand; wird von Gameloop aufgerufen
     *
     * @param fracsec Teil einer Sekunde, der seit dem letzten Update vergangen ist
     */
    void updateContent(float fracsec) {
        if (gameContent != null) {
            gameContent.update(fracsec);
        }

    }

    /**
     * Wird aufgerufen, wenn die Zeichenfläche erzeugt wird
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Gameloop anwerfen
        gameThread = new Thread(this);
        gameThread.start();

    }

    /**
     * Wird aufgerufen, wenn sich die Größe der Zeichenfläche ändert
     * Das initiale Festlegen der Größe bewirkt ebenfalls den Aufruf dieser Funktion
     *
     * @param holder Surface Holder
     * @param format Pixelformat
     * @param width  Breite in Pixeln
     * @param height Höhe in Pixeln
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int border = 0;                                                     // darf's ein wenig Rand sein?
        float gameWidth = width - border;
        float gameHeight = (int) (gameWidth / ((float) width / height));

        audioPlayer = new AudioPlayer(context);
        new Thread(audioPlayer).start();
        audioPlayer.addSound(Sounds.COIN, new Vector2(40, 40));
        //audioPlayer.addSound(Sounds.BASS, new Vector2(4000, 80));

        ScaleHelper.calculateRatio((int) gameWidth, (int) gameHeight);
        gameContent = new GameContent(getContext(), levelName, executioner, audioPlayer);

        // Reset der Zustände bei "onResume"
        gameOver = false;

    }

    /**
     * Wird am Ende des Lifecycles der Zeichenfläche aufgerufen
     * Ein guter Ort um ggf. Ressourcen freizugeben, Verbindungen
     * zu schließen und die Gameloop und den Time Thread zu beenden
     *
     * @param holder SurfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Gameloop and Time Thread beenden
        runningRenderLoop = false;
        gameOver = false;
        gameContent.cleanup();
        audioPlayer.cleanup();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gameloop, ruft {@link #updateContent(float)} und {@link #updateGraphics(Canvas)} auf
     * und ermittelt die seit dem letzten Schleifendurchlauf vergangene Zeit (wird zum zeitlich
     * korrekten Aktualisieren des Spielzustandes benötigt)
     */
    @Override
    public void run() {
        runningRenderLoop = true;

        long lastTime = System.currentTimeMillis();

        while (runningRenderLoop) {
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - lastTime;
            float fracsec = (float) delta / 1000f;
            lastTime = currentTime;

            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) continue;

            if (!gameOver)
                updateContent(fracsec); // kompletten Spielzustand aktualisieren

            updateGraphics(canvas); // Neu zeichnen

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameContent == null) return false;
        if (gameContent.player == null) return false;

        int displayW = getWidth();
        int displayH = getHeight();

        if (event.getAction() == MotionEvent.ACTION_UP && event.getPointerCount() < 2) {
            if (leftmove) {
                leftmove = false;
                gameContent.player.stopMove(Direction.LEFT);
            }
            if (rightmove) {
                rightmove = false;
                gameContent.player.stopMove(Direction.RIGHT);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (event.getX() < displayW * 0.3) {
                gameContent.movePlayer(Direction.LEFT);
                leftmove = true;
            }
            if (event.getX() > displayW * 0.70) {
                gameContent.movePlayer(Direction.RIGHT);
                rightmove = true;
            }
        }
        if (event.getPointerCount() > 1) {
            if (event.getAction() == 262)
                GameContent.player.move(Direction.UP);
        }
        if (gestureDetector.onTouchEvent(event))
            return true;
        else
            return super.onTouchEvent(event);
    }

    /**
     * Die Fling-Geste wird genutzt, um die Spielfigur durch den Level zu bewegen.
     * Der eigentliche Move wird dem Gameloop synchron signalisiert und von diesem ausgeführt.
     *
     * @param e1        {@link MotionEvent} welches die Geste gestartet hat (Ursprung)
     * @param e2        {@link MotionEvent} am Ende der Geste (aktuelle Position)
     * @param velocityX Geschwindigkeit der Geste auf der X-Achse (Pixel / Sekunde)
     * @param velocityY Geschwindigkeit der Geste auf der Y-Achse (Pixel / Sekunde)
     * @return true wenn das Event verarbeitet wurde, andernfalls false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float deg = (float) Math.toDegrees(
                Math.acos(velocityX / Math.sqrt(velocityX * velocityX + velocityY * velocityY))
        );
        if (velocityY > 0)
            deg = 180f + (180f - deg);
        if (deg >= 45 && deg <= 135) {
            if (velocityY < -6000f) ;
            gameContent.movePlayer(Direction.UP);
        }

        return true;
    }

    /**
     * Wird hier nicht true zurück gegeben, erfolgen keine onFling events
     *
     * @param e {@link MotionEvent} aktuelles Event
     * @return true.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        isTouched = true;
        return true;
    }

    // Nicht genutzte Gesten
    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

}
