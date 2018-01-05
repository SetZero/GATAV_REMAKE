package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent;
import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.GameStateHandler;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.events.Owl;

/**
 * Created by Sebastian on 2017-12-25.
 */

public class GameEventHandler {
    private final Timer timer;
    private final GameEventExecutioner executioner;
    private ArrayList<MovableGraphics> dynamics = new ArrayList<>();
    private Map<String, List<Collidable>> objects;
    private Player player;
    private GameStateHandler gameState = new GameStateHandler();
    private double gracePeriod = 3;
    private double currentGracePeriod = 0;
    private AudioPlayer audioPlayer;

    //Audio Events
    private Owl owlAudioEvent;

    public GameEventHandler(Map<String, List<Collidable>> objects, Timer timer, GameEventExecutioner executioner, AudioPlayer audioPlayer) {
        this.objects = objects;
        this.timer = timer;
        currentGracePeriod = timer.getElapsedTime() + gracePeriod;
        this.executioner = executioner;
        this.audioPlayer = audioPlayer;
        this.owlAudioEvent = new Owl(new Vector2(4000, 600), audioPlayer, timer);
    }

    public ArrayList<MovableGraphics> getDynamics() {
        return dynamics;
    }

    public void update(GameCamera cam) {
        if (hasReachedFinish(cam)) {
            //TODO: add some Finish Screen
            executioner.finishLevel();
        }

        if (hasReachCheckpoint()) {
            if (gameState.getLastCheckpoint() == null) {
                Log.d("GameEventHandler", "New Checkpoint!");
                gameState.setLastCheckpoint(new Vector2(player.getPosition()));
            } else if (gameState.getLastCheckpoint().getX() < player.getPosition().getX()) {
                Log.d("GameEventHandler", "Even Newer Checkpoint!");
                gameState.setLastCheckpoint(new Vector2(player.getPosition()));
            }
        }

        if (cam.isAttachedToObject() && (isOutOfBounds(cam) || isInDeathZone())) {
            if(currentGracePeriod >= timer.getElapsedTime()) return;
            //TODO: Add some Death Screen
            GameContent.getHud().drawPopupMessage("you Died :(", 5);
            currentGracePeriod = timer.getElapsedTime() + gracePeriod;
            if (gameState.getLastCheckpoint() != null) {
                Log.d("GameEventHandler", "Reset!");
                player.setPosition(gameState.getLastCheckpoint());
                player.setActive(true);
                player.setIsAlive(true);
            }
        }
        owlAudioEvent.update();
    }

    private boolean hasReachedFinish(GameCamera cam) {
        if (player == null) return false;

        List<Collidable> finishObjs = objects.get(Constants.finishObjectGroupString);
        return isInTheZone(finishObjs);
    }

    private boolean isInDeathZone() {
        List<Collidable> deathZones = objects.get(Constants.deathzoneObjectGroupString);
        return isInTheZone(deathZones);
    }

    private boolean hasReachCheckpoint() {
        List<Collidable> checkpoints = objects.get(Constants.checkpointsObjectGroupString);
        return isInTheZone(checkpoints);
    }

    private boolean isInTheZone(List<Collidable> zone) {
        if (zone != null) {
            for (Collidable z : zone) {
                if (z instanceof Rectangle) {
                    Rect checkpointRect = ((Rectangle) z).getRect();
                    if (checkpointRect.intersect(player.getHitbox().getRect())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isOutOfBounds(GameCamera cam) {
        //Player is in Center = not out of bounds
        return !cam.isRectInView(player.getHitbox().getRect());
    }

    void addDynamicObject(MovableGraphics dynamic) {
        dynamics.add(dynamic);
        if (dynamic instanceof Player) {
            this.player = (Player) dynamic;
        }
    }
}
