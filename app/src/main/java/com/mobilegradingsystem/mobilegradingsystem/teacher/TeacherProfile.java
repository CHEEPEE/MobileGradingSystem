package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.Login;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.ClassTeacherRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfile extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextView teacherName,addClass;
    TeacherProfileProfileObjectModel teacherProfileProfileObjectModel;
    UserProfileObjectModel userProfileObjectModel;
    CircleImageView accountImage;
    Context context;
    Dialog addClassDialog;
    RecyclerView classList;
    String semester = null;
    ClassTeacherRecyclerViewAdapter classTeacherRecyclerViewAdapter;
    ArrayList<TeacherClassObjectModel> teacherClassObjectModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);
        teacherName = (TextView) findViewById(R.id.StudentName);
        accountImage = (CircleImageView) findViewById(R.id.account_image);
        classList = (RecyclerView) findViewById(R.id.classList);
        addClass = (TextView) findViewById(R.id.addClass);
        mAuth = FirebaseAuth.getInstance();
        context = TeacherProfile.this;
        db = FirebaseFirestore.getInstance();

        db.collection("teacherProfile").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                teacherProfileProfileObjectModel = documentSnapshot.toObject(TeacherProfileProfileObjectModel.class);
                teacherName.setText(
                        teacherProfileProfileObjectModel.getTeacherName()
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
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClass();
            }
        });
        getClassList();
    }
    void  getClassList(){
        classTeacherRecyclerViewAdapter = new ClassTeacherRecyclerViewAdapter(context,teacherClassObjectModelArrayList);
        classList.setLayoutManager(new LinearLayoutManager(context));
        classList.setAdapter(classTeacherRecyclerViewAdapter);
        db.collection("class").whereEqualTo("userId",mAuth.getUid()).orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                teacherClassObjectModelArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    teacherClassObjectModelArrayList.add(documentSnapshot.toObject(TeacherClassObjectModel.class));
                }
                classTeacherRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
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
        profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,UserUpdatePassword.class);
                startActivity(i);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.profilePicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,UpdateProfilePicture.class);
                startActivity(i);
                dialog.dismiss();
            }
        });
        logout = (TextView) dialog.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    void addClass(){
        addClassDialog = new Dialog(context);
        addClassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addClassDialog.setCancelable(true);
        addClassDialog.setContentView(R.layout.dlg_add_class_subject);

        Window window = addClassDialog.getWindow();
        addClassDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final EditText className,schedule,description;
        addClassDialog.show();
        final CheckBox fSem,sSem;
        fSem = (CheckBox) addClassDialog.findViewById(R.id.firstSem);
        sSem = (CheckBox) addClassDialog.findViewById(R.id.secondSem);
        fSem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSem.setChecked(false);
                semester = "1";
            }
        });
        sSem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fSem.setChecked(false);
                semester = "2";
            }
        });
        className = (EditText) addClassDialog.findViewById(R.id.announcementTitle);
        schedule = (EditText) addClassDialog.findViewById(R.id.schedule);
        description = (EditText) addClassDialog.findViewById(R.id.inputName);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        final String schoolYear = year+" - "+(year+1);
        final TextView saveClass=(TextView) addClassDialog.findViewById(R.id.saveClass);

        addClassDialog.findViewById(R.id.saveClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClass.setClickable(false);
                saveClass(className.getText().toString(),
                        schedule.getText().toString(),
                        description.getText().toString(),semester,schoolYear);
            }
        });
    }

    void saveClass(String name,String schedule,String description,String semester,String schoolYear){
        String key  = db.collection("class").document().getId();

        TeacherClassObjectModel teacherClassObjectModel =
                new TeacherClassObjectModel(key,mAuth.getUid(),name,schedule,description,semester,schoolYear);
        db.collection("class").document(key).set(teacherClassObjectModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addClassDialog.dismiss();
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

}
