package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
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

public class RegisterStudent extends AppCompatActivity {
    String classKey;
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
    ProgramsRecyclerViewAdapter programsRecyclerViewAdapter;
    EditText fname,mName,lName,studentId,email;
    TempUserObject tempUserObject;

    TextView saveInfo,selectYearLevel,selectSection;
    StudentProfileProfileObjectModel studentProfileProfileObjectModel;
    boolean isUpdateProfile = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
        classKey = getIntent().getExtras().getString("classKey");
        email = (EditText) findViewById(R.id.email);
        tempUserObject = new TempUserObject();
        selectDepartment = (TextView) findViewById(R.id.selectDepartment);
        selectProgram = (TextView) findViewById(R.id.selectProgram);
        selectYearLevel = (TextView) findViewById(R.id.selectYearLevel);
        selectSection = (TextView) findViewById(R.id.selectSection);
        db = FirebaseFirestore.getInstance();

        context  = RegisterStudent.this;
        isUpdateProfile = getIntent().getExtras().getBoolean("isUpdate");

        fname = (EditText) findViewById(R.id.fName);
        mName = (EditText) findViewById(R.id.mName);
        lName = (EditText) findViewById(R.id.lName);
        studentId = (EditText) findViewById(R.id.announcementTitle);
        saveInfo = (TextView) findViewById(R.id.saveInfo);
        studentId.setText(getIntent().getExtras().getString("studentId"));
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
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile(departmentKey,programKey,yearLevelKey,sectionLKey);
            }
        });

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


    void selectDepartmentDialog(){
        final Dialog dialog = new Dialog(RegisterStudent.this);
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
        final Dialog dialog = new Dialog(RegisterStudent.this);
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
        String key = db.collection("tempCreateUsers").document().getId();
        if (validate(a,b,c,d)){
//            TempUserObject tempUserObject
//                    = new TempUserObject(email.getText().toString(),key
//                    ,departmentKey
//                    ,programKey
//                    ,fname.getText().toString()
//                    ,mName.getText().toString()
//                    ,lName.getText().toString()
//                    ,studentId.getText().toString()
//                    ,"pending"
//                    ,yearLevelKey,sectionLKey
//            );
            tempUserObject.setEmail(email.getText().toString());
            tempUserObject.setPassword(key);
            tempUserObject.setUserType("student");
            tempUserObject.setUserSchoolId(getIntent().getExtras().getString("studentId"));
            tempUserObject.setfName(fname.getText().toString());
            tempUserObject.setmName(mName.getText().toString());
            tempUserObject.setlName(lName.getText().toString());
            tempUserObject.setDepartmentKey(departmentKey);
            tempUserObject.setProgramKey(programKey);
            tempUserObject.setSectionKey(sectionLKey);
            tempUserObject.setStudentId(getIntent().getExtras().getString("studentId"));
            tempUserObject.setYearLevelKey(yearLevelKey);
            tempUserObject.setUserName(fname.getText().toString()+" "+mName.getText().toString()+" "+lName.getText().toString());
            tempUserObject.setAccountStatus("active");
            tempUserObject.setClassCode(classKey);
            Toast.makeText(context,"Triggered",Toast.LENGTH_SHORT).show();
            db.collection("tempCreateUsers").whereEqualTo("email",email.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if (queryDocumentSnapshots.getDocuments().size()==0){
                        db.collection("tempCreateUsers").document(email.getText().toString())
                                .set(tempUserObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        });
                    }else {
                        Toast.makeText(context,"Email alerady Exist",Toast.LENGTH_SHORT).show();
                    }
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
