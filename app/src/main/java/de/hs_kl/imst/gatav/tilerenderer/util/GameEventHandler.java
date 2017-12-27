package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.GameStateHandler;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Created by Sebastian on 2017-12-25.
 */

public class GameEventHandler {

    private ArrayList<MovableGraphics> dynamics = new ArrayList<>();
    private Map<String, List<Collidable>> objects;
    private Player player;
    private GameStateHandler gameState = new GameStateHandler();

    public GameEventHandler(Map<String, List<Collidable>> objects) {
        this.objects = objects;
    }

    public void update(GameCamera cam) {
        if (hasReachedFinish(cam)) {
            //TODO: add some Finish Screen
            GameEventExecutioner.finishLevel();
        }

        if(hasReachCheckpoint()) {
            if(gameState.getLastCheckpoint() == null) {
                Log.d("GameEventHandler", "New Checkpoint!");
                gameState.setLastCheckpoint(new Vector2(player.getPosition()));
            } else if(gameState.getLastCheckpoint().getX() < player.getPosition().getX()) {
                Log.d("GameEventHandler", "Even Newer Checkpoint!");
                gameState.setLastCheckpoint(new Vector2(player.getPosition()));
            }
        }

        if(cam.isAttachedToObject() && isOutOfBounds(cam)) {
            //TODO: Add some Death Screen
            Log.d("GameEventHandler", "U diededed!");
            if(gameState.getLastCheckpoint() != null) {
                Log.d("GameEventHandler", "Reset!");
                player.setPosition(gameState.getLastCheckpoint());
            }
        }
    }

    private boolean hasReachedFinish(GameCamera cam) {
        if (player == null) return false;

        Collidable finish = objects.get("Ziel").get(0);
        if (finish instanceof Rectangle) {
            Rect finishRect = ((Rectangle) finish).getRect();
            if (cam.isRectInView(finishRect)) {
                if (finishRect.intersect(player.getHitbox().getRect())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean hasReachCheckpoint() {
        List<Collidable> checkpoints = objects.get("Checkpoints");
        for(Collidable checkpoint: checkpoints) {
            if (checkpoint instanceof Rectangle) {
                Rect checkpointRect = ((Rectangle) checkpoint).getRect();
                if (checkpointRect.intersect(player.getHitbox().getRect())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOutOfBounds(GameCamera cam) {
        //Player is in Center = not out of bounds
        if (cam.isRectInView(player.getHitbox().getRect())) {
            return false;
        }
        return true;
    }

    public void addDynamicObject(MovableGraphics dynamic) {
        dynamics.add(dynamic);
        if (dynamic instanceof Player) {
            this.player = (Player) dynamic;
        }
    }
}
