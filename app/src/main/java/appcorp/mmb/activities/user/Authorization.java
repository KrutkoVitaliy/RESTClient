package appcorp.mmb.activities.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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

import appcorp.mmb.R;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Intermediates;
import appcorp.mmb.classes.Storage;
import appcorp.mmb.network.GetRequest;

public class Authorization extends AppCompatActivity implements View.OnClickListener {

    private TextView signIn;
    private EditText email, pass, name, lastname;
    private Button submit;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        Storage.init(getApplicationContext());
        initFirebase();

        FireAnal.sendString("1", "Open", "Authorization");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        TextView logoText = (TextView) findViewById(R.id.logoText);
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

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
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
            startActivity(new Intent(getApplicationContext(), SignIn.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
        }
    }

    private void registerUser() {
        final String sEmail = Intermediates.encodeToURL(email.getText().toString().trim());
        final String sPass = Intermediates.encodeToURL(pass.getText().toString().trim());
        final String sName = Intermediates.encodeToURL(name.getText().toString().trim());
        final String sLastname = Intermediates.encodeToURL(lastname.getText().toString().trim());
        final String firebaseEmail = email.getText().toString().trim();
        final String firebasePass = pass.getText().toString().trim();

        if (TextUtils.isEmpty(sEmail)) {
            Toast.makeText(getApplicationContext(), R.string.enterEmail, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sPass)) {
            Toast.makeText(getApplicationContext(), R.string.enterPassword, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sName)) {
            Toast.makeText(getApplicationContext(), R.string.enterName, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sLastname)) {
            Toast.makeText(getApplicationContext(), R.string.enterLastname, Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage(Intermediates.convertToString(getApplicationContext(), R.string.registering));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(firebaseEmail, firebasePass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            new GetRequest("http://195.88.209.17/app/in/user.php?name=" + sName + "%20" + sLastname + "&photo=mmbuser.jpg&email=" + sEmail.toLowerCase()).execute();
                            startActivity(new Intent(getApplicationContext(), MyProfile.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .putExtra("Name", sName + " " + sLastname)
                                    .putExtra("PhotoURL", "mmbuserunauth.jpg")
                                    .putExtra("E-mail", sEmail));
                            Storage.addString("Name", name.getText().toString().trim() + " " + lastname.getText().toString().trim());
                            Storage.addString("E-mail", email.getText().toString().trim());
                            Storage.addString("PhotoURL", "mmbuserunauth.jpg");
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}