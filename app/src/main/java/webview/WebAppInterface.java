package webview;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import de.hs_kl.imst.gatav.tilerenderer.MainActivity;
import de.hs_kl.imst.gatav.tilerenderer.MainGameActivity;

/**
 * Created by Sebastian on 2017-12-24.
 */

public class WebAppInterface {
    private MainActivity mContext;
    private AssetManager am;

    /** Instantiate the interface and set the context */
    public WebAppInterface(MainActivity c, AssetManager am) {
        mContext = c;
        this.am = am;
    }

    private  ArrayList<String> getLevel() {
        ArrayList<String> levelList = new ArrayList<String>();  // alle Level-Namen ohne .txt

        try {
            String[] files = am.list("levels");
            for(String s : files) {
                if(!s.endsWith(".tmx")) continue;
                s = s.substring(0, s.lastIndexOf("."));
                levelList.add(s);
            }
            return levelList;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public String showLevel() {
        JSONArray jsArray = new JSONArray(getLevel());
        Log.d("Webview: ", jsArray.toString());
        return jsArray.toString();
    }

    @JavascriptInterface
    public void loadLevel(String level) {
        mContext.loadLevel(level);
    }
}
