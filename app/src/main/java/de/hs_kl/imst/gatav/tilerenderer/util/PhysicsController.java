package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

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
    private float gravity = 8.5f;
    private World world;
    private  List<Collidable> list;

    public PhysicsController(World world){
        this.world = world;
        list = new ArrayList<Collidable>();
        for(Collidable c: world.getObjects().get("Kollisionen")) list.add(c);
    }

    public void Update(float delta){

        for(MovableGraphics item: physicals){
            //gravity
            //Log.d("velocity",item.getVelocity().x+"");
            ArrayList<Contact> collision = isColliding(item);
            boolean groundCollision = false;
            boolean noCollision = true;
            boolean rightCollision = false;
            boolean topCollision = false;
            boolean leftCollision = false;

            for(Contact c : collision) {
               // Log.d("proof", ""+c.siteHidden.name());
                if(c.siteHidden == intersectDirection.BOTTOM){
                    groundCollision = true;
                    noCollision = false;
                }
                if(c.siteHidden == intersectDirection.TOP){
                    topCollision = true;
                    noCollision = false;
                }
                if(c.siteHidden == intersectDirection.LEFT){
                    leftCollision = true;
                    noCollision = false;
                }
                if(c.siteHidden == intersectDirection.RIGHT){
                    rightCollision = true;
                    noCollision = false;
                }
            }
            if ( noCollision ) {
                item.impact(new Vector2(0f, gravity));
                item.isOnGround = false;
            } else
                {
                    item.isOnGround = groundCollision;
                if(leftCollision ){
                    if(item.getVelocity().x<0f)
                    item.setVelocity(new Vector2(0f,item.getVelocity().y));
                }
                if(rightCollision ){
                    if(item.getVelocity().x>0f)
                        item.setVelocity(new Vector2(0f,item.getVelocity().y));
                }
                if(topCollision){
                    if (item.getVelocity().y < 0){
                        item.setVelocity(new Vector2(item.getVelocity().x,gravity));
                    }
                }
                if (groundCollision) {
                    if(item.getVelocity().y > 0)
                    item.setVelocity(new Vector2(item.getVelocity().x, 0f));
                }
            }

        }
    }


    public ArrayList<Contact> isColliding(MovableGraphics item) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Rect rectA = item.getHitbox().getRect();
        Rect rectB;

        for(Collidable c : list){
            rectB = ((Rectangle) c).getRect();
            if(item.getHitbox().isCollidingWith(c)) {

                float wy = (rectA.width()+rectB.width())*(rectA.centerY()-rectB.centerY());
                float hx = (rectA.height()+rectB.height())*(rectA.centerX()-rectB.centerX());

                if(wy>hx){
                    if(wy> -hx){
                        contacts.add(new Contact(intersectDirection.TOP,c));
                    }
                    if(wy<-hx) contacts.add(new Contact(intersectDirection.RIGHT,c));
                }
                else if (wy<hx) {
                    if(wy> -hx){
                        contacts.add(new Contact(intersectDirection.LEFT,c));
                        Log.d("contact", "left side");
                    }
                    if(wy< -hx){
                        contacts.add(new Contact(intersectDirection.BOTTOM,c));
                    }
                }
            }
            else{
                contacts.add(new Contact(intersectDirection.DONT,null));
            }
        }
        return contacts;
    }

    public enum intersectDirection {
        LEFT,RIGHT,TOP,BOTTOM,DONT,DEFAULT;
    }

}