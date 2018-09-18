package com.mobilegradingsystem.mobilegradingsystem.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;

import javax.annotation.Nullable;

public class StudentProfile extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView studentName;
    StudentProfileProfileObjectModel studentProfileProfileObjectModel;
    UserProfileObjectModel userProfileObjectModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        studentName = (TextView) findViewById(R.id.StudentName);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection(Utils.studentProfile).document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                studentProfileProfileObjectModel = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                studentName.setText(
                        studentProfileProfileObjectModel.getfName()
                );
            }
        });
        db.collection("users").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
            }
        });




    }
}
