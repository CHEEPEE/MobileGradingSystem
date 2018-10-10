package com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.AttendanceClassRecordRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.GradeStudentListTeacherRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassGradeFragement extends Fragment {
    FirebaseFirestore db;
    ClassRecordActBotNav act;
    ArrayList<StudentClassObjectModel> studentList = new ArrayList<>();
    GradeStudentListTeacherRecyclerViewAdapter studentListTeacherRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    TextView classRecordCategoryName;


    public ClassGradeFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClassRecordActBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_participation, container, false);
        classRecordCategoryName = (TextView) view.findViewById(R.id.type);
        classRecordCategoryName.setText("Final Grade");
        TextView btnAddPar = (TextView) view.findViewById(R.id.btnAddPar);
        btnAddPar.setVisibility(View.INVISIBLE);
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        studentListTeacherRecyclerViewAdapter = new GradeStudentListTeacherRecyclerViewAdapter(getActivity(),studentList);
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentListRecyclerView.setAdapter(studentListTeacherRecyclerViewAdapter);

        getStudents();
        return view;
    }

    void getStudents(){
        db.collection("studentClasses")
                .whereEqualTo("status","approved").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    studentList.add(documentSnapshot.toObject(StudentClassObjectModel.class));
                }
                studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }


}
