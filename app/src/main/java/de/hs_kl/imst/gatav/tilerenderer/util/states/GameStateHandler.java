package de.hs_kl.imst.gatav.tilerenderer.util.states;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2017-12-27.
 */

public class GameStateHandler {
    private Vector2 lastCheckpoint;
    private double lastCheckpointTime = 0.0;
    private long score = 0;

    public Vector2 getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(Vector2 lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public double getLastCheckpointTime() {
        return lastCheckpointTime;
    }

    public void setLastCheckpointTime(double lastCheckpointTime) {
        this.lastCheckpointTime = lastCheckpointTime;
    }
}
