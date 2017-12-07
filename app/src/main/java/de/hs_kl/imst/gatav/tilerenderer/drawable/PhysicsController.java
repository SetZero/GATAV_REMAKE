package de.hs_kl.imst.gatav.tilerenderer.drawable;

import java.util.ArrayList;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by keven on 07.12.2017.
 */

public class PhysicsController {
    ArrayList<MovableGraphics> physicals = new ArrayList<MovableGraphics>();
    float gravity = 1.8f;
    public PhysicsController(){

    }

    public void Update(){

        for(MovableGraphics item: physicals){
            //if(item.getHitbox().isCollidingWith())
            if(!onGround(item)) {
                item.setVelocity(item.getVelocity() - gravity);
                item.setDirectionVec(new Vector2(item.getDirectionVec().getX(), 90f));
            }
        }
    }

    public boolean onGround(MovableGraphics item){
        //TODO
        return true;
    }

}


