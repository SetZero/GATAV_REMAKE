package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Sebastian on 2018-01-06.
 */

public class LoadingScreen {
    private double loadingScreenColor;
    private boolean loadingScreenFadeDirection = true;
    private String loadingScreenText;
    private TileLoader tileLoader;

    public LoadingScreen(TileLoader tileLoader) {
        int rnd = ThreadLocalRandom.current().nextInt(LoadingScreenTexts.text.length);
        loadingScreenText = LoadingScreenTexts.text[rnd];
        this.tileLoader = tileLoader;
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
}
