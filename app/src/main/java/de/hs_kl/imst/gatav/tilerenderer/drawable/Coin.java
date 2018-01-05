package de.hs_kl.imst.gatav.tilerenderer.drawable;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

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
    }
}
