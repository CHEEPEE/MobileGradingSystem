package com.mobilegradingsystem.mobilegradingsystem.student;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.Util;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.Login;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.ProgramsRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student.ClassStudentRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProfile extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView studentName;
    StudentProfileProfileObjectModel studentProfileProfileObjectModel;
    UserProfileObjectModel userProfileObjectModel;
    CircleImageView accountImage;
    Context context;
    ConstraintLayout menuContainer;
    TextView addClass;
    ClassStudentRecyclerViewAdapter classStudentRecyclerViewAdapter;
    RecyclerView classList;
    ArrayList<StudentClassObjectModel> classObjectModelArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        studentName = (TextView) findViewById(R.id.StudentName);
        accountImage = (CircleImageView) findViewById(R.id.account_image);
        addClass  = (TextView) findViewById(R.id.addClass);
        mAuth = FirebaseAuth.getInstance();
        context = StudentProfile.this;
        menuContainer = (ConstraintLayout) findViewById(R.id.menuContainer);
        classList = (RecyclerView) findViewById(R.id.classList);
        db = FirebaseFirestore.getInstance();

        db.collection(Utils.studentProfile).document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                studentProfileProfileObjectModel = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                studentName.setText(
                        studentProfileProfileObjectModel.getfName()+" "+studentProfileProfileObjectModel.getlName()
                );
            }
        });

        db.collection("users").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                GlideApp.with(context).load(userProfileObjectModel.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(accountImage);

            }
        });
        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileSettings();
            }
        });

        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });

        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClassDialog();
            }
        });

        menuContainer.animate()
                .translationY(menuContainer.getWidth())
                .alpha(0.0f)
                .setDuration(300);

        getStudentClasses();
    }

    void profileSettings(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_profile_settings);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        TextView profileSetting,logout;
        profileSetting = (TextView) dialog.findViewById(R.id.profileSettings);
        logout = (TextView) dialog.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void signOut(){
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

    void toggleMenu(){
        if (menuContainer.getVisibility() == View.VISIBLE){

            menuContainer.animate()
                    .translationX(menuContainer.getWidth())
                    .alpha(0.0f)
                    .setDuration(300);

            new java.util.Timer().schedule( new java.util.TimerTask(){
                @Override
                public void run() {
                    menuContainer.setVisibility(View.INVISIBLE);
                }
            },
            300
            );
        }else {
            menuContainer.setVisibility(View.VISIBLE);
            menuContainer.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(300);
        }
    }

    void getStudentClasses(){
        classStudentRecyclerViewAdapter = new ClassStudentRecyclerViewAdapter(context,classObjectModelArrayList);
        classList.setLayoutManager(new LinearLayoutManager(context));
        classList.setAdapter(classStudentRecyclerViewAdapter);

        db.collection("studentClasses").whereEqualTo("studentUserId",mAuth.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                classObjectModelArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentClassObjectModel studentClassObjectModel = documentSnapshot.toObject(StudentClassObjectModel.class);
                    classObjectModelArrayList.add(studentClassObjectModel);
                }
                classStudentRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    void addClassDialog(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_student_add_class);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        final EditText inptClassCode = (EditText) dialog.findViewById(R.id.inptClassCode);
        final TextView saveClass = (TextView) dialog.findViewById(R.id.saveClass);
        saveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inptClassCode.getText().toString().trim().equals("")){
                    inptClassCode.setError("Input Class Code First");
                }else {

                    db.collection("class").document(inptClassCode.getText().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            if (documentSnapshot.getData() != null){
                                db.collection("teacherProfile").document(documentSnapshot.getData().get("userId").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.getData().get("accountStatus").equals("active")){
                                            db.collection("studentClasses").whereEqualTo("classCode",inptClassCode.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if (queryDocumentSnapshots.getDocuments().size() == 0){
                                                        String key = db.collection("studentClasses").document().getId();
                                                        StudentClassObjectModel studentClassObjectModel = new StudentClassObjectModel(key,mAuth.getUid(),inptClassCode.getText().toString(),studentProfileProfileObjectModel.getStudentId());
                                                        db.collection("studentClasses").document(key).set(studentClassObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog.dismiss();
                                                                toggleMenu();
                                                            }
                                                        });
                                                    }else {
                                                        Toast.makeText(context,"Already Added to Your Classes",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }else {
                                            inptClassCode.setError("The Account Owner of the access Code is not Valid");
                                        }
                                    }
                                });

                            }else {
                                inptClassCode.setError("Class Code Doesn't Exist");

                            }
                        }
                    });
                }
            }
        });
    }
}
