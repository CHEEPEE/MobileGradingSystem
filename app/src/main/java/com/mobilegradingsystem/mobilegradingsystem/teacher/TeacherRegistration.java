package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.IfAccountIsPendingTeacher;
import com.mobilegradingsystem.mobilegradingsystem.Login;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherProfileProfileObjectModel;

import javax.annotation.Nullable;

public class TeacherRegistration extends AppCompatActivity {
    EditText teacherName,teacherid;
    Button save;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Context context;
    boolean update = false;

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
        getProfileDetails();
    }

    void getProfileDetails(){
        db.collection("teacherProfile").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               try {
                   TeacherProfileProfileObjectModel teacherProfileProfileObjectModel = documentSnapshot.toObject(TeacherProfileProfileObjectModel.class);
                   teacherName.setText(teacherProfileProfileObjectModel.getTeacherName());
                   teacherid.setText(teacherProfileProfileObjectModel.getTeacherId());
                   update = true;
               }catch (NullPointerException ex){

               }
            }
        });
    }
    boolean validate(){
        boolean validate = true;
        if (teacherid.getText().toString().trim().length() == 0){
            validate =  false;
        }
        if (teacherName.getText().toString().trim().length() == 0){
            validate = false;
        }
        return validate;
    }
    void saveProfile(){
       if (validate()){
           TeacherProfileProfileObjectModel teacherProfileProfileObjectModel =
                   new TeacherProfileProfileObjectModel(mAuth.getUid(),
                           teacherid.getText().toString(),
                           teacherName.getText().toString(),"pending",mAuth.getCurrentUser().getEmail(),"teacher");

           db.collection("teacherProfile")
                   .document(mAuth.getUid()).set(teacherProfileProfileObjectModel)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Intent i = new Intent(context,IfAccountIsPendingTeacher.class);
                           startActivity(i);
                           finish();
                       }
                   });
       }else {
           Toast.makeText(context,"Please fill up fields",Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    public void onBackPressed() {
        if (!update){
            logOut();
        }
        super.onBackPressed();
    }

    void logOut(){
            GoogleSignInClient mGoogleSignInClient ;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {  //signout Google
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseAuth.getInstance().signOut(); //signout firebase
                            Intent setupIntent = new Intent(getBaseContext(),Login.class/*To ur activity calss*/);
                            Toast.makeText(getBaseContext(), "Logged Out", Toast.LENGTH_LONG).show(); //if u want to show some text
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(setupIntent);
                            finish();
                        }
                    });
        }
}
