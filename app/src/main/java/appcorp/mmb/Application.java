package appcorp.mmb;

import android.content.Intent;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.sharing.vkontakte.GetToken;

public class Application extends android.app.Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent(Application.this, GetToken.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();

        //VK initialization
        VKSdk.initialize(this);

        //Facebook initialization
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

        //Screen initialization
        Storage.init(this);
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //noinspection deprecation
        Storage.addInt("Width", display.getWidth());
        //noinspection deprecation
        Storage.addInt("Height", (int) (display.getWidth() * 0.75F));

        //Localization initialization
        TextView getTranslationCheck = new TextView(this);
        getTranslationCheck.setText(R.string.translation);
        if (String.valueOf(getTranslationCheck.getText()).equals("English")) {
            Storage.addString("Localization", "English");
        } else if (String.valueOf(getTranslationCheck.getText()).equals("Russian")) {
            Storage.addString("Localization", "Russian");
        }

        //Firebase initialization and send log event
        FireAnal.setContext(this);
        FireAnal.sendString("Make Me Beauty", "Open", "Application");
        FireAnal.sendString("Initialize VK", "System", "Application");
        FireAnal.sendString("Initialize Facebook", "System", "Application");
        FireAnal.sendString("Initialize Firebase", "System", "Application");
        FireAnal.sendString("Initialize screen", "System", "Application");
        FireAnal.sendString("Initialize localization", "System", "Application");
    }
}
