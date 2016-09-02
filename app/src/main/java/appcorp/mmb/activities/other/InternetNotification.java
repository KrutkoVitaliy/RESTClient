package appcorp.mmb.activities.other;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import appcorp.mmb.activities.feeds.SelectCategory;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.R;

public class InternetNotification extends AppCompatActivity {

    TextView checkLogoText;
    Button reconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_notification);

        checkLogoText = (TextView) findViewById(R.id.checkLogoText);
        checkLogoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        reconnect = (Button) findViewById(R.id.reconnect);
        reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Intermediates.getInstance().isConnected(getApplicationContext()))
                    startActivity(new Intent(getApplicationContext(), SelectCategory.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }
}
