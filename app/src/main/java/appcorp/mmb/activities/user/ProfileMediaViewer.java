package appcorp.mmb.activities.user;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import appcorp.mmb.R;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;

public class ProfileMediaViewer extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_media_viewer);

        Storage.init(getApplicationContext());
        initLocalization(Intermediates.getInstance().convertToString(getApplicationContext(), R.string.translation));
        initScreen();
        initFirebase();

        FireAnal.sendString("1", "Open", "ProfileMediaViewer");

        webView = (WebView) findViewById(R.id.webViewProfile);
        webView.setWebViewClient(new WebViewClient());
        String url = getIntent().getStringExtra("URL");
        String type = getIntent().getStringExtra("Type");
        if (url.contains("http://"))
            url = url.replace("http://", "");
        if (url.contains("https://"))
            url = url.replace("https://", "");

        if (url.contains("www.google.com"))
            url = url.replace("www.google.com", "");
        if (url.contains("www.facebook.com"))
            url = url.replace("www.facebook.com", "");
        if (url.contains("www.vk.com"))
            url = url.replace("www.vk.com", "");
        if (url.contains("www.instagram.com"))
            url = url.replace("www.instagram.com", "");
        if (url.contains("www.ok.ru"))
            url = url.replace("www.ok.ru", "");

        if (url.contains("google.com"))
            url = url.replace("google.com", "");
        if (url.contains("facebook.com"))
            url = url.replace("facebook.com", "");
        if (url.contains("vk.com"))
            url = url.replace("vk.com", "");
        if (url.contains("instagram.com"))
            url = url.replace("instagram.com", "");
        if (url.startsWith("@"))
            url = url.substring(1, url.length());
        if (url.contains("ok.ru"))
            url = url.replace("ok.ru", "");

        if (type.equals("gplus"))
            url = "https://www.google.com/" + url;
        if (type.equals("fb"))
            url = "https://www.facebook.com/" + url;
        if (type.equals("vk"))
            url = "https://www.vk.com/" + url;
        if (type.equals("instagram"))
            url = "https://www.instagram.com/" + url;
        if (type.equals("ok"))
            url = "https://www.ok.ru.com/" + url;

        webView.loadUrl(url);

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
}