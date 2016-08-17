package appcorp.mmb;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import appcorp.mmb.activities.SelectCategory;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;

public class StartApplication extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_application);

        initStorage();
        initScreen();
        initFirebase();
        initAdMob();
        timer();
    }

    private void initStorage() {
        Storage.init(getApplicationContext());
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

    private void initAdMob() {
        /*MobileAds.initialize(getApplicationContext(), "ca-app-pub-4151792091524133/8805221291");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
    }

    private void timer() {
        TextView logoText;
        logoText = (TextView) findViewById(R.id.splashText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(getApplicationContext(), SelectCategory.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }.start();
    }
}
