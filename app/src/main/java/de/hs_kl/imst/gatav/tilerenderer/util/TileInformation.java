package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * Created by Sebastian on 2017-12-05.
 */

public class TileInformation {
    private int xPos;
    private int yPos;

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

    private int tilesetPiece;
}
