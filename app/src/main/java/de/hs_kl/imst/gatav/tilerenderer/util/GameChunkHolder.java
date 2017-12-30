package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;

/**
 * Created by Sebastian on 2017-12-30.
 */

public class GameChunkHolder {
    private Bitmap[][] chunkArray;

    public Map<String, List<Collidable>> getObjectGroups() {
        return objectGroups;
    }

    public void setObjectGroups(Map<String, List<Collidable>> objectGroups) {
        this.objectGroups = objectGroups;
    }

    private Map<String, List<Collidable>> objectGroups = new HashMap<>();

    public Bitmap[][] getChunkArray() {
        return chunkArray;
    }

    public void setChunkArray(Bitmap[][] chunkArray) {
        this.chunkArray = chunkArray;
    }
}
