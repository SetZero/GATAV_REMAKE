package webview;

import android.content.res.AssetManager;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hs_kl.imst.gatav.tilerenderer.MainActivity;
import de.hs_kl.imst.gatav.tilerenderer.util.Constants;

/**
 * Created by Sebastian on 2017-12-24.
 */

public class WebAppInterface {
    private MainActivity mContext;
    private AssetManager am;

    /**
     * Instantiate the interface and set the context + AssetManager
     */
    public WebAppInterface(MainActivity c, AssetManager am) {
        mContext = c;
        this.am = am;
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
}
