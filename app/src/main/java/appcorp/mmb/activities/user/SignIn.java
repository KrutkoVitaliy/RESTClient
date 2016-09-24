package appcorp.mmb.activities.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import appcorp.mmb.R;
import appcorp.mmb.activities.user.Authorization;
import appcorp.mmb.activities.user.MyProfile;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private TextView logoText;
    private TextView signUp;
    private EditText email, pass;
    private Button submit;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Storage.init(getApplicationContext());
        initLocalization(Intermediates.getInstance().convertToString(getApplicationContext(), R.string.translation));
        initScreen();
        initFirebase();

        FireAnal.sendString("1", "Open", "SignIn");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        logoText = (TextView) findViewById(R.id.logoText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        signUp = (TextView) findViewById(R.id.signUp);
        email = (EditText) findViewById(R.id.signInEmailField);
        pass = (EditText) findViewById(R.id.signInPassField);
        submit = (Button) findViewById(R.id.signInSubmit);

        signUp.setOnClickListener(this);
        submit.setOnClickListener(this);
        if(getIntent().getStringExtra("tempemail") != null) {
            email.setText(getIntent().getStringExtra("tempemail"));
        }
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            signInUser();
        }
        if (view == signUp) {
            startActivity(new Intent(getApplicationContext(), Authorization.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        }
    }

    private void signInUser() {
        final String sEmail = email.getText().toString().trim();
        final String sPass = pass.getText().toString().trim();

        if (TextUtils.isEmpty(sEmail)) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sPass)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Sign in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(sEmail, sPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), MyProfile.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .putExtra("PhotoURL", "mmbuserunauth.jpg")
                                    .putExtra("E-mail", sEmail.toLowerCase()));
                            Storage.addString("E-mail", email.getText().toString().trim().toLowerCase());
                            Storage.addString("PhotoURL", "mmbuserunauth.jpg");
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.cantAuth, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SignIn.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                    .putExtra("tempemail", sEmail));
                        }
                    }
                });
    }
}

