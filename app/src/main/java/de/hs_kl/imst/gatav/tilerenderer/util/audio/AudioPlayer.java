package de.hs_kl.imst.gatav.tilerenderer.util.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-03.
 */

public class AudioPlayer implements Runnable {

    private SoundPool sp;
    private MediaPlayer player;
    private Queue<Pair<Vector2, Integer>> soundToPlay = new ConcurrentLinkedQueue<>();
    private boolean playing = true;
    private Context ctx;
    private Player playerCharacter;
    private static final double audioThreshold = 83.2;


    public AudioPlayer(Context ctx) {
        init();
        this.ctx = ctx;
        player = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = ctx.getAssets().openFd("music/GloriousMorning2.mp3");
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            player.prepare();
            player.setVolume(1f, 1f);
            player.setLooping(true);
            //player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sp = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();
    }

    public void addSound(Sounds s, Vector2 source) {
        int soundId = sp.load(ctx, s.getSoundResource(), 1);
        Pair<Vector2, Integer> element = new Pair<>(source, soundId);
        soundToPlay.add(element);
    }

    @Override
    public void run() {
        while (playing) {
            if (playerCharacter == null) playerCharacter = GameContent.player;
            else {
                if (!soundToPlay.isEmpty()) {
                    for (Pair<Vector2, Integer> sound : soundToPlay) {
                        // ~83.2 = 100% volume => @ ~4000 Units = 0
                        double volume = (audioThreshold - 10d * Math.log10(
                                4 * Math.PI * Math.pow(
                                        Math.abs(Vector2.distance(sound.first, playerCharacter.getPosition())), 2
                                    )
                                )
                        ) / audioThreshold;
                        if(volume > 0) {
                            sp.play(sound.second, (float) volume, (float) volume, 1, 0, 1);
                            Log.d("Sound", "Starting Sound: " + sound.second.toString());
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //soundToPlay.clear();
                }
            }
        }
    }
}
