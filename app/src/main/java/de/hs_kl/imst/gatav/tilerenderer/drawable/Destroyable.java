package de.hs_kl.imst.gatav.tilerenderer.drawable;

/**
 * Created by keven on 14.12.2017.
 */


/**
 * Interface that expresses if an item can be destructed
 */
public interface Destroyable {
    void processHit(float hit);
    boolean isDestroyed();
}
