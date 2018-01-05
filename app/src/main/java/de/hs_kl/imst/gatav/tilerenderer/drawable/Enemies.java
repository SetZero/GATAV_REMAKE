package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Contact;
import de.hs_kl.imst.gatav.tilerenderer.util.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by keven on 17.12.2017.
 */

public abstract class Enemies extends MovableGraphics implements Destroyable, CollisionReactive  {
    protected float lifePoints, animTime, speed;
    protected float dieTimer = 0.0f;
    protected Animations run;
    protected Animations dieng;
    protected BitmapDrawable idle;
    protected int scorePoints;
    protected int drawOffsetY;
    protected boolean isAlive =true;
    public int hitPoints;

    public int getScorePoints() {
        return scorePoints;
    }

    public void setScorePoints(int scorePoints) {
        this.scorePoints = scorePoints;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public Enemies(float x, float y, float lifePoints, int hitPoints, float speed, int scorePoints){
        super(x,y);
        this.scorePoints = scorePoints;
        this.lifePoints = lifePoints;
        this.hitPoints = hitPoints;
        this.speed = speed;
    }

    @Override
    public void processHit(float hit) {
        this.lifePoints -= hit;
    }

    @Override
    public boolean isDestroyed() {
        if(lifePoints <= 0) return true;
        return false;
    }

    @Override
    public void update(float delta){
        if(isAlive)
        super.update(delta);
        if(this.lifePoints <= 0) isAlive =false;
        if(isActive) {
            if (isAlive) {
                aIHandle();
                stateHandle();
                animationHandle(delta);
            } else {
                if (dieng.isFinished(dieTimer)) {
                    GameContent.world.removeGameObject(this);
                } else {
                    bmp = dieng.getDrawable(dieTimer);
                }
                dieTimer += delta;
            }
        }
    }

    protected void stateHandle(){
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

    protected void animationHandle(float delta){
        if(run.isFinished(animTime)) animTime = 0;
        if(currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT)
            animTime += delta;
        if(currentDirection == Direction.IDLE){
            bmp = idle;
        }
        // running right
        if(currentDirection == Direction.RIGHT ){
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
    protected void aIHandle(){
        if(GameContent.player.isAlive()) {
            if (GameContent.player.getPosition().x-GameContent.player.getHitbox().getWidth()/2 < Position.x) {
                move(Direction.LEFT);
                currentDirection = Direction.LEFT;
            } else if (GameContent.player.getPosition().x -GameContent.player.getHitbox().getWidth()/2 > Position.x) {
                move(Direction.RIGHT);
                currentDirection = Direction.RIGHT;
            }
        }
    }

    protected void move(Direction direction){
        previous = currentDirection;
        switch (direction){
            case LEFT:{
                if(!isLeftColliding) {
                    if (velocity.getX() > 0 && velocity.x <= speed && !isOnGround) {
                        velocity.x = 0f;
                        impact(new Vector2(-speed, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.LEFT;
                    }
                    if (velocity.getX() > -speed) {
                        impact(new Vector2(-speed, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.LEFT;
                    }
                }
                break;
            }
            case RIGHT:{
                if(!isRightColliding) {
                    if (velocity.getX() < 0 && velocity.x >= -speed && !isOnGround) {
                        velocity.x = 0f;
                        impact(new Vector2(speed, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.RIGHT;
                    }
                    if (velocity.getX() < speed) {
                        impact(new Vector2(speed, 0f));
                        if (velocity.y == 0)
                            currentDirection = Direction.RIGHT;
                    }
                }
                break;
            }

        }
    }

    @Override
    public void onCollision(Contact c) {
        if(c.collisionObject instanceof Player && (c.siteHidden == PhysicsController.intersectDirection.LEFT || c.siteHidden == PhysicsController.intersectDirection.RIGHT) && ((Player)c.collisionObject).isAlive()){
            if(c.siteHidden == PhysicsController.intersectDirection.LEFT) {
                ((Player) c.collisionObject).setLifePoints(((Player) c.collisionObject).getLifePoints() - hitPoints);
                ((Player) c.collisionObject).applyLinearImpulse(new Vector2(-630f,-280f));
            }
            if(c.siteHidden == PhysicsController.intersectDirection.RIGHT){
                ((Player) c.collisionObject).setLifePoints(((Player) c.collisionObject).getLifePoints() - hitPoints);
                ((Player) c.collisionObject).applyLinearImpulse(new Vector2(630f,-280f));
            }

        }
    }

    public boolean isAlive(){
        return isAlive;
    }

    @Override
    public void draw(Canvas canvas){
        if(isActive){
            if (bmp != null && isActive && !isFlipped) {
                //if(Constants.debugBuild) {
                    Paint p = new Paint();
                    p.setColor(Color.argb(128, 0, 65, 200));
                    canvas.drawRect(hitbox.getRect(), p);
                //}
                canvas.drawBitmap(bmp.getBitmap(), Position.getX(), Position.getY()+drawOffsetY, null);
            } else if (isFlipped) {
                //if(Constants.debugBuild) {
                    Paint p = new Paint();
                    p.setColor(Color.argb(128, 0, 65, 200));
                    canvas.drawRect(hitbox.getRect(), p);
                //}
                BitmapDrawable bmp = flip(this.bmp);
                canvas.drawBitmap(bmp.getBitmap(), Position.getX(), Position.getY()+drawOffsetY, null);
            }
        }
    }

    /**
     * decreases hitpoints and returns true if enemy died
     * @param hitPoints
     */
    public boolean decreaseLife(int hitPoints){
        lifePoints -= hitPoints;
        if(lifePoints <= 0) return true;
        return false;
    }
}
