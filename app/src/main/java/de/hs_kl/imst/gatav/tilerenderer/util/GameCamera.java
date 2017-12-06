package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Sebastian on 2017-12-05.
 */

public class GameCamera {
    int canvasWidth = 0;
    int canvasHeight = 0;
    private int cameraXCenter;
    private int cameraYCenter;

    public int getCameraXCenter() {
        return cameraXCenter;
    }

    public void setCameraXCenter(int cameraXCenter) {
        this.cameraXCenter = cameraXCenter;
    }

    public int getCameraYCenter() {
        return cameraYCenter;
    }

    public void setCameraYCenter(int cameraYCenter) {
        this.cameraYCenter = cameraYCenter;
    }

    public void setCameraPosition(int x, int y) {
        this.cameraXCenter = x;
        this.cameraYCenter = y;
    }

    public void draw(Canvas canvas) {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        int xPos = cameraXCenter - (canvasWidth/2);
        int yPos = cameraYCenter - (canvasHeight/2);
        if(xPos < 0) xPos = 0;
        if(yPos < 0) yPos = 0;
        canvas.translate(-xPos, -yPos);
    }

    public boolean isRectInView(Rect a) {
        //TODO: Make this less static, bind to Rec width!
        int minX = cameraXCenter - ((canvasWidth/2));
        int minY = cameraYCenter - ((canvasHeight/2));
        int maxX = cameraXCenter + (canvasWidth/2);
        int maxY = cameraYCenter + (canvasHeight/2);
        Rect b = new Rect(minX, minY, maxX, maxY);
        if(b.intersect(a))
            return true;
        return false;
    }
}
