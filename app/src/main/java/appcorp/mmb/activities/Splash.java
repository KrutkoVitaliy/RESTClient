package appcorp.mmb.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.TagManager;

import java.util.concurrent.TimeUnit;

import appcorp.mmb.ContainerHolderSingleton;
import appcorp.mmb.GlobalFeed;
import appcorp.mmb.R;

public class Splash extends Activity {

    private static final String CONTAINER_ID = "GTM-T99GV9";
TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashText = (TextView) findViewById(R.id.splashText);
        splashText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        TagManager tagManager = TagManager.getInstance(this);
        tagManager.setVerboseLoggingEnabled(true);

        PendingResult<ContainerHolder> pending = tagManager.loadContainerPreferNonDefault(
                CONTAINER_ID, R.raw.gtm_default_container);

        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("IL", "failure loading container");
                    return;
                }
                ContainerHolderSingleton.setContainerHolder(containerHolder);
                ContainerLoadedCallback.registerCallbacksForContainer(container);
                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
                startMainActivity();
            }
        }, 2, TimeUnit.SECONDS);
    }

    private void startMainActivity() {
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(Splash.this, GlobalFeed.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }.start();
    }

    private static class ContainerLoadedCallback implements ContainerHolder.ContainerAvailableListener {
        @Override
        public void onContainerAvailable(ContainerHolder containerHolder, String containerVersion) {
            // We load each container when it becomes available.
            Container container = containerHolder.getContainer();
            registerCallbacksForContainer(container);
        }

        public static void registerCallbacksForContainer(Container container) {
            // Register two custom function call macros to the container.
            //container.registerFunctionCallMacroCallback("increment", new CustomMacroCallback());
            //container.registerFunctionCallMacroCallback("mod", new CustomMacroCallback());
            // Register a custom function call tag to the container.
            //container.registerFunctionCallTagCallback("custom_tag", new CustomTagCallback());
        }

    }
}
