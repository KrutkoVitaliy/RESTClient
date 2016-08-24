package appcorp.mmb.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import appcorp.mmb.R;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class SignIn extends Activity implements View.OnClickListener {

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
            startActivity(new Intent(getApplicationContext(), Authorization.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                                    .putExtra("PhotoURL", "mmbuser.jpg")
                                    .putExtra("E-mail", sEmail));
                            Storage.addString("E-mail", email.getText().toString().trim());
                            Storage.addString("PhotoURL", "mmbuser.jpg");
                        } else {
                            Toast.makeText(getApplicationContext(), "Cannot sign in! Please try again!", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}

