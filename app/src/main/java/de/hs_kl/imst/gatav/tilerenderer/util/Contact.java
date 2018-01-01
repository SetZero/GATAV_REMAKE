package de.hs_kl.imst.gatav.tilerenderer.util;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController.intersectDirection;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;

/**
 * Created by keven on 10.12.2017.
 */

public class Contact {
    public intersectDirection siteHidden;
    public Collidable collisions;

    public Contact setCollisionObject(MovableGraphics collisionObject) {
        this.collisionObject = collisionObject;
        return this;
    }

    public Object collisionObject;
    /**
     * @param params
     * maybe useful in later implementions
     */
    public String params;
    public Contact(intersectDirection siteHit, Collidable other){
        siteHidden =siteHit; collisions =other;
    }
    public Contact(intersectDirection siteHit, Collidable other, String param){
        siteHidden= (siteHit); collisions = (other); params = (param);
    }
    public Contact(intersectDirection siteHit, Collidable other, MovableGraphics item){
        siteHidden= (siteHit); collisions = (other); collisionObject = (item);
    }
}
