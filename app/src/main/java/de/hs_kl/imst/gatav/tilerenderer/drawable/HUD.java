package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import de.hs_kl.imst.gatav.tilerenderer.util.GameCamera;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Klasse um dem Spieler ein HUD zu bieten
 * Created by keven on 27.12.2017.
 */

public class HUD {
    private String msg;
    private double popupLength;

    private Bitmap popupImage;
    private double popupImageLength;
    private int imageOffset = 40;
    private Vector2 imagePosition = new Vector2(20, 20);
    private boolean showPopupImage = false;

    private Paint paint;
    private Rect lp = new Rect();
    private Timer timer;
    private AssetManager assetManager;


    public HUD(GameCamera camera, Timer timer, AssetManager assetManager) {
        this.timer = timer;
        this.assetManager = assetManager;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
    }

    public void draw(Canvas canvas) {
        if (GameContent.player != null) {
            drawLP(canvas);
            if (msg != null)
                drawPopup(canvas);
            if (popupImage != null)
                drawPopupImage(canvas);
            drawScore(canvas);
            drawTimer(canvas);
        }
    }

    public void update(float delta) {
        if (timer.getElapsedTime() > popupLength) {
            popupLength = 0;
            msg = null;
        }

        if (showPopupImage && timer.getElapsedTime() > popupImageLength) {
            popupImageLength = 0;
            popupImage = null;
            showPopupImage = false;
        }

        if (timer.getElapsedTime() > popupImageLength - 1 && showPopupImage && popupImage != null) {
            imagePosition.setY(imagePosition.getY() + (1 / delta));
        }
    }

    public void drawPopupMessage(String msg, float popupLength) {
        this.msg = msg;
        this.popupLength = timer.getElapsedTime() + popupLength;
    }

    private void drawLP(Canvas canvas) {
        String lpText = "LP: " + (int) GameContent.player.getLifePoints();
        paint.getTextBounds(lpText, 0, lpText.length(), lp);
        canvas.drawText(lpText, 10, 60, paint);
    }

    private void drawScore(Canvas canvas) {
        String scoreText = "Score: " + GameContent.player.getScore();
        canvas.drawText(scoreText, lp.width() + 60, 60, paint);
    }

    public void drawPopupImage(String filename, float popupLength) {
        try {
            InputStream istr = assetManager.open(filename);
            popupImage = BitmapFactory.decodeStream(istr);
            Matrix matrix = new Matrix();
            matrix.postRotate(20);
            popupImage = Bitmap.createScaledBitmap(popupImage,
                    (int) (popupImage.getWidth() * ScaleHelper.getRatioX()) - (int) (imageOffset * ScaleHelper.getRatioX()),
                    (int) (popupImage.getHeight() * ScaleHelper.getRatioY()) - (int) (imageOffset * ScaleHelper.getRatioY()),
                    true);
            popupImage = Bitmap.createBitmap(popupImage, 0, 0, popupImage.getWidth(), popupImage.getHeight(), matrix, true);
            this.popupImageLength = timer.getElapsedTime() + popupLength;
            imagePosition.setX(imageOffset / 2);
            imagePosition.setY(imageOffset / 2);
            showPopupImage = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void drawPopupImage(Canvas canvas) {
        canvas.drawBitmap(popupImage, imagePosition.getX() + (canvas.getWidth() / 2 - popupImage.getWidth() / 2), imagePosition.getY(), null);
    }

    private void drawPopup(Canvas canvas) {
        paint.setTextSize(80);
        canvas.drawText(msg, (canvas.getWidth() / 2) * ScaleHelper.getRatioX(), (canvas.getHeight() / 2) * ScaleHelper.getRatioY(), paint);
        paint.setTextSize(50);
    }


    private void drawTimer(Canvas canvas) {

        long remainingTime = (long) (timer.getTotalLevelTime() - timer.getElapsedTime());
        remainingTime = (remainingTime >= 0 ? remainingTime : 0);
        String timeText = "Time: " + String.format(Locale.GERMAN, "%03d", remainingTime);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timeText, canvas.getWidth() - timeText.length(), 60, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }
}
