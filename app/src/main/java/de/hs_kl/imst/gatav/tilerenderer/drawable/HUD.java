package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Locale;

import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;

/**
 * Created by keven on 27.12.2017.
 */

public class HUD {
    //private GameCamera camera;
    private String msg;
    private Paint paint;
    private Rect lp = new Rect(),popup = new Rect(),score = new Rect();
    private double popupLength;
    private Timer timer;

    public HUD(GameCamera camera, Timer timer){
        //this.camera = camera;
        this.timer = timer;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
    }

    public void draw(Canvas canvas) {
        if(GameContent.player != null) {
            drawLP(canvas);
            if(msg!=null)
                drawPopup(canvas);
            drawScore(canvas);
            drawTimer(canvas);
        }
    }

    public void update(float delta){
        //Log.d("time, length","time "+popupTimer+" length "+popupLength);
        if(timer.getElapsedTime() > popupLength){
            popupLength = 0;
            msg = null;
        }
    }

    public void drawPopupMessage(String msg, float popupLength){
        this.msg = msg;
        this.popupLength = timer.getElapsedTime() + popupLength;
    }

    private void drawLP(Canvas canvas){
            String lpText = "LP: " + (int)GameContent.player.getLifePoints();
            paint.getTextBounds(lpText, 0, lpText.length(), lp);
            canvas.drawText(lpText, 10, 60, paint);
    }

    private void drawScore(Canvas canvas){
            String scoreText = "Score: " + GameContent.player.getScore();
            //paint.getTextBounds(lpText, 0, 0, lp);
            canvas.drawText(scoreText, lp.width()+60, 60, paint);
    }

    private void drawPopup(Canvas canvas){
            //paint.getTextBounds(msg, 0, 0, lp);
            paint.setTextSize(80);
            canvas.drawText(msg, canvas.getWidth()/2, canvas.getHeight()/2, paint);
            paint.setTextSize(50);
    }

    private void drawTimer(Canvas canvas) {

        String timeText = "Time: " + String.format(Locale.GERMAN, "%03d", (long)(timer.getTotalLevelTime() - timer.getElapsedTime()));
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timeText, canvas.getWidth() - timeText.length(), 60, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }
}
