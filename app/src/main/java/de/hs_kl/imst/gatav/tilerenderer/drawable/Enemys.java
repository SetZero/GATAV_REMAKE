package de.hs_kl.imst.gatav.tilerenderer.drawable;

import de.hs_kl.imst.gatav.tilerenderer.util.Contact;
import de.hs_kl.imst.gatav.tilerenderer.util.PhysicsController;

/**
 * Created by keven on 17.12.2017.
 */

public abstract class Enemys extends MovableGraphics implements Destroyable, CollisionReactive  {
    protected float lifePoints;
    public static int hitPoints;
    public Enemys(float x, float y, float lifePoints, int hitPoints){
        super(x,y);
        this.lifePoints = lifePoints;
        this.hitPoints = hitPoints;
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
    public void react(Contact c) {
        if(c.params.equals("Player") && c.siteHidden == PhysicsController.intersectDirection.TOP){
            this.lifePoints -= Player.hitPoints;
        }
    }
}
