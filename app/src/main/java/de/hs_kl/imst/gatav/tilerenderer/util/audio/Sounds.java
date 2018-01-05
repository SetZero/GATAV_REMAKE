package de.hs_kl.imst.gatav.tilerenderer.util.audio;

import de.hs_kl.imst.gatav.tilerenderer.R;

/**
 * Created by Sebastian on 2018-01-04.
 */

public enum Sounds {
    BASS(R.raw.bass),
    COIN(R.raw.coin),
    OWL(R.raw.owl);

    private int soundResource = -1;

    Sounds(int id) {
        this.soundResource = id;
    }

    public int getSoundResource() {
        return soundResource;
    }
}
