package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public class Player extends MovableGraphics {
    private Direction previous;
    private byte doublejump = 0;
    public Player(float x, float y) {
        super(x, y);
            try {
                InputStream is = GameContent.context.getAssets().open("dynamics/player/Player.png");
                loadGraphic(is,17,35,5);
                hitbox = new Rectangle((int)x,(int)y,width,height);
                isActive = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void move(Direction direction){
        previous = currentDirection;
        switch (direction){
            case UP: {
                if (velocity.getY() != 0 && previous != Direction.UP) doublejump ++;
                if(doublejump < 2){
                    velocity.setY(0f);
                    impact(new Vector2(0f,-170f));
                    currentDirection = Direction.UP;
                    doublejump++;
                }
                break;
            }
            case LEFT:{
                if(velocity.getY() == 0 && velocity.getX() > -200f){
                    impact(new Vector2(-100f,0f));
                    currentDirection = Direction.LEFT;
                }
                break;
            }
            case RIGHT:{
                if(velocity.getY() == 0 && velocity.getX() < 200f) {
                    impact(new Vector2(100f, 0f));
                    currentDirection = Direction.RIGHT;
                }
                break;
            }

        }
    }

    @Override
    public void update(float delta){
        super.update(delta);
        if(velocity.getY() == 0f && velocity.getX() == 0){
            previous = currentDirection;
            currentDirection = Direction.IDLE;
        }
        if(doublejump != 0 && velocity.getY() == 0) doublejump = 0;
        //DOWN == FALLING
        if(velocity.getY() != 0 && currentDirection != Direction.UP) currentDirection = Direction.DOWN;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
