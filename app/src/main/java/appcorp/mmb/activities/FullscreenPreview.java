package appcorp.mmb.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import appcorp.mmb.R;

public class FullscreenPreview extends Activity {

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_preview);

        final WebView webView;
        webView = (WebView) findViewById(R.id.fullscreenView);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setPadding(0,0,0,0);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String s = getIntent().getStringExtra("screenshot");
        webView.loadUrl(s);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
    }
}