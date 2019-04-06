package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.Login;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.DepartmentObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.SectionObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.TempUserObject;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.YearLevelObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.DepartmentRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.ProgramsRecyclerViewAdapter;

import org.w3c.dom.Text;

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
    final static String departmentKey = "Dd9LNYTPVFpwJlIkv3fe";
    String yearLevelKey;
    String sectionLKey;
    ProgramsRecyclerViewAdapter programsRecyclerViewAdapter;
    EditText fname,mName,lName,studentId,email,mobileNumber;
    TempUserObject tempUserObject;
    CheckBox isShowPassword;
    TextView password;

    TextView saveInfo,selectYearLevel,selectSection;
    StudentProfileProfileObjectModel studentProfileProfileObjectModel;
    boolean isUpdateProfile = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
//        classKey = getIntent().getExtras().getString("classKey");
        email = (EditText) findViewById(R.id.email);
        tempUserObject = new TempUserObject();
        selectDepartment = (TextView) findViewById(R.id.selectDepartment);
        selectProgram = (TextView) findViewById(R.id.selectProgram);
        selectYearLevel = (TextView) findViewById(R.id.selectYearLevel);
        selectSection = (TextView) findViewById(R.id.selectSection);
        isShowPassword = (CheckBox) findViewById(R.id.isShowPassword);
        password = (TextView) findViewById(R.id.password);

        db = FirebaseFirestore.getInstance();

        context  = RegisterStudent.this;
//        isUpdateProfile = getIntent().getExtras().getBoolean("isUpdate");

        fname = (EditText) findViewById(R.id.fName);
        mName = (EditText) findViewById(R.id.mName);
        lName = (EditText) findViewById(R.id.lName);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        studentId = (EditText) findViewById(R.id.announcementTitle);
        saveInfo = (TextView) findViewById(R.id.saveInfo);
//        studentId.setText(getIntent().getExtras().getString("studentId"));
        selectDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDepartmentDialog();
            }
        });
        isShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                password.setTransformationMethod(b? HideReturnsTransformationMethod.getInstance(): PasswordTransformationMethod.getInstance());
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
                getPrograms();
                selectProgram();
            }
        });
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveProfile(departmentKey,programKey,yearLevelKey,sectionLKey);
                if (validate(departmentKey,programKey,yearLevelKey,sectionLKey)){
                   db.collection("studentProfile").whereEqualTo("studentId",getString(studentId)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                           if (queryDocumentSnapshots.getDocuments().size() == 0){
                               isEmailExist();
                           }else {
                               studentId.setError("Already Taken");
                           }
                       }
                   });
                }else {
                    Toast.makeText(context,"Please Fill up Everything",Toast.LENGTH_SHORT).show();
                }

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
//                departmentKey = departmentObjectModel.getKey();
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
        if (a == null || b == null || c == null || d == null || mobileNumber.getText().toString().equals(null) || !isPasswordValid() ){
            isValid = false;
        }
        return isValid;
    }

    boolean isPasswordValid(){
        boolean isValid = true;
        try {
            String pass = password.getText().toString();
            if(pass.length() <= 7){
                isValid = false;
                password.setError("Password too short");
            }
        }catch (NullPointerException ex){
            isValid = false;
        }
        return  isValid;
    }
    String getString(EditText text){
        String value = "";
        try {
           if (text.getText().toString().trim().length() != 0){
               value = text.getText().toString();
           }else {
               text.setError("Required");
           }
        }catch (NullPointerException ex){

        }
        return value;
    }
    void saveProfile(String a,String b,String c,String d){
        String key = db.collection("tempCreateUsers").document().getId();
        if (validate(a,b,c,d)){
            signUpUser();
            final StudentProfileProfileObjectModel studentProfileProfileObjectModel = new StudentProfileProfileObjectModel();
            final UserProfileObjectModel userProfileObjectModel = new UserProfileObjectModel();
            studentProfileProfileObjectModel.setAccountStatus("active");
            studentProfileProfileObjectModel.setStudentId(getString(studentId));
            studentProfileProfileObjectModel.setDepartmentKey(departmentKey);
            studentProfileProfileObjectModel.setProgramKey(programKey);
            studentProfileProfileObjectModel.setfName(getString(fname));
            studentProfileProfileObjectModel.setmNme(getString(mName));
            studentProfileProfileObjectModel.setlName(getString(lName));
            studentProfileProfileObjectModel.setEmail(getString(email));
            studentProfileProfileObjectModel.setYearLevelKey(yearLevelKey);
            studentProfileProfileObjectModel.setSectionKey(sectionLKey);
            studentProfileProfileObjectModel.setPhoneNumber(getString(mobileNumber));

            userProfileObjectModel.setUserType("student");
            userProfileObjectModel.setUserName(getString(fname)+" "+getString(lName));
            userProfileObjectModel.setContactNumber(getString(mobileNumber));
            userProfileObjectModel.setEmail(getString(email));
            userProfileObjectModel.setUserSchoolId(getString(studentId));




            System.out.println("ready to save");
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                studentProfileProfileObjectModel.setUserId(user.getUid());
                                userProfileObjectModel.setUserId(user.getUid());
//                                db.collection()
                                db.collection("users").document(user.getUid()).set(userProfileObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("studentProfile").document(user.getUid()).set(studentProfileProfileObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent i = new Intent(RegisterStudent.this, Login.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        });
                                    }
                                });


                            } else {
                                // If sign in fails, display a message to the user.
                                Utils.message("Registration Failed",RegisterStudent.this);


                            }

                            // ...
                        }
                    });



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


//            tempUserObject.setEmail(email.getText().toString());
//            tempUserObject.setPassword(key);
//            tempUserObject.setUserType("student");
////            tempUserObject.setUserSchoolId(getIntent().getExtras().getString("studentId"));
//            tempUserObject.setfName(fname.getText().toString());
//            tempUserObject.setmName(mName.getText().toString());
//            tempUserObject.setlName(lName.getText().toString());
//            tempUserObject.setDepartmentKey(departmentKey);
//            tempUserObject.setProgramKey(programKey);
//            tempUserObject.setSectionKey(sectionLKey);
//            tempUserObject.setPhoneNumber(mobileNumber.getText().toString());
////            tempUserObject.setStudentId(getIntent().getExtras().getString("studentId"));
//            tempUserObject.setYearLevelKey(yearLevelKey);
//            tempUserObject.setUserName(fname.getText().toString()+" "+mName.getText().toString()+" "+lName.getText().toString());
//            tempUserObject.setAccountStatus("approved");
////            tempUserObject.setClassCode(classKey);
//            Toast.makeText(context,"Triggered",Toast.LENGTH_SHORT).show();
//            db.collection("tempCreateUsers").whereEqualTo("email",email.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                @Override
//                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                    if (queryDocumentSnapshots.getDocuments().size()==0){
//                        db.collection("tempCreateUsers").document(email.getText().toString())
//                                .set(tempUserObject).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                finish();
//                            }
//                        });
//                    }else {
//                        Toast.makeText(context,"Email alerady Exist",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }else{
            Toast.makeText(context,"Please Fill up Everything",Toast.LENGTH_SHORT).show();
        }

    }
    void isEmailExist(){
        if (isValidEmail(email.getText().toString())){
            db.collection("users").whereEqualTo("email",email.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size()==0){
                        saveProfile(departmentKey,programKey,yearLevelKey,sectionLKey);
                    }else {
                        Toast.makeText(context,"Email Already Exist",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            email.setError("Email not Valid");
        }

    }

    void writeStudentProfile() {

    }

    void signUpUser(){


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
    boolean isValidEmail(String email){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
