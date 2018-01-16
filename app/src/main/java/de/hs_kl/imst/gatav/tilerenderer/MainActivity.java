package de.hs_kl.imst.gatav.tilerenderer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import de.hs_kl.imst.gatav.tilerenderer.util.Constants;
import webview.WebAppInterface;

public class MainActivity extends AppCompatActivity {

    /**
     * Sets up saved settings and displays main menu as webview
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(Constants.prefernceName, 0);
        Constants.backgroundSoundVolume = (settings.getBoolean("enableBGM", true) ? 0.1f : 0.0f);
        Constants.enableEyeCandy = settings.getBoolean("enableEyecandy", false);

        setContentView(R.layout.web_activity);
        AssetManager am = getResources().getAssets();


        WebView mainMenuView = findViewById(R.id.mainMenu);
        mainMenuView.loadUrl("file:///android_asset/webView/index.html");
        WebSettings webSettings = mainMenuView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mainMenuView.setLongClickable(false);
        mainMenuView.setHapticFeedbackEnabled(false);
        mainMenuView.setOnLongClickListener(v -> true);

        mainMenuView.addJavascriptInterface(new WebAppInterface(this, am, getResources().getString(R.string.app_name), settings), "Android");
    }

    /**
     * Loads the level which is passed (starts MainGameActivity)
     * @param level the level to load
     */
    public void loadLevel(String level) {
        Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }


}

