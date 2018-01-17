package de.hs_kl.imst.gatav.tilerenderer.drawable;

import de.hs_kl.imst.gatav.tilerenderer.util.Contact;

/**
 * Created by keven on 17.12.2017.
 */

/**
 * Interface that expresses if an item or entity can collide with another
 */
public interface CollisionReactive {
    void onCollision(Contact c);
}
