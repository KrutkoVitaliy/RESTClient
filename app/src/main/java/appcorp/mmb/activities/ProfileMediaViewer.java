package appcorp.mmb.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.webkit.WebView;

import appcorp.mmb.R;

public class ProfileMediaViewer extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_media_viewer);

        webView = (WebView) findViewById(R.id.webViewProfile);
        webView.loadUrl(getIntent().getStringExtra("URL"));
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();
    }
}
