package com.apps.firebase.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.firebase.R;
import com.apps.firebase.firebase.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private String token;
    private EditText email, pass;
    private TextView reg;
    private Button log;
    private ProgressDialog progress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        token = SharedPrefManager.getInstance(LoginActivity.this).getDeviceToken();
        mAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);

        email = (EditText) findViewById(R.id.student_email);
        pass = (EditText) findViewById(R.id.student_password);
        log = (Button) findViewById(R.id.login);
        reg = (TextView) findViewById(R.id.registration);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setMessage("Login...");
                progress.show();

                studentLogin();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, DisplayActivity.class));
        }
    }*/

    private void studentLogin() {
        final String e = email.getText().toString().trim();
        final String p = pass.getText().toString().trim();
        if (!TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) { //!Patterns.EMAIL_ADDRESS.matcher(e).matches() && p.length()<6
            mAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progress.cancel();
                        Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(LoginActivity.this, DisplayActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                    } else {
                        progress.cancel();
                        alertDialog(""+task.getException().getMessage());
                        Log.d(TAG, ""+task.getException().getMessage());
                    }
                }
            });

            /*mAuth.signInWithCustomToken(token)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progress.cancel();
                                Toast.makeText(LoginActivity.this, "Token Login successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                progress.cancel();
                                alertDialog(""+task.getException().getMessage());
                                Log.d(TAG, ""+task.getException().getMessage());
                            }
                        }
                    });*/
        } else {
            progress.cancel();
            alertDialog("Empty fields are not allowed.");
        }
    }

    //====================================================| Alert Message
    public void alertDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.alert_title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
