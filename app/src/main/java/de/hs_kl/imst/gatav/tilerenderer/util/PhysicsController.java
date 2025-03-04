package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Class which controlls Gravity and a CollisionSystem for all MovableGraphics in the loaded world
 * the Controller works with rectangles
 * Created by keven on 07.12.2017.
 */

public class PhysicsController {
    private ArrayList<MovableGraphics> physicals = new ArrayList<MovableGraphics>();
    private float gravity = 680f;//680.5f;
    private List<Collidable> list;
    private List<MovableGraphics> toRemove = new ArrayList<>();

    public PhysicsController(World world) {
        list = new ArrayList<>();
        list.addAll(world.getObjects().get(Constants.collisionObjectGroupString));
    }

    public ArrayList<MovableGraphics> getPhysicals() {
        return physicals;
    }

    public void addPhysical(MovableGraphics physical) {
        this.physicals.add(physical);
    }

    public void removePhysical(MovableGraphics x) {
        toRemove.add(x);
    }

    public List<MovableGraphics> getToRemove() {
        return toRemove;
    }

    public void cleanup() {
        physicals.removeAll(toRemove);
        list.removeIf(v -> toRemove.stream().map(MovableGraphics::getHitbox).collect(Collectors.toList()).contains(v));
        toRemove.clear();
    }

    public void Update(float delta, GameCamera cam) {
        //heavy lag protection
        if(delta > 2) delta = 2;
        for (MovableGraphics item : physicals) {
            boolean groundCollision = false;
            boolean noCollision = true;
            float groundY = 0.0f;
            boolean rightCollision = false;
            boolean topCollision = false;
            boolean leftCollision = false;
            item.isRightColliding = false;
            item.isLeftColliding = false;
            Rect r = (item.getHitbox().getRect());
            if (cam.isRectInView(r)) {
                ArrayList<Contact> collision = isColliding(item, cam);
                for (Contact c : collision) {
                    if (c.siteHit == intersectDirection.BOTTOM) {
                        groundY = ((Rectangle) c.collisions).getRect().top;
                        groundCollision = true;
                        noCollision = false;
                    }
                    if (c.siteHit == intersectDirection.TOP) {
                        topCollision = true;
                        noCollision = false;
                    }
                    if (c.siteHit == intersectDirection.LEFT) {
                        leftCollision = true;
                        noCollision = false;
                        item.isLeftColliding = true;
                    }
                    if (c.siteHit == intersectDirection.RIGHT) {
                        rightCollision = true;
                        noCollision = false;
                        item.isRightColliding = true;
                    }
                }
                if (noCollision) {
                    item.impact(new Vector2(0f, gravity * delta ));
                    item.isOnGround = false;
                } else {
                    item.isOnGround = groundCollision;
                    if (leftCollision) {
                        if (item.getVelocity().x < 0f)
                            item.setVelocity(new Vector2(0f, item.getVelocity().y));
                        item.setLinearImpulse(new Vector2(0f, item.getLinearImpulse().y));
                        if (!item.isOnGround) {
                            item.impact(new Vector2(0f, gravity * delta ));
                        }
                    }
                    if (rightCollision) {
                        if (item.getVelocity().x > 0f)
                            item.setVelocity(new Vector2(0f, item.getVelocity().y));
                        item.setLinearImpulse(new Vector2(0f, item.getLinearImpulse().y));

                        if (!item.isOnGround) {
                            item.impact(new Vector2(0f, gravity * delta ));
                        }
                    }
                    if (topCollision) {
                        if (item.getVelocity().y < 0)
                            item.setVelocity(new Vector2(item.getVelocity().x, 0f));
                        item.setLinearImpulse(new Vector2(item.getLinearImpulse().x, 0f));

                        if (!item.isOnGround) {
                            item.impact(new Vector2(0f, gravity * delta ));
                        }
                    }
                    if (groundCollision) {
                        if (item.getVelocity().y > 0) {
                            item.setVelocity(new Vector2(item.getVelocity().x, 0f));
                            item.setPosition(new Vector2(item.getPosition().x, groundY - item.getHitbox().getHeight() + 1));
                        }

                    }
                }

            }
        }
        for (MovableGraphics item : physicals) {
            for (MovableGraphics other : physicals) {
                if (item != other) {
                    item.onCollision(collisionDirection(other.getHitbox(), item).setCollisionObject(other));
                }
            }
        }
    }


    private ArrayList<Contact> isColliding(MovableGraphics item, GameCamera cam) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Rect view = cam.getCameraViewRect();
        view.bottom = view.bottom + view.height() / 2;
        view.top = view.top - view.height() / 2;
        view.left = view.left - view.width() / 2;
        view.right = view.right + view.width() / 2;
        for (Collidable c : list) {
            if (cam.isRectInView(view))
                contacts.add(collisionDirection(c, item));
        }
        return contacts;
    }

    /**
     * checks if the movable is colliding with another object
     * @param c the other collidable
     * @param item the item that will be checked
     * @return a contact object with informations about the collision
     */
    private Contact collisionDirection(Collidable c, MovableGraphics item) {
        Rect rectA = (item.getHitbox().getRect());
        Rect rectB = (((Rectangle) c).getRect());
        if (rectA.intersect(rectB)) {

            float wy = (rectA.width() + rectB.width()) * (rectA.centerY() - rectB.centerY());
            float hx = (rectA.height() + rectB.height()) * (rectA.centerX() - rectB.centerX());
            if (wy < hx) {
                if (wy < -hx) {
                    return new Contact(intersectDirection.BOTTOM, c);
                }
                if (wy > -hx) {
                    return new Contact(intersectDirection.LEFT, c);
                }
            }else if (wy > hx) {
                if (wy > -hx) {
                    return new Contact(intersectDirection.TOP, c);
                }
                if (wy < -hx) return new Contact(intersectDirection.RIGHT, c);
            }
        }
        return new Contact(intersectDirection.DONT, null);
    }

    public enum intersectDirection {
        LEFT, RIGHT, TOP, BOTTOM, DONT
    }

}