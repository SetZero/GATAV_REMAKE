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

public class Player extends MovableGraphics implements Destroyable{
    private Direction previous;
    private byte doublejump = 0;
    private float lifePoints =150;
    private Direction stopDirection = Direction.IDLE;

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
                    impact(new Vector2(0f,-470f));
                    currentDirection = Direction.UP;
                    doublejump++;
                }
                break;
            }
            case LEFT:{
                if(velocity.getY() == 0 && velocity.getX() > -200f){
                    impact(new Vector2(-200f,0f));
                    currentDirection = Direction.LEFT;
                }
                break;
            }
            case RIGHT:{
                if(velocity.getY() == 0 && velocity.getX() < 200f) {
                    impact(new Vector2(200f, 0f));
                    currentDirection = Direction.RIGHT;
                }
                break;
            }

        }
    }

    /**
     * interrupts LEFT or RIGHT movement the correct way (is the player on Ground?)
     * this interrupt will occur on the next possible update
     * @param direction
     */
    public void stopMove(Direction direction){
        stopDirection = direction;
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
        if(isOnGround && stopDirection != Direction.IDLE){
            if(stopDirection == Direction.LEFT && velocity.getX() < 0f){
                impact(new Vector2(200f, 0f));
                currentDirection = Direction.IDLE;
            }
            else if(stopDirection == Direction.RIGHT && velocity.getX() > 0f){
                impact(new Vector2(-200f, 0f));
                currentDirection = Direction.IDLE;
            }
            stopDirection = Direction.IDLE;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void processHit(float hit) {
        lifePoints -= hit;
    }

    @Override
    public boolean isDestroyed() {
        if(lifePoints > 0f) return false;
        return true;
    }
}
