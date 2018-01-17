package de.hs_kl.imst.gatav.tilerenderer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by keven on 14.12.2017.
 */

public class Animations {
    List<BitmapDrawable> animations = new ArrayList<BitmapDrawable>();
    float timeStep = 1.0f;

    public Animations(float timeStep) {
        this.timeStep = timeStep;
    }

    /**
     * ONLY png files
     *
     * @param fileName  path from assets until the file + filename without number
     * @param range     how much frames? 1 - n
     * @param dstWidth  the width it will be scaled to
     * @param dstHeight the height it will be scaled to
     * @return list of BitmapDrawables
     * @throws IOException
     */
    public static List<BitmapDrawable> frameLoad(String fileName, int range, int dstWidth, int dstHeight, Context context) throws IOException {
        List<BitmapDrawable> list = new ArrayList<>();
        InputStream is;
        Bitmap bmp;
        for (int i = 1; i <= range; i++) {
            is = context.getAssets().open(fileName + i + ".png");
            bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), dstWidth, dstHeight, false);
            list.add(new BitmapDrawable(bmp));
            is.close();
        }
        return list;
    }

    public void addAnimation(List<BitmapDrawable> d) {
        animations = d;
    }

    /**
     * @param faded time faded since last update
     * @return the current animation in dependence from the faded time
     */
    public BitmapDrawable getDrawable(float faded) {
        int x = Math.round(faded / timeStep);
        if (x >= animations.size()) {
            return animations.get(animations.size() - 1);
        }
        x = (x < 0 ? 0 : x);
        return animations.get(x);
    }

    /**
     *
     * @param faded time since last update
     * @return if the animation is finished
     */
    public boolean isFinished(float faded) {
        int x = Math.round(faded / timeStep);
        return x >= animations.size();
    }
}
