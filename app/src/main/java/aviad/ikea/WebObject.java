package aviad.ikea;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Aviad on 03/05/2018.
 */

public class WebObject extends WebViewClient {
    final ProgressBar loadProgress;
    final WebView wv;

    WebObject(WebView webView, ProgressBar progressBar) {
        this.wv = webView;
        this.loadProgress = progressBar;
    }

    public void onPageFinished(WebView view, String url) {
        wv.setVisibility(View.VISIBLE);
        loadProgress.setVisibility(View.INVISIBLE);
        view.clearCache(true);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        wv.loadData("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<center>" + "Check your internet connection" + ".</center>", "text/html", "UTF-8");
    }
}