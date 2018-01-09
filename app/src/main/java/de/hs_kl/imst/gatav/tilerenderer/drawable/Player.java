package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import java.io.IOException;
import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.util.Animations;
import de.hs_kl.imst.gatav.tilerenderer.util.Constants;
import de.hs_kl.imst.gatav.tilerenderer.util.Contact;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController;
import de.hs_kl.imst.gatav.tilerenderer.util.ScaleHelper;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;
import de.hs_kl.imst.gatav.tilerenderer.util.states.Direction;
import de.hs_kl.imst.gatav.tilerenderer.util.states.PlayerStates;

public final class Player extends MovableGraphics implements Destroyable, CollisionReactive {
    public static int hitPoints = 40;
    protected Animations dieng;
    float dieTimer = 0.0f;
    private byte doublejump = 0;
    private float lifePoints = 150;
    private float speed = 300f;
    private boolean isAlive = true;
    private Direction stopDirection = Direction.IDLE;
    private BitmapDrawable idle;
    private Animations run;
    private int score;
    private AudioPlayer audioPlayer;
    private Vector2 startPosition;
    private float animTime = 0f;

    public Player(float x, float y, Context context, AudioPlayer audioPlayer) {
        super(x, y);
        try {
            InputStream is = context.getAssets().open("dynamics/player/Player.png");
            loadGraphic(is, 17, 35, ScaleHelper.getEntitiyScale());
            run = new Animations(1f / 4f);
            is.close();
            is = context.getAssets().open("dynamics/player/Player.png");
            run.addAnimation(super.loadTextures(is, 17, 35, 1, 4, ScaleHelper.getEntitiyScale()));
            idle = run.getDrawable(0f);
            dieng = new Animations(1f / 4f);
            friction = 0.0f;
            dieng.addAnimation(Animations.frameLoad("dynamics/player/Die", 4, 25 * ScaleHelper.getEntitiyScale(), 40 * ScaleHelper.getEntitiyScale(), context));
            hitbox = new Rectangle((int) x, (int) y, width - 35, height - 5);
            isActive = true;
            this.audioPlayer = audioPlayer;
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector2 getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Vector2 startPosition) {
        this.startPosition = startPosition;
    }

    public void softResetPlayer() {
        setLifePoints(150);
        setIsAlive(true);
        setActive(true);
        setVelocity(new Vector2(0, 0));
        setLinearImpulse(new Vector2(0, 0));
    }

    public void resetPlayer() {
        softResetPlayer();
        setPosition(startPosition);
        setScore(0);
    }

    public float getLifePoints() {
        return lifePoints;
    }

    public void setLifePoints(float lifePoints) {
        this.lifePoints = lifePoints;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void move(Direction direction) {
        previous = currentDirection;
        switch (direction) {
            case UP: {
                if (velocity.getY() != 0 && previous == Direction.DOWN) doublejump++;
                if (doublejump < 2) {
                    audioPlayer.addSound(Sounds.JUMP, getPosition());
                    velocity.setY(0f);
                    impact(new Vector2(0f, -550f));
                    currentDirection = Direction.UP;
                    doublejump++;
                }
                break;
            }
            case LEFT: {
                if (!isLeftColliding) {
                    if (velocity.getX() > 0 && velocity.x <= speed && !isOnGround) {
                        velocity.x = 0f;
                        impact(new Vector2(-speed, 0f));
                    }
                    if (velocity.x > -speed) {
                        impact(new Vector2(-speed, 0f));
                    }
                }
                break;
            }
            case RIGHT: {
                if (!isRightColliding) {
                    if (velocity.getX() < 0 && velocity.x >= -speed && !isOnGround) {
                        velocity.x = 0f;
                        impact(new Vector2(speed, 0f));
                    }
                    if (velocity.x < speed) {
                        impact(new Vector2(speed, 0f));
                    }
                }
                break;
            }

        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean b) {
        isAlive = b;
    }

    /**
     * interrupts LEFT or RIGHT movement the correct way (is the player on Ground?)
     * this interrupt will occur on the next update (if interrupt is possible)
     * @param direction which the movement should be stopped
     */
    public void stopMove(Direction direction) {
        stopDirection = direction;
    }


    @Override
    public void update(float delta) {
        if(isActive) {
            if (isAlive) {
                super.update(delta);
                if (lifePoints <= 0)
                    isAlive = false;
                stateHandle();
                animationHandle(delta);
            } else {
                if (dieng.isFinished(dieTimer)) {
                    setChanged();
                    notifyObservers(PlayerStates.DEAD);
                } else {
                    bmp = dieng.getDrawable(dieTimer);
                }
                dieTimer += delta;
            }
        }
    }

    private void stateHandle() {
        if (velocity.getY() == 0f && velocity.getX() == 0) {
            previous = currentDirection;
            currentDirection = Direction.IDLE;
        }
        if (doublejump != 0 && velocity.getY() == 0 && isOnGround) doublejump = 0;
        if (isOnGround && stopDirection != Direction.IDLE) {
            if (stopDirection == Direction.LEFT && velocity.getX() < 0f) {
                impact(new Vector2(speed, 0f));
                currentDirection = Direction.IDLE;
            } else if (stopDirection == Direction.RIGHT && velocity.getX() > 0f) {
                impact(new Vector2(-speed, 0f));
                currentDirection = Direction.IDLE;
            }
            stopDirection = Direction.IDLE;
        }
        if (isOnGround && velocity.x > 0f && velocity.y == 0) {
            currentDirection = Direction.RIGHT;
        } else if (isOnGround && velocity.x < 0f && velocity.y == 0) {
            currentDirection = Direction.LEFT;
        }
        if (velocity.getY() != 0 && currentDirection != Direction.UP)
            currentDirection = Direction.DOWN;
    }

    private void animationHandle(float delta) {
        if (run.isFinished(animTime)) animTime = 0;
        if (currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT)
            animTime += delta;
        if (currentDirection == Direction.IDLE) {
            bmp = idle;
        }
        // running right
        if (currentDirection == Direction.RIGHT) {
            bmp = run.getDrawable(animTime);
            isFlipped = false;
        } else if (currentDirection == Direction.LEFT) {
            bmp = run.getDrawable(animTime);
            isFlipped = true;
        }
        if (currentDirection == Direction.UP || currentDirection == Direction.IDLE || currentDirection == Direction.IDLE || currentDirection == Direction.DOWN) {
            bmp = idle;
        }
        if (velocity.getX() > 10) {
            isFlipped = false;
        } else if (velocity.getX() < -10) {
            isFlipped = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (bmp != null  && !isFlipped) {
            if (Constants.debugBuild) {
                Paint p = new Paint();
                p.setColor(Color.argb(128, 0, 65, 200));
                canvas.drawRect(hitbox.getRect(), p);
            }
            canvas.drawBitmap(bmp.getBitmap(), Position.getX(), Position.getY(), null);
        } else if (isFlipped) {
            if (Constants.debugBuild) {
                Paint p = new Paint();
                p.setColor(Color.argb(128, 0, 65, 200));
                canvas.drawRect(hitbox.getRect(), p);
            }
            BitmapDrawable bmp = flip(this.bmp);
            canvas.drawBitmap(bmp.getBitmap(), Position.getX(), Position.getY(), null);
        }
    }

    @Override
    public void processHit(float hit) {
        lifePoints -= hit;
    }

    @Override
    public boolean isDestroyed() {
        return !(lifePoints > 0f);
    }

    @Override
    public void onCollision(Contact c) {
        if (c.siteHit == PhysicsController.intersectDirection.BOTTOM && c.collisionObject instanceof Enemies) {
            if (((Enemies) c.collisionObject).isAlive() && isAlive) {
                velocity.y = 0.0f;
                impact(new Vector2(0f, -400f));
                if (((Enemies) c.collisionObject).decreaseLife(hitPoints))
                    score += ((Enemies) c.collisionObject).getScorePoints();
            }

        }

    }

}
