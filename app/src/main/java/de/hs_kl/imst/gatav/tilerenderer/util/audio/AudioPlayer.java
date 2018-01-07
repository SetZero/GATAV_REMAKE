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

    private MediaPlayer player;
    private final int cacheElements = 10;
    // ~83.2 = 100% volume => @ ~4000 Units = 0%
    private final double audioThreshold = 83.2;
    private final Queue<Pair<AudioDataKeeper, Integer>> loadingQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean playing = new AtomicBoolean(true);
    private final Context ctx;
    private SoundPool sp;
    private final LruCache<Integer, Integer> cache = new LruCache<Integer, Integer>(cacheElements) {

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Integer oldElement, Integer newElement) {
            sp.unload(oldElement);
        }

        @Override
        protected int sizeOf(Integer key, Integer value) {
            return 1;
        }
    };
    private final LruCache<Integer, AudioDataKeeper> soundToPlay = new LruCache<Integer, AudioDataKeeper>(cacheElements) {

        @Override
        protected void entryRemoved(boolean evicted, Integer key, AudioDataKeeper oldElement, AudioDataKeeper newElement) {
            sp.stop(key);
        }

        @Override
        protected int sizeOf(Integer key, AudioDataKeeper value) {
            return 1;
        }
    };
    private Player playerCharacter;

    class AudioDataKeeper {
        private final Vector2 position;
        private final double decibel;

        public AudioDataKeeper(Vector2 position, double decibel) {
            this.position = position;
            this.decibel = decibel;
        }

        public Vector2 getPosition() {
            return position;
        }

        public double getDecibel() {
            return decibel;
        }
    }

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
            player.setVolume(0.01f, 0.01f);
            player.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeBGMSpeed(float speed) {
        player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
    }

    public void stopBGM() {
        player.stop();
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
            for (Pair<AudioDataKeeper, Integer> sound : loadingQueue) {
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
        addSound(s, source, audioThreshold);
    }

    public void addSound(Sounds s, Vector2 source, double decibel) {
        //If we can play a song....
        if (playing.get() && playerCharacter != null) {
            double distance = Math.abs(Vector2.distance(source, playerCharacter.getPosition()));
            //don't add out of reach sounds
            if (distance > 4000) return;
            if (cache.get(s.getSoundResource()) != null) {
                //...get it from the cache and play it
                int soundId = cache.get(s.getSoundResource());
                int id = sp.play(soundId, 0.5f, 0.5f, 1, 0, 1);
                //soundToPlay.add(new Pair<>(source, id));
                AudioDataKeeper data = new AudioDataKeeper(source, decibel);
                soundToPlay.put(id, data);
                //Log.d("addSound", "Position: (" + source.getX() + ", " + source.getY() + ")");
            } else {
                //add it to the loading queue if we don't have it
                int soundId = sp.load(ctx, s.getSoundResource(), 1);
                cache.put(s.getSoundResource(), soundId);
                AudioDataKeeper data = new AudioDataKeeper(source, decibel);
                Pair<AudioDataKeeper, Integer> element = new Pair<>(data, soundId);
                loadingQueue.add(element);
            }
        }
    }

    public void cleanup() {
        player.reset();
        player.release();
        player = null;
        playing.set(false);
        sp.release();
        soundToPlay.evictAll();
    }

    public void setPlayerCharacter(Player playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    @Override
    public void run() {
        if(player != null)
            player.start();
        //As long as we can play songs
        while (playing.get()) {
            //If we have a reference to the player (for relative audio position)
            if (playerCharacter == null) continue;
            else {
                //go over all audio resources
                Map<Integer, AudioDataKeeper> snapshot = soundToPlay.snapshot();
                for (Map.Entry<Integer, AudioDataKeeper> sound : snapshot.entrySet()) {
                    double distance = Math.abs(Vector2.distance(sound.getValue().getPosition(), playerCharacter.getPosition()));
                    //stolen from a book ;-)
                    //calculates diffusion of sound @audioThreshold as starting dB, falls of logarithmic
                    //Log.d("AudioPlayer", "dB: " + sound.getValue().getDecibel());
                    double volume = (sound.getValue().getDecibel() - 10d * Math.log10(
                            4 * Math.PI * Math.pow(
                                    distance, 2
                            )
                    )
                    ) / sound.getValue().getDecibel();
                    //playerCharacter.getHitbox().getWidth()
                    //Sigmoid for thresholding of stereo sound position (https://imgur.com/a/Ww50l)
                    double left = 0.75 + Math.tanh(((playerCharacter.getPosition().getX() - sound.getValue().getPosition().getX()) * 0.001) + 2) * 0.25;
                    double right = 0.75 + Math.tanh(-((playerCharacter.getPosition().getX() - sound.getValue().getPosition().getX()) * 0.001 - 2)) * 0.25;
                    //Log.d("AudioPlayer", "Volume: " + volume + "dB: " + sound.getValue().getDecibel() + "Left: " + left + ", Right: " + right + ", Distance: " + distance + ", Sound: (" + sound.getValue().getPosition().getX() + ", " + sound.getValue().getPosition().getY() + ")");
                    volume = (volume > 1 ? 1 : volume); // prevent infinite volume
                    //Log.d("Audio Player", "Volume: " + volume + "@(" + sound.getValue().getX() + ", " + sound.getValue().getY() + ")");
                    if (volume > 0) {
                        //Log.d("Audio Player", "Setting Volume (" + sound.getKey() + "): " + volume);
                        sp.setVolume(sound.getKey(), (float) (volume * left), (float) (volume * right));
                    } else {
                        sp.setVolume(sound.getKey(), 0, 0);
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
