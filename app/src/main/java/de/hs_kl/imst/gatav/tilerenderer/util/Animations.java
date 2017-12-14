package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Drawables;

import static java.lang.Math.round;

/**
 * Created by keven on 14.12.2017.
 */

public class Animations {
    ArrayList<BitmapDrawable> animations = new ArrayList<BitmapDrawable>();
    float timeStep = 1.0f;
    public Animations(float timeStep){
        this.timeStep = timeStep;
    }
    public void addAnimation(ArrayList<BitmapDrawable> d){
        animations = d;
    }

    /**
     *
     * @param faded time faded since last update
     * @return the current animation in dependence from the faded time
     */
    public BitmapDrawable getDrawable(float faded){
        int x = Math.round(faded/timeStep);
        //Log.d("size / x",""+animations.size()+" "+x);
        if(x >= animations.size()){
            return animations.get(animations.size()-1);
        }
        return animations.get(x);
    }
    public boolean isFinished(float faded){
        int x = Math.round(faded/timeStep);
        if(x>=animations.size()){
            return true;
        }
        return false;
    }
}
