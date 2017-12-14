package de.hs_kl.imst.gatav.tilerenderer.util;

import de.hs_kl.imst.gatav.tilerenderer.util.Hitboxes.Rectangle;

/**
 * Created by keven on 14.12.2017.
 */

public class DynamicBody {
    Rectangle Hitbox = null;
    public DynamicBody(Rectangle hitbox){
        this.Hitbox = hitbox;
    }
}
