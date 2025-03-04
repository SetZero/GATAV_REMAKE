package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * simple class for Vectors
 * Created by keven on 06.12.2017.
 */

public class Vector2 {
    public float x = 0.0f, y = 0.0f;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public static Vector2 add(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public void add(Vector2 v1) {
        this.x += v1.getX();
        this.y += v1.getY();
    }

    public static Vector2 minus(Vector2 v1, Vector2 v2) {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }

    public static Vector2 skalarMul(Vector2 v1, float x) {
        return new Vector2(v1.x * x, v1.y * x);
    }

    public static float distance(Vector2 v1, Vector2 v2) {
        Vector2 verbindungsvektor = Vector2.minus(v1, v2);
        return verbindungsvektor.vectorLength();
    }

    public static Vector2 nextPoint(Vector2 start, Vector2 direction, float velocity) {
        float unitVector = direction.unitVector();
        Vector2 point = skalarMul(direction, velocity * unitVector);
        return add(start, point);
    }

    public static float angle(Vector2 v1, Vector2 v2) {
        float x = (v1.x * v2.x + v1.y * v2.y) / (v1.vectorLength() * v2.vectorLength());
        x = (float) Math.cosh(x);
        return x;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float vectorLength() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public boolean equals(Vector2 v2) {
        float diffX = getX() - v2.getX();
        float diffY = getY() - v2.getY();
        return Math.abs(diffX - 1.0) <= 0.0001 && Math.abs(diffY - 1.0) <= 0.0001;
    }

    public float unitVector() {
        return 1 / vectorLength();
    }

}
