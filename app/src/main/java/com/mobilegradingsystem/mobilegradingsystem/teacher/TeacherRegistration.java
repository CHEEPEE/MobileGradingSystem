package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherProfileProfileObjectModel;

public class TeacherRegistration extends AppCompatActivity {
    EditText teacherName,teacherid;
    Button save;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);
        context = TeacherRegistration.this;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        teacherName = (EditText) findViewById(R.id.instructorName);
        teacherid = (EditText) findViewById(R.id.instructorID);

        save = (Button) findViewById(R.id.saveInfo);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    void saveProfile(){
        TeacherProfileProfileObjectModel teacherProfileProfileObjectModel =
                new TeacherProfileProfileObjectModel(mAuth.getUid(),
                        teacherid.getText().toString(),
                        teacherName.getText().toString());

        db.collection("teacherProfile")
                .document(mAuth.getUid()).set(teacherProfileProfileObjectModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            Intent i = new Intent(context,TeacherProfile.class);
            startActivity(i);
            finish();
            }
        });
    }
}
