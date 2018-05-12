package aviad.ikea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Aviad on 02/05/2018.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),SplashScreen.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
