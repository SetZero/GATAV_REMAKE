package de.hs_kl.imst.gatav.tilerenderer.drawable.particles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Emits Water Particles
 * Created by Sebastian on 2018-01-18.
 */

public class WaterParticle extends ParticlePrototype {

    private Vector2 direction;
    private Paint color = new Paint();
    private Paint innerColor = new Paint();
    private int size = 10;
    private int stroke = 2;
    private int damage = 0;
    private int speed = 1000;
    private float initialYPosition;
    private Vector2 velocity = new Vector2(1, 1);

    public WaterParticle(Vector2 position, int color, float radians, int size) {
        setPosition(position);
        initialYPosition = position.getY();
        this.size = size;
        direction = new Vector2((float)Math.cos(radians), (float)Math.sin(radians));
        this.innerColor.setColor(color);
        this.color.setARGB(125, Color.red(color), Color.green(color), Color.blue(color));
        super.setIgnoringPlayer(true);
    }

    @Override
    public void update(float delta) {
        if(initialYPosition < getPosition().getY()) setActive(false);
        if(isActive()) {
            setPosition(Vector2.add(getPosition(), new Vector2(direction.getX() * speed * delta * velocity.getX(), direction.getY() * speed * delta * velocity.getY())));

            if (velocity.getY() > -1) {
                velocity.setY(velocity.getY() - (2f*delta));
            }
        }
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
