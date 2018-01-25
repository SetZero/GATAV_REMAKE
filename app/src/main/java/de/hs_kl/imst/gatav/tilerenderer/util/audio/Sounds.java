package de.hs_kl.imst.gatav.tilerenderer.util.audio;

import de.hs_kl.imst.gatav.tilerenderer.R;

/**
 * A Enum of all sounds which are used. Used to prevent passing of integers and confusion
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
    SEA(R.raw.sea),
    WOOSH(R.raw.woosh),
    SIREN(R.raw.sirene),
    PLAYER_DEATH_LINE(R.raw.zum_abschied),
    PLAYER_DEAHT_BY_ENEMY_LINE(R.raw.freund_oder_feind);

    private int soundResource = -1;

    Sounds(int id) {
        this.soundResource = id;
    }

    public int getSoundResource() {
        return soundResource;
    }
}
