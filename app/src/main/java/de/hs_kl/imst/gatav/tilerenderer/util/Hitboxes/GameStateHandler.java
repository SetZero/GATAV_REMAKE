package de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2017-12-27.
 */

public class GameStateHandler {
    private Vector2 lastCheckpoint;
    private long score = 0;

    public Vector2 getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(Vector2 lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
