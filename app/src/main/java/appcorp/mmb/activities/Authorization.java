package appcorp.mmb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import appcorp.mmb.R;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class Authorization extends AppCompatActivity implements View.OnClickListener {

    private TextView logoText;
    private TextView signIn;
    private EditText email, pass, name, lastname;
    private Button submit;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        logoText = (TextView) findViewById(R.id.logoText);
        logoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Galada.ttf"));

        signIn = (TextView) findViewById(R.id.signIn);
        email = (EditText) findViewById(R.id.emailField);
        pass = (EditText) findViewById(R.id.passField);
        name = (EditText) findViewById(R.id.nameField);
        lastname = (EditText) findViewById(R.id.lastnameField);
        submit = (Button) findViewById(R.id.submit);

        signIn.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == submit) {
            registerUser();
        }
        if (view == signIn) {
            /*registerUser();*/
        }
    }

    private void registerUser() {
        final String sEmail = Intermediates.encodeToURL(email.getText().toString().trim());
        final String sPass = Intermediates.encodeToURL(pass.getText().toString().trim());
        final String sName = Intermediates.encodeToURL(name.getText().toString().trim());
        final String sLastname = Intermediates.encodeToURL(lastname.getText().toString().trim());

        if (TextUtils.isEmpty(sEmail)) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sPass)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sName)) {
            Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sLastname)) {
            Toast.makeText(getApplicationContext(), "Please enter your lastname", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering...");
        progressDialog.show();



        firebaseAuth.createUserWithEmailAndPassword(sEmail, sPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            new GetRequest("http://195.88.209.17/app/in/user.php?name=" + sName + "%20" + sLastname + "&photo=mmbuser.jpg&email=" + sEmail).execute();
                            startActivity(new Intent(getApplicationContext(), MyProfile.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .putExtra("Name", sName + " " + sLastname)
                                    .putExtra("PhotoURL", "mmbuser.jpg")
                                    .putExtra("E-mail", sEmail));
                            Storage.addString("Name", name.getText().toString().trim() + " " + lastname.getText().toString().trim());
                            Storage.addString("E-mail", email.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}