package aviad.ikea;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Aviad on 01/05/2018.
 */

public class SplashScreen extends AppCompatActivity {

    private  final int MY_PERMISSION_REQUEST_CAMERA = 0;
    private final int GENERAL_DURATION = 600;
    private RelativeLayout mainLayout;

    private ImageView logo,tools;
    private Runnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mainLayout = findViewById(R.id.main_splash);
        //  initAndAnimateViews();
        runnable = new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        };
        handler = new Handler();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initAndAnimateViews();
            handler.postDelayed(runnable,GENERAL_DURATION*5);
        } else {
            requestCameraPermission();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            //reload();
            initAndAnimateViews();
            handler.postDelayed(runnable,GENERAL_DURATION*5);
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(SplashScreen.this, new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initAndAnimateViews() {
        tools = findViewById(R.id.img_tools);
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                startMainActivity();
            }
        });
        logo = findViewById(R.id.img_ikea);
        AnimatorSet fadeIn = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(logo,GENERAL_DURATION*4).build();
        AnimatorSet rotate = new AnimUtility.AnimUtilsBuilder().rotateViewAnimation(tools,GENERAL_DURATION*4,720,GENERAL_DURATION).build();
        new AnimUtility.AnimUtilsBuilder().bindSets(fadeIn,rotate).start();
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(),ScannerScreen.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
    private boolean isExpired() {
        GregorianCalendar expDate = new GregorianCalendar( 2018, 4, 11 ); // midnight
        GregorianCalendar now = new GregorianCalendar();
        return !now.after( expDate );
    }
}
