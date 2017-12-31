package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;

/**
 * Created by keven on 27.12.2017.
 */

public class HUD {
    //private GameCamera camera;
    private String msg;
    private Paint paint;
    private Rect lp = new Rect(),popup = new Rect(),score = new Rect();
    private float popupTimer,popupLength;

    public HUD(GameCamera camera){
        //this.camera = camera;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
    }

    public void draw(Canvas canvas) {
        if(GameContent.player != null) {
            drawLP(canvas);
            if(msg!=null && popupTimer <= popupLength)
                drawPopup(canvas);
            drawScore(canvas);
        }
    }

    public void update(float delta){
        //Log.d("time, length","time "+popupTimer+" length "+popupLength);
        if(msg!= null)
            popupTimer += delta;
        if(popupTimer > popupLength){
            popupTimer = 0;
            popupLength = 0;
            msg = null;
        }
    }

    public void drawPopupMessage(String msg,float popupLength){
        this.msg = msg;
        this.popupLength = popupLength;
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
}
