package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.LinkedList;
import java.util.Locale;

/**
 * Helper to show the FPS
 * Created by Sebastian on 2017-12-05.
 */

public class FPSHelper {
    private static final int MAX_SIZE = 100;
    private static final double NANOS = 1000000000.0;
    private static LinkedList<Long> times = new LinkedList<Long>() {{
        add(System.nanoTime());
    }};

    /**
     * Draw FPS text
     * @param canvas canvas to draw on
     */
    public static void draw(Canvas canvas) {
        String fpsText = String.format(Locale.GERMAN, "FPS: %.2f", fps());

        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(fpsText, 300*ScaleHelper.getRatioX(), 25, paint);
    }

    /**
     * Calculates and returns frames per second
     */
    public static double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - times.getFirst()) / NANOS;
        times.addLast(lastTime);
        int size = times.size();
        if (size > MAX_SIZE) {
            times.removeFirst();
        }
        return difference > 0 ? times.size() / difference : 0.0;
    }
}
