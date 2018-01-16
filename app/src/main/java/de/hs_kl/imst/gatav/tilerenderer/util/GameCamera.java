package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Canvas;
import android.graphics.Rect;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;

/**
 * The Game camera which will manage everything the current view
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

    /**
     * @return the center of the camera X position
     */
    public int getCameraXCenter() {
        return cameraXCenter;
    }

    /**
     * @param cameraXCenter the center of the camera X position
     */
    public void setCameraXCenter(int cameraXCenter) {
        assert (attachedTo == null) : "Camera can't be moved as long as it's attached to a Body!";
        this.cameraXCenter = cameraXCenter;
    }

    /**
     * @return the center of the camera Y position
     */
    public int getCameraYCenter() {
        return cameraYCenter;
    }

    /**
     * @param cameraYCenter the center of the camera Y position
     */
    public void setCameraYCenter(int cameraYCenter) {
        assert (attachedTo == null) : "Camera can't be moved as long as it's attached to a Body!";
        this.cameraYCenter = cameraYCenter;
    }

    /**
     * @param x the center of the camera X position
     * @param y the center of the camera Y position
     */
    public void setCameraPosition(int x, int y) {
        this.cameraXCenter = x;
        this.cameraYCenter = y;
    }

    /**
     * @return if the camera is following an object
     */
    public boolean isAttachedToObject() {
        return attachedTo != null;
    }

    /**
     * @return the total height of the level
     */
    public int getLevelHeight() {
        return levelHeight;
    }

    /**
     * @param levelHeight the total height of the level (only called by TileRenderer)
     */
    public void setLevelHeight(int levelHeight) {
        this.levelHeight = levelHeight;
    }

    /**
     *
     * @return the width of the level in px
     */
    public int getLevelWidth() {
        return levelWidth;
    }

    /**
     * @param levelWidth the width of the level in px
     */
    public void setLevelWidth(int levelWidth) {
        this.levelWidth = levelWidth;
    }

    /**
     * calculate the transform offset of the camera with the player in the center, also check that
     * the camera is not out of bounds
     * @param canvas canvas to draw (transform) on
     */
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

    /**
     * @return the rect the camera is currently in
     */
    public Rect getCameraViewRect() {
        int minX = cameraXCenter - ((canvasWidth / 2));
        int minY = cameraYCenter - ((canvasHeight / 2));
        int maxX = cameraXCenter + (canvasWidth / 2);
        int maxY = cameraYCenter + (canvasHeight / 2);
        return new Rect(minX, minY, maxX, maxY);
    }

    /**
     * Checks if a rect is in the view of the camera
     * @param a Rect to check
     * @return is in view
     */
    public boolean isRectInView(Rect a) {
        return getCameraViewRect().intersect(a);
    }

    /**
     * Attach the camera to the object (follow it)
     * @param body
     */
    public void attach(MovableGraphics body) {
        attachedTo = body;
    }

    /**
     * unfollow object
     */
    public void detach() {
        attachedTo = null;
    }
}