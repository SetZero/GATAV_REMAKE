package de.hs_kl.imst.gatav.tilerenderer.drawable;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;

import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by keven on 19.12.2017.
 */

public final class Skeletton extends Enemys implements CollisionReactive, Destroyable {
    private Animations run;
    private BitmapDrawable idle;
    private float animTime;
    public Skeletton(int x, int y){
        super(x,y, 30, 20);
        try {
            InputStream is = GameContent.context.getAssets().open("dynamics/player/Player.png");
            loadGraphic(is, 17, 35, 5);
            run = new Animations(1f / 4f);
            is = GameContent.context.getAssets().open("dynamics/player/Player.png");
            run.addAnimation(super.loadTextures(is, 17, 35, 1, 4, 5));
            idle = run.getDrawable(0f);
            hitbox = new Rectangle((int) x, (int) y, width - 35, height);
            isActive = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stateHandle() {
        if(velocity.getY() == 0f && velocity.getX() == 0){
            previous = currentDirection;
            currentDirection = Direction.IDLE;
        }
        if(isOnGround && velocity.x > 0f  && velocity.y == 0){
            currentDirection = Direction.RIGHT;
        }
        else if(isOnGround && velocity.x < 0f && velocity.y == 0){
            currentDirection = Direction.LEFT;
        }
        if(velocity.getY() != 0 && currentDirection != Direction.UP) currentDirection = Direction.DOWN;
    }

    @Override
    public void animationHandle(float delta) {
        if(run.isFinished(animTime)) animTime = 0;
        if(currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT)
            animTime += delta;
        if(currentDirection == Direction.IDLE){
            bmp = idle;
        }
        // running right
        if(currentDirection == Direction.RIGHT && velocity.x > 0){
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
    public void aIHandle() {
        if(GameContent.player.getPosition().x < Position.x){
            move(Direction.LEFT);
            currentDirection = Direction.LEFT;
        }
        else if(GameContent.player.getPosition().x > Position.x){
            move(Direction.RIGHT);
            currentDirection = Direction.RIGHT;
        }
    }
    private void move(Direction direction){
        previous = currentDirection;
        switch (direction){
            case LEFT:{
                if(!isLeftColliding) {
                    if (velocity.getX() > 0 && velocity.x <= 120f && !isOnGround) {
                        velocity.x = 0f;
                        impact(new Vector2(-120f, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.LEFT;
                    }
                    if (velocity.getX() > -120f) {
                        impact(new Vector2(-120f, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.LEFT;
                    }
                }
                break;
            }
            case RIGHT:{
                if(!isRightColliding) {
                    if (velocity.getX() < 0 && velocity.x >= -120f && !isOnGround) {
                        velocity.x = 0f;
                        impact(new Vector2(120f, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.RIGHT;
                    }
                    if (velocity.getX() < 120f) {
                        impact(new Vector2(120f, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.RIGHT;
                    }
                }
                break;
            }

        }
    }
}
