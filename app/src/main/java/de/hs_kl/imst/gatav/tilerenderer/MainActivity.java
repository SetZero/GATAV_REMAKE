package de.hs_kl.imst.gatav.tilerenderer;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.IOException;
import java.util.ArrayList;

import webview.WebAppInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        AssetManager am = getResources().getAssets();


        WebView mainMenuView = findViewById(R.id.mainMenu);
        mainMenuView.loadUrl("file:///android_asset/webView/index.html");
        WebSettings webSettings = mainMenuView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mainMenuView.addJavascriptInterface(new WebAppInterface(this, am), "Android");
    }

    public void loadLevel(String level) {
        Intent intent = new Intent(MainActivity.this, MainGameActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }


}

