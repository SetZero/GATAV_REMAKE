package de.hs_kl.imst.gatav.tilerenderer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;

public class MainGameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameEventExecutioner ctrl = new GameEventExecutioner(this);

        String level=getIntent().getExtras().getString("level");

        gameView = new GameView(this, level);
        setContentView(gameView);
    }


}
