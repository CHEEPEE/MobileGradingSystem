package com.mobilegradingsystem.mobilegradingsystem.student;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.DepartmentObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.DepartmentRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.ProgramsRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class StudentRegistration extends AppCompatActivity {
    TextView selectDepartment,selectProgram;
    ArrayList<DepartmentObjectModel> departmentObjectModelArrayList = new ArrayList<>();
    ArrayList<ProgramsObjectModel> programsObjectModels =  new ArrayList<>();
    FirebaseFirestore db;
    DepartmentRecyclerViewAdapter departmentRecyclerViewAdapter;
    Context context;
    String programKey;
    String departmentKey;
    FirebaseAuth mAuth;
    ProgramsRecyclerViewAdapter programsRecyclerViewAdapter;
    EditText fname,mName,lName,studentId;
    TextView saveInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        selectDepartment = (TextView) findViewById(R.id.selectDepartment);
        selectProgram = (TextView) findViewById(R.id.selectProgram);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        context  = StudentRegistration.this;


        fname = (EditText) findViewById(R.id.fName);
        mName = (EditText) findViewById(R.id.mName);
        lName = (EditText) findViewById(R.id.lName);
        studentId = (EditText) findViewById(R.id.className);
        saveInfo = (TextView) findViewById(R.id.saveInfo);


        selectDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDepartmentDialog();
            }
        });
        departmentRecyclerViewAdapter = new DepartmentRecyclerViewAdapter(context,departmentObjectModelArrayList);
        db.collection("department").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                departmentObjectModelArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    departmentObjectModelArrayList.add(documentSnapshot.toObject(DepartmentObjectModel.class));
                }
                departmentRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        programsRecyclerViewAdapter = new ProgramsRecyclerViewAdapter(context,programsObjectModels);
        selectProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProgram();
            }
        });
    }

    void selectDepartmentDialog(){
        final Dialog dialog = new Dialog(StudentRegistration.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dlg_select_department);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        RecyclerView departmentList = (RecyclerView) dialog.findViewById(R.id.departmentList);
        departmentList.setLayoutManager(new LinearLayoutManager(context));
        departmentList.setAdapter(departmentRecyclerViewAdapter);
        departmentRecyclerViewAdapter.setOnItemClickListener(new DepartmentRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, DepartmentObjectModel departmentObjectModel) {
                departmentKey = departmentObjectModel.getKey();
                selectDepartment.setText(departmentObjectModel.getDepartment());
                getPrograms();
                dialog.dismiss();
            }
        });
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    void getPrograms(){
        db.collection("program").whereEqualTo("department",departmentKey)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        programsObjectModels.clear();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            programsObjectModels.add(documentSnapshot.toObject(ProgramsObjectModel.class));
                        }
                        programsRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
    }

    void selectProgram(){
        final Dialog dialog = new Dialog(StudentRegistration.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_select_program);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        RecyclerView programList = (RecyclerView) dialog.findViewById(R.id.programList);
        programList.setLayoutManager(new LinearLayoutManager(context));
        programList.setAdapter(programsRecyclerViewAdapter);
        programsRecyclerViewAdapter.setOnItemClickListener(new ProgramsRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, ProgramsObjectModel programsObjectModel) {
                programKey = programsObjectModel.getKey();
                selectProgram.setText(programsObjectModel.getProgram());
                dialog.dismiss();
            }
        });
    }
    void saveProfile(){
        StudentProfileProfileObjectModel studentProfileProfileObjectModel
                = new StudentProfileProfileObjectModel(mAuth.getUid()
                ,departmentKey
                ,programKey
                ,fname.getText().toString()
                ,mName.getText().toString()
                ,lName.getText().toString()
                ,studentId.getText().toString()
                ,"pending"
        );
        db.collection(Utils.studentProfile).document(mAuth.getUid()).set(studentProfileProfileObjectModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent i = new Intent(context,StudentProfile.class);
                        startActivity(i);
                        finish();
                    }
                });
    }
}
