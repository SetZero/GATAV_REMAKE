package de.hs_kl.imst.gatav.tilerenderer.util.audio.events;

import android.util.Log;

import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;

/**
 * Created by Sebastian on 2018-01-05.
 */

public class Owl {
    private Timer timer;
    private AudioPlayer audioPlayer;
    private double lastOwl = -3;
    private double timeBetween = 4; //seconds
    private Vector2 position;

    public Owl(Vector2 position, AudioPlayer audioPlayer, Timer timer) {
        this.audioPlayer = audioPlayer;
        this.timer = timer;
        this.position = position;
    }

    public void update() {
        if(lastOwl + timeBetween <= timer.getElapsedTime()) {
            lastOwl = timer.getElapsedTime();
            audioPlayer.addSound(Sounds.OWL, position);
        }
    }
}
