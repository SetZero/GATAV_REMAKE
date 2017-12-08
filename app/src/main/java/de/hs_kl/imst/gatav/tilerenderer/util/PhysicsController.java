package de.hs_kl.imst.gatav.tilerenderer.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.TileLoader;
import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by keven on 07.12.2017.
 */

public class PhysicsController {
    public ArrayList<MovableGraphics> getPhysicals() {
        return physicals;
    }

    public void addPhysical(MovableGraphics physical) {
        this.physicals.add(physical);
    }

    private ArrayList<MovableGraphics> physicals = new ArrayList<MovableGraphics>();
    private float gravity = 18f;
    private Map<String, List<Collidable>> objects;

    public PhysicsController(Map<String, List<Collidable>> objects){
        this.objects = objects;
    }

    public void Update(float delta){

        for(MovableGraphics item: physicals){
            //gravity
            if(!onGround(item)) {
                //velocity wird nur verringert wenn bewegung auf y achse
                Log.d("gravity", "applied");
                if(item.getDirectionVec().getY()<0f)
                    item.setVelocity(item.getVelocity() - gravity*delta);
                item.impact(new Vector2(0f,905f),0f);
            }
            else{   //wenn der movable den boden berührt und der richtungsvektor noch nach unten zeigt
                    // wird die gravitation beendet (spieler kann sich nicht nach unten bewegen)
                if(item.getDirectionVec().getY()>0f){
                    Log.d("gravity","removed");
                item.setDirectionVec(new Vector2(item.getDirectionVec().getX(),0f));}
            }
        }
    }

    public boolean onGround(MovableGraphics item){

        for(Collidable c : objects.get("Kollisionen")){
            //colliding with funktioniert nicht
            Log.d("player rect","bottom "+item.getHitbox().getRect().bottom+" top "+item.getHitbox().getRect().top+" left "+item.getHitbox().getRect().left+" right "+item.getHitbox().getRect().right);
            if(item.getHitbox() != null && item.getHitbox().isCollidingWith((Rectangle)c)) {
                return true;}
        }
        return false;
    }

}


