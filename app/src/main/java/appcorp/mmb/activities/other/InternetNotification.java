package appcorp.mmb.activities.other;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import appcorp.mmb.activities.feeds.SelectCategory;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.R;
import appcorp.mmb.classes.Storage;

public class InternetNotification extends AppCompatActivity {

    TextView checkLogoText;
    Button reconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_notification);

        Storage.init(getApplicationContext());
        initLocalization(Intermediates.getInstance().convertToString(getApplicationContext(), R.string.translation));
        initScreen();
        initFirebase();

        FireAnal.sendString("1", "Open", "InternetNotification");

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
