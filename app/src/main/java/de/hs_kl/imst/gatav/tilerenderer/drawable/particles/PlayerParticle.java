package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Adds a Particle which originates from the player
 * Does no damage to the player, but to enemies
 * Created by Sebastian on 2018-01-18.
 */

public class PlayerParticle extends ParticlePrototype {

    private Vector2 direction;
    private Paint color = new Paint();
    private Paint innerColor = new Paint();
    private int speed = 500;
    private int size = 5;
    private int stroke = 2;
    private int damage = 30;

    public PlayerParticle(Vector2 position, int color, float radians) {
        setPosition(position);
        direction = new Vector2((float)Math.cos(radians), (float)Math.sin(radians));
        this.innerColor.setColor(Color.WHITE);
        this.color.setColor(color);
        this.color.setStyle(Paint.Style.STROKE);
        this.color.setStrokeWidth(stroke * ScaleHelper.getRatioX());
        super.setOriginatedFromPlayer(true);
        super.setIgnoringPlayer(true);
    }

    @Override
    public void update(float delta) {
        setPosition(Vector2.add(getPosition(), new Vector2(direction.getX()*delta*speed, direction.getY()*delta*speed)));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(getPosition().getX(), getPosition().getY(), (size - stroke) * ScaleHelper.getRatioX(), innerColor);
        canvas.drawCircle(getPosition().getX(), getPosition().getY(), size * ScaleHelper.getRatioX(), color);
    }

    @Override
    public int getDamage() {
        return this.damage;
    }
}
