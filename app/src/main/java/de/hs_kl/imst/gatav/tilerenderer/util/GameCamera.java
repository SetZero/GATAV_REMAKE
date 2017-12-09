package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;

/**
 * Created by Sebastian on 2017-12-05.
 */

public class GameCamera {
    private MovableGraphics attachedTo;
    private int canvasWidth = 0;
    private int canvasHeight = 0;
    private  int cameraViewWidth = 600;
    private int cameraViewHeight = 0; // automatic
    private int cameraXCenter;
    private int cameraYCenter;

    public int getCameraXCenter() {
        return cameraXCenter;
    }

    public void setCameraXCenter(int cameraXCenter) {
        assert(attachedTo == null) : "Camera can't be moved as long as it's attached to a Body!";
        this.cameraXCenter = cameraXCenter;
    }

    public int getCameraYCenter() {
        return cameraYCenter;
    }

    public void setCameraYCenter(int cameraYCenter) {
        assert(attachedTo == null) : "Camera can't be moved as long as it's attached to a Body!";
        this.cameraYCenter = cameraYCenter;
    }

    public void setCameraPosition(int x, int y) {
        this.cameraXCenter = x;
        this.cameraYCenter = y;
    }

    public void draw(Canvas canvas) {
        if(attachedTo != null) {
            cameraXCenter = (int) attachedTo.getPosition().getX();
            cameraYCenter = (int) attachedTo.getPosition().getY();
        }

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        float aspectRatio = canvasHeight / (float)canvasWidth;
        cameraViewHeight = (int)(cameraViewWidth * aspectRatio);

        float ratioX = canvasHeight / (float)cameraViewHeight;
        float ratioY = canvasWidth / (float)cameraViewWidth;

        int xPos = cameraXCenter - (canvasWidth/2);
        int yPos = cameraYCenter - (canvasHeight/2);
        if(xPos < 0) xPos = 0;
        if(yPos < 0) yPos = 0;
        canvas.translate(-xPos, -yPos);

        //canvas.scale(ratioX, ratioY);
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

    public void attach(MovableGraphics body) {
        attachedTo = body;
    }

    public void detach() {
        attachedTo = null;
    }
}
