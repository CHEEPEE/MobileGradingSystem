package com.mobilegradingsystem.mobilegradingsystem.student;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.IfAccountIsPending;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.DepartmentObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.SectionObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.TempUserObject;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.YearLevelObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.DepartmentRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.ProgramsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

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
    String yearLevelKey;
    String sectionLKey;
    FirebaseAuth mAuth;
    ProgramsRecyclerViewAdapter programsRecyclerViewAdapter;
    EditText fname,mName,lName,studentId;
    TextView saveInfo,selectYearLevel,selectSection;
    StudentProfileProfileObjectModel studentProfileProfileObjectModel;
    boolean isUpdateProfile = false;
    TempUserObject tempUserObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        selectDepartment = (TextView) findViewById(R.id.selectDepartment);
        selectProgram = (TextView) findViewById(R.id.selectProgram);
        selectYearLevel = (TextView) findViewById(R.id.selectYearLevel);
        selectSection = (TextView) findViewById(R.id.selectSection);
        db = FirebaseFirestore.getInstance();
        tempUserObject = new TempUserObject();
        mAuth = FirebaseAuth.getInstance();
        context  = StudentRegistration.this;
        isUpdateProfile = getIntent().getExtras().getBoolean("isUpdate");

        fname = (EditText) findViewById(R.id.fName);
        mName = (EditText) findViewById(R.id.mName);
        lName = (EditText) findViewById(R.id.lName);
        studentId = (EditText) findViewById(R.id.announcementTitle);
        saveInfo = (TextView) findViewById(R.id.saveInfo);


        selectDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDepartmentDialog();
            }
        });
        selectYearLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (programKey!=null){
                    selectYearLevel();
                }else {
                    Toast.makeText(context,"Please Select your Course before selecting year Level",Toast.LENGTH_SHORT).show();
                }
            }
        });
        selectSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yearLevelKey!=null){
                    selectSection();
                }else {
                    Toast.makeText(context,"Please Select your Year before selecting Section",Toast.LENGTH_SHORT).show();
                }
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

        if (isUpdateProfile){
            getProfileInfo();
        }

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                        categoryChangeListeners();
                    }
                },
                5000
        );
    }

    void getProfileInfo(){
        db.collection("studentProfile").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                studentProfileProfileObjectModel = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                setInfoToFields();
            }
        });
    }
    void setInfoToFields(){
        studentId.setText(studentProfileProfileObjectModel.getStudentId());
        fname.setText(studentProfileProfileObjectModel.getfName());
        mName.setText(studentProfileProfileObjectModel.getmNme());
        lName.setText(studentProfileProfileObjectModel.getlName());
        db.collection("program").document(studentProfileProfileObjectModel.getProgramKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
           try {
               selectProgram.setText(documentSnapshot.get("program").toString());
               programKey = documentSnapshot.get("key").toString();
           }catch (NullPointerException ex){

           }

            }
        });
        db.collection("department").document(studentProfileProfileObjectModel.getDepartmentKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
              try {
                  selectDepartment.setText(documentSnapshot.get("department").toString());
                  departmentKey = documentSnapshot.get("key").toString();
              }catch (NullPointerException ex){

              }
            }
        });
        db.collection("yearlevel").document(studentProfileProfileObjectModel.getYearLevelKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
              try {
                  selectYearLevel.setText(documentSnapshot.get("yearLevel").toString());
                  yearLevelKey = documentSnapshot.get("key").toString();
              }catch (NullPointerException ex){

              }
            }

        });
        db.collection("section").document(studentProfileProfileObjectModel.getSectionKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            try {
                selectSection.setText(documentSnapshot.get("section").toString());
                sectionLKey = documentSnapshot.get("key").toString();
            }catch (NullPointerException ex){

            }
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
                saveProfile(departmentKey,programKey,yearLevelKey,sectionLKey);
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

    void selectYearLevel(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Year Level");
        final ArrayList<YearLevelObjectModel> yearLevelObjectModels = new ArrayList<>();
        final List<String> yearLevel = new ArrayList<>();
        // add a list
        final String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
        db.collection("yearlevel").whereEqualTo("program",programKey).orderBy("yearLevel", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                yearLevelObjectModels.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    yearLevelObjectModels.add(documentSnapshot.toObject(YearLevelObjectModel.class));
                    yearLevel.add(documentSnapshot.get("yearLevel").toString());
                    System.out.println(documentSnapshot.get("yearLevel").toString());
                }
                builder.setItems(yearLevel.toArray(new String[yearLevel.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yearLevelKey = yearLevelObjectModels.get(which).getKey();
                        selectYearLevel.setText(yearLevel.get(which));
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    void selectSection(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Section");
        final ArrayList<SectionObjectModel> sectionObjectModels = new ArrayList<>();
        final List<String> yearLevel = new ArrayList<>();
        // add a list
        db.collection("section").whereEqualTo("yearLevel",yearLevelKey).orderBy("section", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                sectionObjectModels.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    sectionObjectModels.add(documentSnapshot.toObject(SectionObjectModel.class));
                    yearLevel.add(documentSnapshot.get("section").toString());
                    System.out.println(documentSnapshot.get("section").toString());
                }
                builder.setItems(yearLevel.toArray(new String[yearLevel.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sectionLKey = yearLevel.get(which);
                        selectSection.setText(yearLevel.get(which));
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    boolean validate(String a,String b,String c,String d){
        boolean isValid = true;
        if (a == null || b == null || c == null || d == null){
            isValid = false;
        }
        return isValid;
    }
    void saveProfile(String a,String b,String c,String d){
        if (validate(a,b,c,d)){
            StudentProfileProfileObjectModel studentProfileProfileObjectModel
                    = new StudentProfileProfileObjectModel(mAuth.getUid()
                    ,departmentKey
                    ,programKey
                    ,fname.getText().toString()
                    ,mName.getText().toString()
                    ,lName.getText().toString()
                    ,studentId.getText().toString()
                    ,"pending"
                    ,yearLevelKey,sectionLKey,null
            );
            db.collection(Utils.studentProfile).document(mAuth.getUid()).set(studentProfileProfileObjectModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent i = new Intent(context,IfAccountIsPending.class);
                            startActivity(i);
                            finish();
                        }
                    });
        }else{
            Toast.makeText(context,"Please Fill up Everything",Toast.LENGTH_SHORT).show();
        }
    }

    void categoryChangeListeners(){
        selectDepartment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                programKey = null;
                selectProgram.setText("Select Course or Program");
                yearLevelKey = null;
                selectYearLevel.setText("Select Year Level");
                sectionLKey = null;
                selectSection.setText("Please Select Section");
            }
        });
        selectProgram.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                yearLevelKey = null;
                selectYearLevel.setText("Select Year Level");
                sectionLKey = null;
                selectSection.setText("Please Select Section");
            }
        });
        selectYearLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sectionLKey = null;
                selectSection.setText("Please Select Section");
            }
        });
    }
}
