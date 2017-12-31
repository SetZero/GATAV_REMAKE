package de.hs_kl.imst.gatav.tilerenderer.drawable;

/**
 * Created by keven on 31.12.2017.
 */

public final class Coin extends Collectable {
    public Coin(int x, int y)throws Exception{
        super(x,y,32,37,GameContent.context.getAssets().open("collectables/coin/Coin.png"));
    }

    @Override
    protected void onCollect() {
        GameContent.player.setScore(GameContent.player.getScore()+ 20);
    }
}
