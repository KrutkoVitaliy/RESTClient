package appcorp.mmb.activities.other;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import appcorp.mmb.R;
import appcorp.mmb.classes.FireAnal;

public class FullscreenPreview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_preview);

        FireAnal.sendString("Fullscreen preview", "Open", "Activity");

        final WebView webView;
        webView = (WebView) findViewById(R.id.fullscreenView);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setPadding(0, 0, 0, 0);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String s = getIntent().getStringExtra("screenshot");
        webView.loadData("<html><body>" +
                "<table width='100%' height='100%'>" +
                "<tr>" +
                "<td width='100%' height='100%' valign='middle'><img src='" + s + "' width='100%'/></td>" +
                "</tr>" +
                "</table>" +
                "</body></html>", "text/html", "UTF-8");

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}