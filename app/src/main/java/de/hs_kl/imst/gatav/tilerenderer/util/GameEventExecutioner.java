package de.hs_kl.imst.gatav.tilerenderer.util;

import android.support.v7.app.AppCompatActivity;

/**
 * Executes game events
 * Created by Sebastian on 2017-12-25.
 */

public class GameEventExecutioner {
    private AppCompatActivity activity;

    public GameEventExecutioner(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * go back to title screen (if player is in game, otherwise this will just finish the activity)
     */
    public void finishLevel() {
        activity.finish();
    }
}
