package appcorp.mmb;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.activities.other.InternetNotification;
import appcorp.mmb.classes.Intermediates;

public class StartApplication extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_application);

        TextView logoText = (TextView) findViewById(R.id.splashText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        if (!Intermediates.isConnected(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), InternetNotification.class));
        } else {
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long l) {
                }
                @Override
                public void onFinish() {
                    startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }.start();
        }
    }
}