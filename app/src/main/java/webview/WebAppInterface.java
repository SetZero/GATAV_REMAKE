package webview;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.webkit.JavascriptInterface;

import java.io.IOException;
import java.io.InputStream;

import de.hs_kl.imst.gatav.tilerenderer.MainActivity;
import de.hs_kl.imst.gatav.tilerenderer.util.Constants;

/**
 * Created by Sebastian on 2017-12-24.
 */

public class WebAppInterface {
    private MainActivity mContext;
    private AssetManager am;
    private String gameName;
    private SharedPreferences settings;

    /**
     * Instantiate the interface and set the context + AssetManager
     */
    public WebAppInterface(MainActivity c, AssetManager am, String gameName, SharedPreferences settings) {
        mContext = c;
        this.am = am;
        this.gameName = gameName;
        this.settings = settings;
    }

    /**
     * Gets all level from folder /levels
     */
    private String getJson() {
        String json;
        try {
            InputStream is = am.open(Constants.worldInfoSaveLocation + Constants.worldInfoFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Loads all Level to the Webview
     */
    @JavascriptInterface
    public String showLevel() {
        //JSONArray jsArray = new JSONArray(getLevel());
        return getJson();
    }

    /**
     * loads the given level
     */
    @JavascriptInterface
    public void loadLevel(String level) {
        mContext.loadLevel(level);
    }

    @JavascriptInterface
    public String getGameName() {
        return gameName;
    }

    @JavascriptInterface
    public boolean getBGMEnabledValue() {
        return settings.getBoolean("enableBGM", true);
    }


    @JavascriptInterface
    public void setBGMEnabledValue(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("enableBGM", value);
        editor.apply();
        Constants.backgroundSoundVolume = (value ? 0.1f : 0.0f);
    }

    @JavascriptInterface
    public boolean getEyecandyValue() {
        return settings.getBoolean("enableEyecandy", false);
    }


    @JavascriptInterface
    public void setEyecandyValue(boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("enableEyecandy", value);
        editor.apply();
        Constants.enableEyeCandy = value;
    }
}
