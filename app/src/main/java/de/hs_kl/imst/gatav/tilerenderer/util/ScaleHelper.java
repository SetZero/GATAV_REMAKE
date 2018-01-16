package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * Helper class to get the device scaling
 * Created by Sebastian on 2017-12-12.
 */

public class ScaleHelper {
    /* Scalings */
    private static int cameraViewWidth = 600; //600 virtual pixel to render
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

    /**
     * calculates the scale ratio given by cameraViewWidth, so that all devices will have this amount
     * of pixel
     * @param canvasWidth the width of the actual canvas to draw on (will be different on all devices
     * @param canvasHeight the height of the canvas to draw on
     */
    public static void calculateRatio(int canvasWidth, int canvasHeight) {
        float aspectRatio = canvasHeight / (float) canvasWidth;
        cameraViewHeight = (int) (cameraViewWidth * aspectRatio);

        ratioX = (int) (canvasHeight / (float) cameraViewHeight);
        ratioY = (int) (canvasWidth / (float) cameraViewWidth);
    }

    /**
     * Scaling of all Entities, call this on enetities only, they have their own scaling!
     * @return scale factor
     */
    public static int getEntitiyScale() {
        return (int) ((5f / 3f) * getRatioY());
    }
}
