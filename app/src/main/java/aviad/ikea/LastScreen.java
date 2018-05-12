package aviad.ikea;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * Created by Aviad on 03/05/2018.
 */

public class LastScreen extends BaseActivity implements View.OnClickListener{
    private  KonfettiView viewKonfetti;
    private RelativeLayout layoutWellDone ;
    private ImageView imgMirror,imgDrawer,imgReturn;
    private double dir = 20;
    private Handler handler;
    private Runnable runnable;
    private boolean cooledDown = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_screen);
        runnable = new Runnable() {
            @Override
            public void run() {
                cooledDown =true;
            }
        };
        handler = new Handler();
        initViews();
    }

    private void initViews() {
        viewKonfetti = findViewById(R.id.viewKonfetti);
        layoutWellDone = findViewById(R.id.layout_well_done);
        imgDrawer = findViewById(R.id.drawer_img);
        imgMirror = findViewById(R.id.mirror_img);
        imgReturn = findViewById(R.id.return_btn);
        imgReturn.setOnClickListener(this);
        layoutWellDone.setOnClickListener(this);
        imgDrawer.setOnClickListener(this);
        imgMirror.setOnClickListener(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initKonfetti();
            }
        }, 200);
    }


    private void initKonfetti() {
        viewKonfetti.build()
                .addColors(getResources().getColor(R.color.konf_1), getResources().getColor(R.color.konf_2), getResources().getColor(R.color.konf_3))
                .setDirection(120.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2500L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12,5f))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                .stream(240, 5000L);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_well_done:
                if(cooledDown) {
                    cooledDown = false;
                    handler.postDelayed(runnable, 5000);
                    initKonfetti();
                }
                break;
            case R.id.drawer_img:
                startWebScreen("https://www.ikea.co.il/catalogue/Home_organisation/Clothes_and_shoe_organisers/20282107");
                break;
            case R.id.mirror_img:
                startWebScreen("http://www.ikea.co.il/catalogue/Bedroom_furniture/Mirrors_range/00306920");
                break;
            case R.id.return_btn:
                returnVideoScreen();
        }
    }

    private void returnVideoScreen() {
            Intent intent = new Intent(this,VideoScreen.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
    }

    private void startWebScreen(String url) {
        Intent intent = new Intent(this,WebViewScreen.class);
        intent.putExtra("url", url);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
