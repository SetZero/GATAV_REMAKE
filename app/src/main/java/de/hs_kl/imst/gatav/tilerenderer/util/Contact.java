package de.hs_kl.imst.gatav.tilerenderer.util;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController.intersectDirection;

/**
 * Created by keven on 10.12.2017.
 */

public class Contact {
    public intersectDirection siteHidden;
    public Collidable collisions;
    public Object collisionObject;
    public String params;

    public Contact(intersectDirection siteHit, Collidable other) {
        siteHidden = siteHit;
        collisions = other;
    }

    public Contact(intersectDirection siteHit, Collidable other, String param) {
        this(siteHit, other);
        params = (param);
    }

    public Contact(intersectDirection siteHit, Collidable other, MovableGraphics item) {
        this(siteHit, other);
        collisionObject = (item);
    }

    public Contact setCollisionObject(MovableGraphics collisionObject) {
        this.collisionObject = collisionObject;
        return this;
    }
}
