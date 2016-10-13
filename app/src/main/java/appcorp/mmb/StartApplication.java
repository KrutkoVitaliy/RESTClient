package appcorp.mmb;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import appcorp.mmb.activities.feeds.SelectCategory;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vk.sdk.VKSdk;

public class StartApplication extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_application);
        TextView logoText = (TextView) findViewById(R.id.splashText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));
        initFirebase();
        sendOpenApplication();
        initScreen();
        initFacebook();
        timer();
    }

    private void initScreen() {
        Storage.init(getApplicationContext());
        Display display = ((WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
        Storage.addInt("Width", display.getWidth());
        Storage.addInt("Height", (int) (display.getWidth() * 0.75F));
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    private void sendOpenApplication() {
        FireAnal.sendString("0", "OpenApplication", "OpenApplication");
    }

    private void timer() {
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
