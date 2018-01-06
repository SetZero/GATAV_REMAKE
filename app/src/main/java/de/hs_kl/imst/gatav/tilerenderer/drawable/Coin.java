package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;
import android.util.Pair;

import de.hs_kl.imst.gatav.tilerenderer.util.Vector2;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.AudioPlayer;
import de.hs_kl.imst.gatav.tilerenderer.util.audio.Sounds;

/**
 * Created by keven on 31.12.2017.
 */

public final class Coin extends Collectable {
    public Coin(int x, int y, Context context) {
        super(x, y, 32, 37, context, "collectables/coin/Coin.png");
    }

    @Override
    protected void onCollect() {
        GameContent.player.setScore(GameContent.player.getScore()+ 20);
        setChanged();
        notifyObservers(new Pair<>(Sounds.COIN, new Vector2(Position)));
    }
}
