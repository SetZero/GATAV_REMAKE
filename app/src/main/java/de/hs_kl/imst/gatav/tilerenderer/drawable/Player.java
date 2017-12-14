package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

public class Player extends MovableGraphics implements Destroyable{
    private Direction previous;
    private byte doublejump = 0;
    private float lifePoints =150;
    private Direction stopDirection = Direction.IDLE;
    private BitmapDrawable idle;
    private Animations run;
    private float animTime = 0f;

    public Player(float x, float y) {
        super(x, y);
            try {
                InputStream is = GameContent.context.getAssets().open("dynamics/player/Player.png");
                loadGraphic(is,17,35,5);
                run = new Animations(1f/4f);
                is = GameContent.context.getAssets().open("dynamics/player/Player.png");
                run.addAnimation(super.loadTextures(is,17,35,1,4,5));
                idle = run.getDrawable(0f);
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
                if (velocity.getY() != 0 && previous == Direction.DOWN) doublejump ++;
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
        stateHandle();
        animationHandle(delta);
    }
    private void stateHandle(){
        if(velocity.getY() == 0f && velocity.getX() == 0){
            previous = currentDirection;
            currentDirection = Direction.IDLE;
        }
        if(doublejump != 0 && velocity.getY() == 0) doublejump = 0;
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
        if(isOnGround && velocity.x > 0f  && velocity.y == 0){
            currentDirection = Direction.RIGHT;
        }
        else if(isOnGround && velocity.x < 0f && velocity.y == 0){
            currentDirection = Direction.LEFT;
        }
        if(velocity.getY() != 0 && currentDirection != Direction.UP) currentDirection = Direction.DOWN;
    }
    private void animationHandle(float delta){
        //noFlip
        if(run.isFinished(animTime)) animTime = 0;
        if(currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT)
            animTime += delta;
        if(currentDirection == Direction.IDLE){
            bmp = idle;
        }
        // running right
        if(currentDirection == Direction.RIGHT && velocity.x > 0){
            Log.d("animtime",""+animTime);
            bmp = run.getDrawable(animTime);
            isFlipped = false;
        }
        else if(currentDirection == Direction.LEFT){
            bmp = run.getDrawable(animTime);
            isFlipped = true;
        }
        if(currentDirection == Direction.UP || currentDirection == Direction.IDLE || currentDirection == Direction.IDLE || currentDirection == Direction.DOWN){
            bmp = idle;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(bmp != null && isActive && !isFlipped) {
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
        else if (isFlipped){
            BitmapDrawable bmp = flip(this.bmp);
            canvas.drawBitmap(bmp.getBitmap(),Position.getX(),Position.getY(),null);
        }
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
