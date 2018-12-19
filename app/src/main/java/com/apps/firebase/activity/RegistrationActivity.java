package com.apps.firebase.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.apps.firebase.R;
import com.apps.firebase.firebase.SharedPrefManager;
import com.apps.firebase.student.Student;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private static final int RESULT_LOAD_IMAGE = 101;

    private Uri uriProfileImage;
    private String profileImageUrl;
    private String token;
    private ImageView photo;
    private EditText id, name, email, pass, age;
    private Button reg;

    private ProgressDialog progress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        token = SharedPrefManager.getInstance(RegistrationActivity.this).getDeviceToken();
        mAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        //photo = (ImageView) findViewById(R.id.student_photo);
        id = (EditText) findViewById(R.id.student_id);
        name = (EditText) findViewById(R.id.student_name);
        email = (EditText) findViewById(R.id.student_email);
        pass = (EditText) findViewById(R.id.student_password);
        age = (EditText) findViewById(R.id.student_age);
        reg = (Button) findViewById(R.id.registration);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setMessage("Registering...");
                progress.show();

                studentRegistration();
            }
        });

        /*photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });*/

        //loadStudentInformation();
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            //Handle the already login user
            finish();
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        }
    }*/

    private void studentRegistration() {
        final String n = name.getText().toString().trim();
        final String e = email.getText().toString().trim();
        final String p = pass.getText().toString().trim();
        final String a = age.getText().toString().trim();

        if (!TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) { //!Patterns.EMAIL_ADDRESS.matcher(e).matches() && p.length()<6
            mAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progress.cancel();
                        Toast.makeText(RegistrationActivity.this, "Registration successfully", Toast.LENGTH_SHORT).show();
                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Saved UUID into firebase Database from created FirebaseAuth
                        dataStore(id, n, e, p, a);
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            progress.cancel();
                            alertDialog("Already have registered");
                        } else {
                            progress.cancel();
                            alertDialog(""+task.getException().getMessage());
                            Log.d(TAG, ""+task.getException().getMessage());
                        }
                    }
                }
            });
        } else {
            progress.cancel();
            alertDialog("Please insert the proper values in these fields");
        }
    }

    private void dataStore(String id, String n, String e, String p, String a) {

        //====================================================| Photo saved in Store
        /*FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(n)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }*/

        //====================================================| Data saved in Database
        if (!TextUtils.isEmpty(n) && !TextUtils.isEmpty(e) && !TextUtils.isEmpty(p) && !TextUtils.isEmpty(a)) {
            mDatabase = FirebaseDatabase.getInstance().getReference("students");
            //String id = mDatabase.push().getKey();
            Student obj = new Student(id, n, e, p, Integer.parseInt(a));
            mDatabase.child(id).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progress.cancel();
                        Toast.makeText(RegistrationActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    } else {
                        progress.cancel();
                        alertDialog(""+task.getException().getMessage());
                        Log.d(TAG, ""+task.getException().getMessage());
                    }
                }
            });
        } else {
            alertDialog("Empty fields are not allowed.");
        }
    }

    private void loadStudentInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(photo);
            }
            if (user.getDisplayName() != null) {
                name.setText(user.getDisplayName());
            }
        }
    }

    //====================================================| For Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            uriProfileImage = data.getData();
            photo.setImageURI(uriProfileImage);
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference ref = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if (uriProfileImage != null) {
            ref.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistrationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
