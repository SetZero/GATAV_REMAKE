package de.hs_kl.imst.gatav.tilerenderer.util.audio.events;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.hs_kl.imst.gatav.tilerenderer.util.Timer;
import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

/**
 * Created by Sebastian on 2018-01-11.
 */

public class EventContainer {
    private Class<?> event;
    private RepeatingEvent fullEvent;
    private Vector2 position;
    private boolean started = false;

    public EventContainer(Class<?> event, Vector2 position) {
        this.event = event;
        this.position = position;
    }

    public void start(Timer timer, AudioPlayer audioPlayer) {
        try {
            Constructor<?> constructor = event.getConstructor(Vector2.class, AudioPlayer.class, Timer.class);
            Object instance = constructor.newInstance(position, audioPlayer, timer);
            fullEvent = (RepeatingEvent) instance;
            started = true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if(started)
            fullEvent.update();
    }
}
