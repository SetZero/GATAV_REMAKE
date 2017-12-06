package de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes;

import android.graphics.Rect;

/**
 * Created by Sebastian on 2017-12-06.
 */

public class Rectangle extends Collidable {
    private Rect rect;
    private int width;

    private int height;

    public Rectangle(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        rect = new Rect(x, y, x + width, y + height);
    }


    @Override
    public void setX(int x) {
        super.setX(x);
        rect.left = super.getX();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        rect.top = super.getY();
    }

    public Rect getRect() {
        return rect;
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
        if(other instanceof Rectangle) {
           if(rect.intersect( ((Rectangle) other).rect)) {
               return true;
           }
        }
        return false;
    }
}
