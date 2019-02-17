package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.ClassProfile;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.ParticipationStudentsClassRecordRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassPartListStudentsAct extends AppCompatActivity {
    ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    ArrayList<StudentClassObjectModel> filteredList = new ArrayList<>();
    ParticipationStudentsClassRecordRecyclerViewAdapter studentListTeacherRecyclerViewAdapter;
    ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel;
    TeacherClassObjectModel classProfile;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String classKey;
    Context context;
    RecyclerView studentList;
    String partKey;
    String term;
    TextView title;
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_part_list_students);
        classKey = getIntent().getExtras().getString("key");
        partKey = getIntent().getExtras().getString("partKey");
        term = getIntent().getExtras().getString("term");
        studentList = (RecyclerView) findViewById(R.id.studentList);
        title = (TextView) findViewById(R.id.title);
        db  =FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        context = this;
        studentListTeacherRecyclerViewAdapter  = new ParticipationStudentsClassRecordRecyclerViewAdapter(context,filteredList,partKey,term);
        studentList.setLayoutManager(new LinearLayoutManager(context));
        studentList.setAdapter(studentListTeacherRecyclerViewAdapter);
        getStudents();
        getClassProfile();
        title.setText("Class Participation");
        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }
    void getStudents(){
        db.collection("studentClasses")
                .whereEqualTo("status","approved")
                .whereEqualTo("classCode",classKey)
                .orderBy("lName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentClassObjectModelArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    studentClassObjectModelArrayList.add(documentSnapshot.toObject(StudentClassObjectModel.class));
                }
                filter("");
                studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    void filter(String filter){
        if (!filter.equals("")){
            filteredList.clear();
            for (StudentClassObjectModel studentClassObjectModel:studentClassObjectModelArrayList){
                if (studentClassObjectModel.getfName().contains(filter) || studentClassObjectModel.getlName().contains(filter)){
                    filteredList.add(studentClassObjectModel);
                }
            }
            studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
        }else {
            filteredList.clear();
            for (StudentClassObjectModel studentClassObjectModel:studentClassObjectModelArrayList){
                filteredList.add(studentClassObjectModel);
            }
            studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    void getClassProfile(){
        db.collection("class").document(classKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
             classProfile = documentSnapshot.toObject(TeacherClassObjectModel.class);

            }
        });
    }

}
