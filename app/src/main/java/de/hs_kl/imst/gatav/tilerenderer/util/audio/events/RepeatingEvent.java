package de.hs_kl.imst.gatav.tilerenderer.util.audio.events;

import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

/**
 * Created by Sebastian on 2018-01-12.
 */

public abstract class RepeatingEvent implements AudioEvent{
    protected Timer timer;
    protected AudioPlayer audioPlayer;
    protected double lastSound = -3;
    protected double timeBetween = 8; //seconds
    protected Vector2 position;

    public RepeatingEvent(Vector2 position, AudioPlayer audioPlayer, Timer timer) {
        this.audioPlayer = audioPlayer;
        this.timer = timer;
        this.position = position;
    }
}
