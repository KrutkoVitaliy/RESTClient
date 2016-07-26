package appcorp.mmb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import appcorp.mmb.activities.feeds.GlobalFeed;

public class StartApplication extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_start);
        startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
