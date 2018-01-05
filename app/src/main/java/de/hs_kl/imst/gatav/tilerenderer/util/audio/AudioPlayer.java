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
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;

/**
 * Created by Sebastian on 2018-01-03.
 */

public class AudioPlayer implements Runnable {

    private final int cacheElements = 10;
    // ~83.2 = 100% volume => @ ~4000 Units = 0%
    private final double audioThreshold = 83.2;
    private SoundPool sp;
    private MediaPlayer player;
    private Queue<Pair<Vector2, Integer>> loadingQueue = new ConcurrentLinkedQueue<>();
    private AtomicBoolean playing = new AtomicBoolean(true);
    private Context ctx;
    private Player playerCharacter;
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
    private LruCache<Integer, Vector2> soundToPlay = new LruCache<Integer, Vector2>(cacheElements) {

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Vector2 oldElement, Vector2 newElement) {
            sp.stop(key);
        }

        @Override
        protected int sizeOf(Integer key, Vector2 value) {
            return 1;
        }
    };


    public AudioPlayer(Context ctx) {
        //Start Sound Pool
        initSoundPool();
        this.ctx = ctx;
        //start Media Player
        player = new MediaPlayer();
        try {
            //Start Playing BGM
            AssetFileDescriptor descriptor = ctx.getAssets().openFd("music/GloriousMorning2.mp3");
            player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            player.prepare();
            player.setVolume(0.5f, 0.5f);
            player.setLooping(true);
            //player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSoundPool() {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sp = new SoundPool.Builder()
                .setMaxStreams(cacheElements)
                .setAudioAttributes(attrs)
                .build();
        //If there is a new Song to Play, wait for it to load and Play it!
        sp.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            ArrayList<Integer> tmp = new ArrayList<>();
            for (Pair<Vector2, Integer> sound : loadingQueue) {
                if (sound.second == sampleId) {
                    int id = sp.play(sound.second, 0.5f, 0.5f, 1, 0, 1);
                    soundToPlay.put(id, sound.first);
                    tmp.add(sound.second);
                }
            }
            loadingQueue.removeIf(v -> tmp.contains(v.second));
        });
    }

    public void addSound(Sounds s, Vector2 source) {
        //If we can play a song....
        if (playing.get() && playerCharacter != null) {
            double distance = Math.abs(Vector2.distance(source, playerCharacter.getPosition()));
            //don't add out of reach sounds
            if (distance > 3900) return;
            if (cache.get(s.getSoundResource()) != null) {
                //...get it from the cache and play it
                int soundId = cache.get(s.getSoundResource());
                int id = sp.play(soundId, 0.5f, 0.5f, 1, 0, 1);
                //soundToPlay.add(new Pair<>(source, id));
                soundToPlay.put(id, source);
                //Log.d("addSound", "Position: (" + source.getX() + ", " + source.getY() + ")");
            } else {
                //add it to the loading queue if we don't have it
                int soundId = sp.load(ctx, s.getSoundResource(), 1);
                cache.put(s.getSoundResource(), soundId);
                Pair<Vector2, Integer> element = new Pair<>(source, soundId);
                loadingQueue.add(element);
            }
        }
    }

    public void cleanup() {
        player.release();
        playing.set(false);
        sp.release();
        soundToPlay.evictAll();
    }

    public void setPlayerCharacter(Player playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    @Override
    public void run() {
        //As long as we can play songs
        while (playing.get()) {
            //If we have a reference to the player (for relative audio position)
            if (playerCharacter == null) continue;
            else {
                //go over all audio resources
                Map<Integer, Vector2> snapshot = soundToPlay.snapshot();
                for (Map.Entry<Integer, Vector2> sound : snapshot.entrySet()) {
                    double distance = Math.abs(Vector2.distance(sound.getValue(), playerCharacter.getPosition()));
                    //stolen from a book ;-)
                    double volume = (audioThreshold - 10d * Math.log10(
                            4 * Math.PI * Math.pow(
                                    distance, 2
                            )
                    )
                    ) / audioThreshold;
                    //playerCharacter.getHitbox().getWidth()
                    //Sigmoid for thresholding of stereo sound position
                    double left = 0.875 + Math.tanh((playerCharacter.getPosition().getX() - sound.getValue().getX()) * 0.001) * 0.125;
                    double right = 0.875 + Math.tanh(-(playerCharacter.getPosition().getX() - sound.getValue().getX()) * 0.001) * 0.125;
                    //Log.d("AudioPlayer", "Left: " + left + ", Right: " + right + ", Distance: " + distance + ", Sound: (" +sound.getValue().getX()+", " + sound.getValue().getY()+")");
                    volume = (volume > 1 ? 1 : volume); // prevent infinite volume
                    //Log.d("Audio Player", "Volume: " + volume + "@(" + sound.getValue().getX() + ", " + sound.getValue().getY() + ")");
                    if (volume > 0) {
                        //Log.d("Audio Player", "Setting Volume (" + sound.getKey() + "): " + volume);
                        sp.setVolume(sound.getKey(), (float) (volume * left), (float) (volume * right));
                    }
                }
            }
            //prevent oversampling
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
