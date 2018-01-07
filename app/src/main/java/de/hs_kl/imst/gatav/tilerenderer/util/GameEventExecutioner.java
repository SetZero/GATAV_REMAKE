package de.hs_kl.imst.gatav.tilerenderer.util;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sebastian on 2017-12-25.
 */

public class GameEventExecutioner {
    private AppCompatActivity activity;

    public GameEventExecutioner(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void finishLevel() {
        activity.finish();
    }
}
