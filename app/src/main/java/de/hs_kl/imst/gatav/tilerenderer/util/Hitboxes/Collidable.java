package de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes;

/**
 * A collideable object (only has one point and an id)
 * Created by Sebastian on 2017-12-06.
 */

public abstract class Collidable {
    private int x;
    private int y;

    private int id;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract boolean isCollidingWith(Collidable other);
}
