package de.hs_kl.imst.gatav.tilerenderer.util.audio.events;

import java.util.concurrent.ThreadLocalRandom;

import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;

/**
 * Plays the sound of an owl after a random time in between
 * Created by Sebastian on 2018-01-05.
 */

public class Owl extends RepeatingEvent {

    public Owl(Vector2 position, AudioPlayer audioPlayer, Timer timer) {
        super(position, audioPlayer, timer);
    }

    @Override
    public void update() {
        if (lastSound + timeBetween <= timer.getElapsedTime()) {
            int randomNum = ThreadLocalRandom.current().nextInt(-2, 3);
            lastSound = timer.getElapsedTime() + randomNum;
            audioPlayer.addSound(Sounds.OWL, position, 90);
        }
    }
}
