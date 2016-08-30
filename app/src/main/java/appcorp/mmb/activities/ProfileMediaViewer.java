package appcorp.mmb.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import appcorp.mmb.R;

public class ProfileMediaViewer extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_media_viewer);

        webView = (WebView) findViewById(R.id.webViewProfile);
        webView.setWebViewClient(new WebViewClient());
        if (!getIntent().getStringExtra("URL").contains("http://") || !getIntent().getStringExtra("URL").contains("https://"))
            webView.loadUrl("http://" + getIntent().getStringExtra("URL"));
    }
}
