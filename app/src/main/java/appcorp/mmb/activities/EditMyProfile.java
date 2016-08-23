package appcorp.mmb.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import appcorp.mmb.R;

public class EditMyProfile extends Activity {

    private WebView webView;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_profile);
        id = new Integer(getIntent().getStringExtra("ID"));

        webView = (WebView) findViewById(R.id.uploadPhoto);
        webView.loadUrl("http://195.88.209.17/app/actions/editform.php?id="+id);
    }
}