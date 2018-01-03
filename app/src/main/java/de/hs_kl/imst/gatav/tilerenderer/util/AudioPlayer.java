package de.hs_kl.imst.gatav.tilerenderer.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

/**
 * Created by Sebastian on 2018-01-03.
 */

public class AudioPlayer implements  Runnable {

    private SoundPool sp;
    private MediaPlayer player;

    public AudioPlayer(Context ctx) {
        //init();
        player = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = ctx.getAssets().openFd("music/GloriousMorning2.mp3");
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            player.prepare();
            player.setVolume(1f, 1f);
            player.setLooping(true);
            player.start();
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

    @Override
    public void run() {
    }
}
