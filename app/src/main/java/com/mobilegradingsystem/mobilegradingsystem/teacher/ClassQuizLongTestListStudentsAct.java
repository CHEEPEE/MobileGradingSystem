package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.ProjectStudentsClassRecordRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.QuizLongTestStudentsClassRecordRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassQuizLongTestListStudentsAct extends AppCompatActivity {
    ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    QuizLongTestStudentsClassRecordRecyclerViewAdapter studentListTeacherRecyclerViewAdapter;
    ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String classKey;
    Context context;
    RecyclerView studentList;
    String partKey;
    String term;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_part_list_students);
        classKey = getIntent().getExtras().getString("key");
        partKey = getIntent().getExtras().getString("partKey");
        term = getIntent().getExtras().getString("term");
        title = (TextView) findViewById(R.id.title) ;
        studentList = (RecyclerView) findViewById(R.id.studentList);
        db  =FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        context = this;
        studentListTeacherRecyclerViewAdapter  = new QuizLongTestStudentsClassRecordRecyclerViewAdapter(context,studentClassObjectModelArrayList,partKey,term);
        studentList.setLayoutManager(new LinearLayoutManager(context));
        studentList.setAdapter(studentListTeacherRecyclerViewAdapter);
        getStudents();
        title.setText("Quiz and Long Test");

    }
    void getStudents(){
        db.collection("studentClasses")
                .whereEqualTo("status","approved").whereEqualTo("classCode",classKey)
                .orderBy("lName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentClassObjectModelArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    studentClassObjectModelArrayList.add(documentSnapshot.toObject(StudentClassObjectModel.class));
                }
                studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
