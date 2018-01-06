package de.hs_kl.imst.gatav.tilerenderer;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;

public class MainGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        super.onCreate(savedInstanceState);
        GameEventExecutioner executioner = new GameEventExecutioner(this);

        String level = getIntent().getExtras().getString("level");

        GameView gameView = new GameView(this, level, executioner);
        setContentView(gameView);
    }


}
