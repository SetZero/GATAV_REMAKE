package de.hs_kl.imst.gatav.tilerenderer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Drawables;
import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;

import static java.lang.Math.round;

/**
 * Created by keven on 14.12.2017.
 */

public class Animations {
        List<BitmapDrawable> animations = new ArrayList<BitmapDrawable>();
        float timeStep = 1.0f;
        public Animations(float timeStep){
            this.timeStep = timeStep;
        }
        public void addAnimation(List<BitmapDrawable> d){
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

    /**
     * ONLY png
     * @param fileName path from assets until the file + filename without number
     * @param range how much frames? 1 - n
     * @param dstWidth
     * @param dstHeight
     * @return
     * @throws IOException
     */
        public static List<BitmapDrawable> frameLoad(String fileName, int range,int dstWidth, int dstHeight,Context context) throws IOException
        {
            List<BitmapDrawable> list = new ArrayList<>();
            InputStream is;
            Bitmap bmp;
            for(int i = 1; i<=range;i++){
                is = context.getAssets().open(fileName+i+".png");
                bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is),dstWidth,dstHeight,false);
                list.add(new BitmapDrawable(bmp));
                is.close();
            }
            return list;
        }
    }
