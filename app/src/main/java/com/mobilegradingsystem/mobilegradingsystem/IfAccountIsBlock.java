package com.mobilegradingsystem.mobilegradingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentRegistration;

import javax.annotation.Nullable;

public class IfAccountIsBlock extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    TextView updateProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_if_account_is_block);
        db = FirebaseFirestore.getInstance();
        db
                .collection("studentProfile")
                .document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
             if (documentSnapshot.get("accoutStatus").equals("approved")){
                 Intent i = new Intent(IfAccountIsBlock.this, StudentProfile.class);
                 startActivity(i);
                 finish();
             }
            }
        });
    }
}
