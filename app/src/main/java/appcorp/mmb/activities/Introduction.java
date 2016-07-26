package appcorp.mmb.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import appcorp.mmb.R;
import appcorp.mmb.activities.feeds.GlobalFeed;
import appcorp.mmb.loaders.Storage;

public class Introduction extends Activity {

    TextView logoText;
    GoogleApiClient mGoogleApiClient;

    String from;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        logoText = (TextView) findViewById(R.id.logoText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        from = getIntent().getStringExtra("From");

        auth();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(getApplicationContext(), GlobalFeed.class)
        //.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void auth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            String name = acct.getDisplayName();
            String photoURL = acct.getPhotoUrl().toString();
            String id = acct.getId();
            Storage.Init(getApplicationContext());
            Storage.Add("Autentification", "Success");
            Storage.Add("ID", id);
            Storage.Add("E-mail", email);
            Storage.Add("Name", name);
            Storage.Add("PhotoURL", photoURL);

            new SendUserData(email, name, photoURL).execute();

            finish();
            startActivity(new Intent(getApplicationContext(), GlobalFeed.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

            //Toast.makeText(getApplicationContext(), email, Toast.LENGTH_LONG).show();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    public class SendUserData extends AsyncTask<Void, Void, String> {
        HttpURLConnection userAddConnection = null;
        BufferedReader userAddReader = null;
        String name, email, photo;

        String resultJsonSearch = "";

        public SendUserData(String email, String name, String photo) {
            this.email = email;
            this.name = name;
            this.photo = photo;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                name = name.replace(" ", "%20");
                URL feedURL = new URL("http://195.88.209.17/app/in/user.php?name="+name+"&photo="+photo+"&email="+email);
                userAddConnection = (HttpURLConnection) feedURL.openConnection();
                userAddConnection.setRequestMethod("GET");
                userAddConnection.connect();
                InputStream inputStream = userAddConnection.getInputStream();
                userAddReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer profileBuffer = new StringBuffer();
                String profileLine;
                while ((profileLine = userAddReader.readLine()) != null) {
                    profileBuffer.append(profileLine);
                }
                resultJsonSearch = profileBuffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJsonSearch;
        }
    }
}