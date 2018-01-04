package de.hs_kl.imst.gatav.tilerenderer.util.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.util.LruCache;
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-03.
 */

public class AudioPlayer implements Runnable {

    private SoundPool sp;
    private MediaPlayer player;
    private Queue<Pair<Vector2, Integer>> loadingQueue = new ConcurrentLinkedQueue<>();
    private Queue<Pair<Vector2, Integer>> soundToPlay = new ConcurrentLinkedQueue<>();
    private AtomicBoolean playing = new AtomicBoolean(true);
    private Context ctx;
    private Player playerCharacter;
    // ~83.2 = 100% volume => @ ~4000 Units = 0%
    private final double audioThreshold = 83.2;
    private final int cacheElements = 10;
    private LruCache<Integer, Integer> cache = new LruCache<Integer, Integer>(cacheElements) {

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Integer oldElement, Integer newElement) {
            sp.unload(oldElement);
        }

        @Override
        protected int sizeOf(Integer key, Integer value) {
            return 1;
        }
    };



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

        sp.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            ArrayList<Integer> tmp = new ArrayList<>();
            for(Pair<Vector2, Integer> sound : loadingQueue) {
                if(sound.second == sampleId) {
                    int id = sp.play(sound.second, 0.5f, 0.5f, 1, 0, 1);
                    soundToPlay.add(new Pair<>(sound.first, id));
                    tmp.add(sound.second);
                }
            }
            loadingQueue.removeIf(v -> tmp.contains(v.second));
        });
    }

    public void addSound(Sounds s, Vector2 source) {
        if(playing.get()) {
            if(cache.get(s.getSoundResource()) != null) {
                int soundId = cache.get(s.getSoundResource());
                int id = sp.play(soundId, 0.5f, 0.5f, 1, 0, 1);
                soundToPlay.add(new Pair<>(source, id));
            } else {
                int soundId = sp.load(ctx, s.getSoundResource(), 1);
                cache.put(s.getSoundResource(), soundId);
                Pair<Vector2, Integer> element = new Pair<>(source, soundId);
                loadingQueue.add(element);
            }
            //soundToPlay.add(element);
        }
    }

    public void cleanup() {
        player.release();
        playing.set(false);
        for(Pair<Vector2, Integer> sound : soundToPlay) {
            sp.unload(sound.second);
        }
        sp.release();
        soundToPlay.clear();
        Log.d("AudioPlayer", "Cleaned!");
    }

    public void setPlayerCharacter(Player playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    @Override
    public void run() {
        while (playing.get()) {
            //not yet initialized!
            if (playerCharacter == null) continue;
            else {
                if (!soundToPlay.isEmpty()) {
                    for (Pair<Vector2, Integer> sound : soundToPlay) {
                        double volume = (audioThreshold - 10d * Math.log10(
                                4 * Math.PI * Math.pow(
                                        Math.abs(Vector2.distance(sound.first, playerCharacter.getPosition())), 2
                                    )
                                )
                        ) / audioThreshold;
                        volume = (volume > 1 ? 1 : volume); // prevent infinite volume
                        if(volume > 0) {
                            sp.setVolume(sound.second, (float) volume, (float) volume);
                            //sp.play(sound.second, (float) volume, (float) volume, 1, 0, 1);
                        }
                    }
                    //soundToPlay.clear();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
