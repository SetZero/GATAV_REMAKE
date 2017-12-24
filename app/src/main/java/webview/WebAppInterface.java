package webview;

import android.content.res.AssetManager;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

import de.hs_kl.imst.gatav.tilerenderer.MainActivity;

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
    private ArrayList<String> getLevel() {
        ArrayList<String> levelList = new ArrayList<String>();  // alle Level-Namen ohne .txt

        try {
            String[] files = am.list("levels");
            for (String s : files) {
                if (!s.endsWith(".tmx")) continue;
                s = s.substring(0, s.lastIndexOf("."));
                levelList.add(s);
            }
            return levelList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all Level to the Webview
     */
    @JavascriptInterface
    public String showLevel() {
        JSONArray jsArray = new JSONArray(getLevel());
        return jsArray.toString();
    }

    /**
     * loads the given level
     */
    @JavascriptInterface
    public void loadLevel(String level) {
        mContext.loadLevel(level);
    }
}
