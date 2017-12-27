package de.hs_kl.imst.gatav.tilerenderer.util;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Sebastian on 2017-12-25.
 */

public class GameEventExecutioner {
    private static AppCompatActivity activity;

    public GameEventExecutioner(AppCompatActivity activity) {
        this.activity = activity;
    }

    public static void finishLevel() {
        activity.finish();
        //Intent intent = new Intent(activity, MainActivity.class);
        //.putExtra("level", level);
        //activity.startActivity(intent);
    }
}
