package appcorp.mmb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import appcorp.mmb.activities.GlobalFeed;

public class StartApplication extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startActivity(new Intent(getApplicationContext(), GlobalFeed.class));
    }
}
