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
    private boolean finished = false;


    //Audio Events
    private boolean speedUpSound = false;
    private final float speedUpSoundAmount = 1.25f;
    private final float speedUpSoundTime = 0.8f;
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
        if (hasReachedFinish() && !finished) {
            //TODO: add some Finish Screen
            //TODO: If Player is not active don't let him move...
            finished = true;
            timer.snapshotTime();
            player.setScore(player.getScore() + (int)(timer.getTotalLevelTime() - timer.getSnapshotTime()));
            GameContent.getHud().drawPopupMessage("Finished!", 5);
            player.setActive(false);
        }

        if(finished && timer.getElapsedTime() > timer.getSnapshotTime() + 3) {
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
        //TODO: Add World Reset!
        if (cam.isAttachedToObject() && (isOutOfBounds(cam) || isInDeathZone())) {
            if(currentGracePeriod >= timer.getElapsedTime()) return;
            //TODO: Add some Death Screen
            GameContent.getHud().drawPopupMessage("you Died :(", 5);
            currentGracePeriod = timer.getElapsedTime() + gracePeriod;
            if (gameState.getLastCheckpoint() != null) {
                player.setPosition(gameState.getLastCheckpoint());
                player.setActive(true);
                player.setIsAlive(true);
            } else {
                player.resetPlayer();
            }
        }

        if(!finished && timer.getElapsedTime() > timer.getTotalLevelTime() * speedUpSoundTime) {
            if(!speedUpSound) {
                audioPlayer.changeBGMSpeed(speedUpSoundAmount);
                speedUpSound = true;
            }
        }

        if(!finished && isOutOfTime()) {
            GameContent.getHud().drawPopupMessage("OUTATIME", 5);
            player.resetPlayer();
        }
        owlAudioEvent.update();
    }

    private boolean hasReachedFinish() {
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

    private boolean isOutOfTime() {
        return timer.getElapsedTime() > timer.getTotalLevelTime();
    }

    void addDynamicObject(MovableGraphics dynamic) {
        dynamics.add(dynamic);
        if (dynamic instanceof Player) {
            this.player = (Player) dynamic;
        }
    }

}
