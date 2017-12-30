package de.hs_kl.imst.gatav.tilerenderer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import de.hs_kl.imst.gatav.tilerenderer.util.GameEventExecutioner;

import static de.hs_kl.imst.gatav.tilerenderer.drawable.GameContent.context;

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
