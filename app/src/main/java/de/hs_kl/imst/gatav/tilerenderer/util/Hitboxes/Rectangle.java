package de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes;

import android.graphics.Rect;

/**
 * Created by Sebastian on 2017-12-06.
 */

public class Rectangle extends Collidable {
    private Rect rect;
    private int width;
    private int height;
    private String type;

    public Rectangle(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        super.setX(x);
        super.setY(y);
        rect = new Rect(x, y, x + width, y + height);
    }

    public Rectangle(int x, int y, int width, int height, String type) {
        this(x, y, width, height);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * set position using left bottom corner position
     *
     * @param x axis
     * @param y axis
     */
    public void setPos(int x, int y) {
        super.setX(x);
        super.setY(y);
        rect.left = x;
        rect.right = x + width;
        rect.top = y;
        rect.bottom = y + height;

    }

    @Override
    public void setX(int x) {
        super.setX(x);
        rect.left = super.getX();
        rect.right = rect.left + width;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        rect.top = super.getY();
        rect.bottom = rect.top + height;
    }

    public Rect getRect() {
        return new Rect(rect);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        rect.right = getX() + width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        rect.bottom = getY() + height;
    }

    @Override
    public boolean isCollidingWith(Collidable other) {
        if (other instanceof Rectangle) {
            if (rect.intersect(((Rectangle) other).rect)) {
                return true;
            }
        }
        return false;
    }
}
