package de.hs_kl.imst.gatav.tilerenderer.util;

/**
 * Constants with different locations, locations of elements, settings and naming conventions
 * Created by Sebastian on 2017-12-27.
 */

public class Constants {
    //Name of the saved settings variable
    public static final String prefernceName = "ITBusterPrefs";

    //enable better graphics
    public static boolean enableEyeCandy = false;
    //background sound voulme
    public static float backgroundSoundVolume = 0.1f;

    //enable debug build (show hitboxes / fps)
    public static final boolean debugBuild = false;
    //Save locations
    public static final String worldInfoSaveLocation = "levels/worldInfo/";
    public static final String worldInfoFileName = "worlds.json";
    public static final String worldSaveLocation = "levels/worlds";

    //Strings vor Tiled Objectgroups
    public static final String collisionObjectGroupString = "Kollisionen";
    public static final String finishObjectGroupString = "Ziel";
    public static final String checkpointsObjectGroupString = "Checkpoints";
    public static final String deathzoneObjectGroupString = "Tot";
    public static final String playerStartObjectGroupString = "Start";
    public static final String enemyObjectGroupString = "Gegner";
    public static final String coinObjectGroupString = "Coin";
    public static final String waterObjectGroupString = "Water";
    public static final String bossObjectGroupString = "Boss";
    public static final String musicObjectGroupString = "Musik";
}
