package aviad.ikea;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Aviad on 03/05/2018.
 */

public class WebViewScreen extends AppCompatActivity {

    private ProgressBar progress;
    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_screen);
        initProgressBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }
        initWeb();

    }

    private void initProgressBar() {
        progress =findViewById(R.id.progress_bar_web);
        progress.setMax(100);
        int colorCodeDark = getResources().getColor(R.color.yellow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progress.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        }
    }

    private void initWeb() {
        WebView wv = findViewById(R.id.webView);
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setJavaScriptEnabled(true);
      //  wv.setScrollBarStyle(View.VISIBLE);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.loadUrl(url);
        wv.setVisibility(View.INVISIBLE);
        wv.setWebViewClient(new WebObject(wv, progress));
    }
}
