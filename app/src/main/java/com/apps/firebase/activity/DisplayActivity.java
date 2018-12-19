package com.apps.firebase.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.apps.firebase.R;
import com.apps.firebase.student.Student;
import com.apps.firebase.student.StudentsList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "DisplayActivity";
    private String token;
    private Button out;
    private ListView listView;

    private List<Student> stdList;

    private ProgressDialog progress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fir-f1dcb.firebaseio.com/");
        mDatabase = mRef.child("students");

        listView = (ListView) findViewById(R.id.student_list);
        stdList = new ArrayList<>();

        out = (Button) findViewById(R.id.logout);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(DisplayActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stdList.clear();
                for (DataSnapshot std : dataSnapshot.getChildren()) {
                    Student obj = std.getValue(Student.class);
                    stdList.add(obj);
                }
                StudentsList adapter = new StudentsList(DisplayActivity.this, stdList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
