package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Rect;

/**
 * Used by Tileloader to store all Tiles
 * Created by Sebastian on 2017-12-05.
 *
 * @see de.hs_kl.imst.gatav.tilerenderer.util.TileLoader
 */

public class TileInformation {
    private int tilesetPiece;
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private Rect tileRect;

    //getter and setter
    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getTilesetPiece() {
        return tilesetPiece;
    }

    public void setTilesetPiece(int tilesetPiece) {
        this.tilesetPiece = tilesetPiece;
    }

    /**
     * generates a rect from all given parameters
     */
    public void generateRect() {
        int left = getxPos() * width;
        int top = getyPos() * height;
        int right = left + width;
        int bottom = top + height;
        tileRect = new Rect(left, top, right, bottom);
    }

    public Rect getTileRect() {
        return tileRect;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
