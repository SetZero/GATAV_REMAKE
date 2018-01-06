package de.hs_kl.imst.gatav.tilerenderer.util;

import android.content.Context;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hs_kl.imst.gatav.tilerenderer.drawable.Coin;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Enemies;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Player;
import de.hs_kl.imst.gatav.tilerenderer.drawable.Robotic;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Collidable;
import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;

/**
 * Created by Sebastian on 2018-01-05.
 */

public class GameEntityFactory {
    private Map<String, List<Collidable>> objects;

    public GameEntityFactory(Map<String, List<Collidable>> objects) {
        this.objects = objects;
    }

    public Player generatePlayer(Context context, AudioPlayer audioPlayer) {
        List<Collidable> zone = objects.get(Constants.playerStartObjectGroupString);
        assert zone != null : "Level without Start Position!";
        assert zone.size() == 1 : "Level with multiple Starts";

        Collidable playerStart = zone.get(0);
        if (playerStart instanceof Rectangle) {
            Rect startRect = ((Rectangle) playerStart).getRect();
            Player player = new Player(0, 0, context, audioPlayer);
            Vector2 startPosition = new Vector2(startRect.left, startRect.top - player.getHitbox().getHeight());
            player.setPosition(startPosition);
            player.setStartPosition(startPosition);
            return player;
        }
        return null;
    }

    public List<Enemies> generateEnemies(Context context) {
        List<Collidable> zones = objects.get(Constants.enemyObjectGroupString);
        List<Enemies> enemies = new ArrayList<>();
        for (Collidable zone : zones) {
            if (zone instanceof Rectangle) {
                Rect enemyRect = ((Rectangle) zone).getRect();
                Enemies enemy = new Robotic(0, 0, context);
                enemy.setPosition(new Vector2(enemyRect.left, enemyRect.top - enemy.getHitbox().getHeight()));
                enemies.add(enemy);
            }
        }
        return enemies;
    }

    public List<Coin> generateCoins(Context context) {
        List<Collidable> zones = objects.get(Constants.coinObjectGroupString);
        List<Coin> coins = new ArrayList<>();
        for (Collidable zone : zones) {
            if (zone instanceof Rectangle) {
                Rect coinZoneRect = ((Rectangle) zone).getRect();
                Coin coin = new Coin(0, 0, context);
                coin.setPosition(new Vector2(coinZoneRect.left, coinZoneRect.top - coin.getHitbox().getHeight()));
                coins.add(coin);
                int coinAmount = coinZoneRect.width() / coin.getHitbox().getWidth();
                for (int i = 0; i < coinAmount; i++) {
                    coins.add(new Coin(coinZoneRect.left + (i*coin.getHitbox().getWidth()), coinZoneRect.top - coin.getHitbox().getHeight(), context));
                }

            }
        }
        return coins;
    }
}
