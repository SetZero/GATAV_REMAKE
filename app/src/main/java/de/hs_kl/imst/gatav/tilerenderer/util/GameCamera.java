package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Rect;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;

/**
 * Created by Sebastian on 2017-12-05.
 */

public class GameCamera {
    private MovableGraphics attachedTo;
    private int canvasWidth = 0;
    private int canvasHeight = 0;
    private int cameraXCenter;
    private int cameraYCenter;
    private int levelHeight;
    private int levelWidth;

    public int getCameraXCenter() {
        return cameraXCenter;
    }

    public void setCameraXCenter(int cameraXCenter) {
        assert (attachedTo == null) : "Camera can't be moved as long as it's attached to a Body!";
        this.cameraXCenter = cameraXCenter;
    }

    public int getCameraYCenter() {
        return cameraYCenter;
    }

    public void setCameraYCenter(int cameraYCenter) {
        assert (attachedTo == null) : "Camera can't be moved as long as it's attached to a Body!";
        this.cameraYCenter = cameraYCenter;
    }

    public void setCameraPosition(int x, int y) {
        this.cameraXCenter = x;
        this.cameraYCenter = y;
    }

    public boolean isAttachedToObject() {
        return attachedTo != null;
    }

    public int getLevelHeight() {
        return levelHeight;
    }

    public void setLevelHeight(int levelHeight) {
        this.levelHeight = levelHeight;
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public void setLevelWidth(int levelWidth) {
        this.levelWidth = levelWidth;
    }

    public void draw(Canvas canvas) {
        /* If attached to Graphic adjust values */
        if (attachedTo != null) {
            cameraXCenter = (int) attachedTo.getPosition().getX();
            cameraYCenter = (int) attachedTo.getPosition().getY();
        }

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        /* Real Camera Position */
        int xPos = cameraXCenter - (canvasWidth / 2);
        int yPos = cameraYCenter - (canvasHeight / 2);


        /* Out of Bounds Check */
        if (xPos < 0) {
            // Links
            cameraXCenter = canvasWidth / 2;
            xPos = 0;
        } else if (cameraXCenter > (levelWidth - canvasWidth / 2)) {
            cameraXCenter = (levelWidth - canvasWidth / 2);
            xPos = cameraXCenter - (canvasWidth / 2);
        }
        if (yPos < 0) {
            //oben
            cameraYCenter = canvasHeight / 2;
            yPos = 0;
        } else if (cameraYCenter > (levelHeight - canvasHeight / 2)) {
            cameraYCenter = (levelHeight - canvasHeight / 2);
            yPos = cameraYCenter - (canvasHeight / 2);
        }
        canvas.translate(-xPos, -yPos);
    }

    public Rect getCameraViewRect() {
        int minX = cameraXCenter - ((canvasWidth / 2));
        int minY = cameraYCenter - ((canvasHeight / 2));
        int maxX = cameraXCenter + (canvasWidth / 2);
        int maxY = cameraYCenter + (canvasHeight / 2);
        return new Rect(minX, minY, maxX, maxY);
    }

    public boolean isRectInView(Rect a) {
        return getCameraViewRect().intersect(a);
    }

    public void attach(MovableGraphics body) {
        attachedTo = body;
    }

    public void detach() {
        attachedTo = null;
    }
}