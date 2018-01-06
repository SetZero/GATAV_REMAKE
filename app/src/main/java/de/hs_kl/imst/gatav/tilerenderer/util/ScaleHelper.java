package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * Created by Sebastian on 2017-12-12.
 */

public class ScaleHelper {
    /* Scalings */
    private static int cameraViewWidth = 600;
    private static int cameraViewHeight = 0; //automatic

    private static int ratioX = 2;
    private static int ratioY = 2;

    //This is static!
    private ScaleHelper() {

    }

    public static float getRatioX() {
        return ratioX;
    }

    public static float getRatioY() {
        return ratioY;
    }

    public static void calculateRatio(int canvasWidth, int canvasHeight) {
        float aspectRatio = canvasHeight / (float) canvasWidth;
        cameraViewHeight = (int) (cameraViewWidth * aspectRatio);

        ratioX = (int) (canvasHeight / (float) cameraViewHeight);
        ratioY = (int) (canvasWidth / (float) cameraViewWidth);
    }

    public static int getEntitiyScale() {
        return (int) ((5f / 3f) * getRatioY());
    }
}
