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
    private  int cameraViewWidth = 1080;
    private int cameraViewHeight = 0; // automatic
    private int cameraXCenter;
    private int cameraYCenter;
    private int levelHeight;
    private int levelWidth;

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


    public void setLevelHeight(int levelHeight) {
        this.levelHeight = levelHeight;
    }

    public void setLevelWidth(int levelWidth) {
        this.levelWidth = levelWidth;
    }

    public void draw(Canvas canvas) {
        /* If attached to Graphic adjust values */
        if(attachedTo != null) {
            cameraXCenter = (int) attachedTo.getPosition().getX();
            cameraYCenter = (int) attachedTo.getPosition().getY();
        }

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        /* Camera Scale Ratio */
        float aspectRatio = canvasHeight / (float)canvasWidth;
        cameraViewHeight = (int)(cameraViewWidth * aspectRatio);

        float ratioX = canvasHeight / (float)cameraViewHeight;
        float ratioY = canvasWidth / (float)cameraViewWidth;

        /* Real Camera Position */
        int xPos = cameraXCenter - (cameraViewWidth/2);
        int yPos = cameraYCenter - (cameraViewHeight/2);

        /* Out of Bounds Check */
        if(xPos < 0) {
            cameraXCenter = cameraViewWidth/2;
            xPos = 0;
        } else if(xPos > levelWidth - cameraViewWidth) {
            cameraXCenter = levelWidth -  (cameraViewWidth/2);
            xPos = levelWidth - cameraViewWidth;
        }
        if(yPos < 0){
            cameraYCenter = cameraViewHeight/2;
            yPos = 0;
        } else if(yPos > levelHeight - cameraViewHeight) {
            cameraYCenter = levelHeight -  (cameraViewHeight/2);
            yPos = levelHeight - cameraViewHeight;
        }
        canvas.scale(ratioX, ratioY);
        canvas.translate(-xPos, -yPos);
    }

    public boolean isRectInView(Rect a) {
        //TODO: Make this less static, bind to Rec width!
        int minX = cameraXCenter - ((cameraViewWidth/2));
        int minY = cameraYCenter - ((cameraViewHeight/2));
        int maxX = cameraXCenter + (cameraViewWidth/2);
        int maxY = cameraYCenter + (cameraViewHeight/2);
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
