package de.hs_kl.imst.gatav.tilerenderer.util;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.drawable.MovableGraphics;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Created by Sebastian on 2017-12-25.
 */

public class GameEventHandler {

    private ArrayList<MovableGraphics> dynamics = new ArrayList<>();
    private Map<String, List<Collidable>> objects;
    private Player player;

    public GameEventHandler(Map<String, List<Collidable>> objects) {
        this.objects = objects;
    }

    public void update(GameCamera cam) {
        if (hasReachedFinish(cam)) {
            GameEventExecutioner.finishLevel();
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

    public void addDynamicObject(MovableGraphics dynamic) {
        dynamics.add(dynamic);
        if (dynamic instanceof Player) {
            this.player = (Player) dynamic;
        }
    }
}
