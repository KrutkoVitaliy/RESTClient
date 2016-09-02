package appcorp.mmb.activities.feeds;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import appcorp.mmb.R;
import appcorp.mmb.activities.other.InternetNotification;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;

public class SelectCategory extends AppCompatActivity {

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
                startActivity(new Intent(getApplicationContext(), ManicureFeed.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        makeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MakeupFeed.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        hairstyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HairstyleFeed.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

    }
}