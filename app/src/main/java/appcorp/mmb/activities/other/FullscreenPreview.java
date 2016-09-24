package appcorp.mmb.activities.other;

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
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;

public class FullscreenPreview extends Activity {

    int i = 0;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_preview);

        Storage.init(getApplicationContext());
        initLocalization(Intermediates.convertToString(getApplicationContext(), R.string.translation));
        initScreen();
        initFirebase();

        FireAnal.sendString("1", "Open", "FullscreenView");

        width = Storage.getInt("Width", 480);
        height = width;

        final WebView webView;
        webView = (WebView) findViewById(R.id.fullscreenView);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setPadding(0,0,0,0);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String s = getIntent().getStringExtra("screenshot");
        webView.loadData("<html><body>" +
                "<table width='100%' height='100%'>" +
                "<tr>" +
                "<td width='100%' height='100%' valign='middle'><img src='"+s+"' width='100%'/></td>" +
                "</tr>" +
                "</table>" +
                "</body></html>" ,"text/html",  "UTF-8");

    }

    private void initScreen() {
        Display display;
        int width, height;
        display = ((WindowManager) getApplicationContext()
                .getSystemService(getApplicationContext().WINDOW_SERVICE))
                .getDefaultDisplay();
        width = display.getWidth();
        height = (int) (width * 0.75F);
        Storage.addInt("Width", width);
        Storage.addInt("Height", height);
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    private void initLocalization(final String translation) {
        if (translation.equals("English")) {
            Storage.addString("Localization", "English");
        }

        if (translation.equals("Russian")) {
            Storage.addString("Localization", "Russian");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}