package appcorp.mmb.activities.feeds;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import appcorp.mmb.R;
import appcorp.mmb.activities.other.InternetNotification;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;

public class SelectCategory extends AppCompatActivity {

    private int toExit = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_feed);

        if (!Intermediates.isConnected(getApplicationContext()))
            startActivity(new Intent(getApplicationContext(), InternetNotification.class));

        FireAnal.sendString("1", "Open", "SelectCategory");

        TextView logoText = (TextView) findViewById(R.id.logoText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        ImageView manicure = (ImageView) findViewById(R.id.manicure);
        ImageView makeup = (ImageView) findViewById(R.id.makeup);
        ImageView hairstyle = (ImageView) findViewById(R.id.hairstyle);

        manicure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ManicureFeed.class));
            }
        });
        makeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MakeupFeed.class));
            }
        });
        hairstyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HairstyleFeed.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        toExit--;
        if(toExit == 0) {
            super.onBackPressed();
        }
        if(toExit == 1) {
            Toast.makeText(getApplicationContext(), R.string.doubleClickToExit, Toast.LENGTH_SHORT).show();
        }
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                toExit = 2;
            }
        }.start();
    }
}