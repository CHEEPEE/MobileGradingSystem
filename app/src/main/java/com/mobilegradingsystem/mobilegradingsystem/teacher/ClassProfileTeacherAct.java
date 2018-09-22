package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;

import javax.annotation.Nullable;

public class ClassProfileTeacherAct extends AppCompatActivity {
    TeacherClassObjectModel teacherClassObjectModel;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String classKey;
    TextView className;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_profile_teacher);
        className = (TextView) findViewById(R.id.className);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        classKey = getIntent().getExtras().getString("classKey");
        db.collection("class").document(classKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                className.setText(teacherClassObjectModel.getName());
            }
        });
    }
}
