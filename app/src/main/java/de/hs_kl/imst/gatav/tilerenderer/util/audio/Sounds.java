package de.hs_kl.imst.gatav.tilerenderer.util.audio;

import de.hs_kl.imst.gatav.tilerenderer.R;

/**
 * Created by Sebastian on 2018-01-04.
 */

public enum Sounds {
    BASS(R.raw.bass),
    COIN(R.raw.coin),
    JUMP(R.raw.jump),
    ENEMY_DEATH(R.raw.enemy_death),
    ROBOT_LAUGH(R.raw.robot_laugh),
    WATERDROP(R.raw.waterdrop),
    HAHA_FUCK(R.raw.haha_fuck),
    FINISH(R.raw.finish),
    OH_YEAH(R.raw.oh_yeah),
    ROBOT_HIT_BY_PLAYER(R.raw.robot_hit),
    OWL(R.raw.owl),
    SEA(R.raw.sea);

    private int soundResource = -1;

    Sounds(int id) {
        this.soundResource = id;
    }

    public int getSoundResource() {
        return soundResource;
    }
}
