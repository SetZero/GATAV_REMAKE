package de.hs_kl.imst.gatav.tilerenderer.util.audio.events;

import java.util.concurrent.ThreadLocalRandom;

import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;

/**
 * Plays the sound of the sea
 * Created by Sebastian on 2018-01-05.
 */

public class Siren extends RepeatingEvent {

    public Siren(Vector2 position, AudioPlayer audioPlayer, Timer timer) {
        super(position, audioPlayer, timer);
        this.timeBetween = 6;
    }

    @Override
    public void update() {
        if (lastSound + timeBetween <= timer.getElapsedTime()) {
            int randomNum = ThreadLocalRandom.current().nextInt(-2, 3);
            lastSound = timer.getElapsedTime() + randomNum;
            audioPlayer.addSound(Sounds.SIREN, position, 90);
        }
    }
}
