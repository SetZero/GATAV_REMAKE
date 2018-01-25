package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

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
    private Bitmap healthHeartImage;
    private Bitmap scoreCoinImage;
    private Bitmap timeWatchImage;
    private double popupImageLength;
    private int imageOffset = 40;
    private final int hudTextHeight = 30;
    private Vector2 imagePosition = new Vector2(20, 20);
    private boolean showPopupImage = false;

    private Paint paint;
    private Paint healthPaint;
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint textStrokePaint;
    private Rect lp = new Rect();
    private Timer timer;
    private AssetManager assetManager;

    private boolean drawScoreboard = false;
    private Paint scoreBoardForegroundPaint;
    private Paint scoreBoardBackgroundPaint;


    public HUD(GameCamera camera, Timer timer, AssetManager assetManager) {
        this.timer = timer;
        this.assetManager = assetManager;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);

        healthPaint = new Paint();
        healthPaint.setColor(Color.rgb(211, 31, 19));
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(137, 137, 137));
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30 * ScaleHelper.getRatioX());

        textStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textStrokePaint.setStyle(Paint.Style.STROKE);
        textStrokePaint.setStrokeWidth(1.5f * ScaleHelper.getRatioX());
        textStrokePaint.setColor(Color.BLACK);
        textStrokePaint.setTextSize(30 * ScaleHelper.getRatioX());

        scoreBoardForegroundPaint = new Paint();
        scoreBoardForegroundPaint.setColor(Color.rgb(0, 0, 0));
        scoreBoardForegroundPaint.setTextSize(30 * ScaleHelper.getRatioX());
        scoreBoardForegroundPaint.setTextAlign(Paint.Align.CENTER);
        scoreBoardForegroundPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));


        scoreBoardBackgroundPaint = new Paint();
        scoreBoardBackgroundPaint.setColor(Color.rgb(237, 226, 184));

        try {
            InputStream histr = assetManager.open("hudImages/heart.png");
            healthHeartImage = BitmapFactory.decodeStream(histr);
            healthHeartImage = Bitmap.createScaledBitmap(healthHeartImage, (int)((healthHeartImage.getWidth() / 4f) * ScaleHelper.getRatioX()), (int)((healthHeartImage.getHeight() / 4f) * ScaleHelper.getRatioY()), false);
            histr.close();
            //no pun intended...
            InputStream sistr = assetManager.open("hudImages/coin.png");
            scoreCoinImage = BitmapFactory.decodeStream(sistr);
            scoreCoinImage = Bitmap.createScaledBitmap(scoreCoinImage, (int)((scoreCoinImage.getWidth() / 4f) * ScaleHelper.getRatioX()), (int)((scoreCoinImage.getHeight() / 4f) * ScaleHelper.getRatioY()), false);
            sistr.close();

            InputStream wistr = assetManager.open("hudImages/watch.png");
            timeWatchImage = BitmapFactory.decodeStream(wistr);
            timeWatchImage = Bitmap.createScaledBitmap(timeWatchImage, (int)((timeWatchImage.getWidth() / 4f) * ScaleHelper.getRatioX()), (int)((timeWatchImage.getHeight() / 4f) * ScaleHelper.getRatioY()), false);
            wistr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            drawScoreboard(canvas);
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
        //String lpText = "LP: " + (int) GameContent.player.getLifePoints();
        //paint.getTextBounds(lpText, 0, lpText.length(), lp);
        //canvas.drawText(lpText, 10, 60, paint);
        Rect healthRect = new Rect();
        healthRect.top = (int) (8 * ScaleHelper.getRatioY());
        healthRect.left = (int) (2 * ScaleHelper.getRatioX() + healthHeartImage.getWidth() / 2);
        healthRect.bottom = (int) (28 * ScaleHelper.getRatioY());
        healthRect.right = (int) (152 * ScaleHelper.getRatioX() + healthHeartImage.getWidth() / 2);

        canvas.drawRect(healthRect, backgroundPaint);
        healthRect.right  *=  (GameContent.player.getLifePoints() / GameContent.player.getMaxLifePoints());
        canvas.drawRect(healthRect, healthPaint);
        canvas.drawBitmap(healthHeartImage, (int)(2 * ScaleHelper.getRatioX()), (int)(2 * ScaleHelper.getRatioY()), null);
    }

    private void drawScore(Canvas canvas) {
        int positionRight = (int) (155 * ScaleHelper.getRatioX() + healthHeartImage.getWidth() / 2);
        String scoreText = String.valueOf(GameContent.player.getScore());
        canvas.drawText(scoreText, positionRight + 2 + scoreCoinImage.getWidth(), (int)(hudTextHeight * ScaleHelper.getRatioY()), textPaint);
        canvas.drawText(scoreText, positionRight + 2 + scoreCoinImage.getWidth(), (int)(hudTextHeight * ScaleHelper.getRatioY()), textStrokePaint);
        canvas.drawBitmap(scoreCoinImage, positionRight, (int)(2 * ScaleHelper.getRatioY()), null);
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
        String timeText = String.format(Locale.GERMAN, "%03d", remainingTime);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textStrokePaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timeText, canvas.getWidth() - timeText.length(), (int)(hudTextHeight * ScaleHelper.getRatioY()), textPaint);
        canvas.drawText(timeText, canvas.getWidth() - timeText.length(), (int)(hudTextHeight * ScaleHelper.getRatioY()), textStrokePaint);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textStrokePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawBitmap(timeWatchImage, canvas.getWidth() - timeText.length() * paint.getTextSize() - timeWatchImage.getWidth(), (int)(2 * ScaleHelper.getRatioY()), null);

    }

    public void drawScoreboard(Canvas canvas) {

        if(drawScoreboard) {
            Rect scoreBackground = new Rect();
            scoreBackground.top = (int) (20 * ScaleHelper.getRatioY());
            scoreBackground.left = (int) (20 * ScaleHelper.getRatioX());
            scoreBackground.bottom = (int) ((ScaleHelper.getCameraViewHeight() - 20) * ScaleHelper.getRatioY());
            scoreBackground.right = (int) ((ScaleHelper.getCameraViewWidth() - 20) * ScaleHelper.getRatioX());
            canvas.drawRect(scoreBackground, scoreBoardBackgroundPaint);

            String scoreText = String.format(Locale.GERMAN, "Score:       %06d", GameContent.player.getScore());
            canvas.drawText(scoreText, (ScaleHelper.getCameraViewWidth() / 2) * ScaleHelper.getRatioX(), (ScaleHelper.getCameraViewHeight()/2 - 45) * ScaleHelper.getRatioY(), scoreBoardForegroundPaint);
            String deathText = String.format(Locale.GERMAN, "Deaths:      %06d", GameContent.player.getPlayerDeaths());
            canvas.drawText(deathText, (ScaleHelper.getCameraViewWidth() / 2) * ScaleHelper.getRatioX(), (ScaleHelper.getCameraViewHeight()/2) * ScaleHelper.getRatioY(), scoreBoardForegroundPaint);
            String timeUsedText = String.format(Locale.GERMAN, "Time:        %06d", (int)(timer.getTotalLevelTime() - timer.getSnapshotTime()));
            canvas.drawText(timeUsedText, (ScaleHelper.getCameraViewWidth() / 2) * ScaleHelper.getRatioX(), (ScaleHelper.getCameraViewHeight()/2 + 45) * ScaleHelper.getRatioY(), scoreBoardForegroundPaint);
        }
    }

    public void setDrawScoreboard(boolean drawIt) {
        this.drawScoreboard = drawIt;
    }

    public void onPlayerDeath() {
        popupImageLength = 0;
    }
}
