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
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.AttendanceClassRecordRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassAttendanceFragement extends Fragment {
    FirebaseFirestore db;
    ClassRecordActBotNav act;
    ArrayList<StudentClassObjectModel> studentList = new ArrayList<>();
    AttendanceClassRecordRecyclerViewAdapter attendanceClassRecordRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    TextView classRecordCategoryName;

    public ClassAttendanceFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClassRecordActBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_record_character_attendance, container, false);
        classRecordCategoryName = (TextView) view.findViewById(R.id.classRecordCategoryName);
        classRecordCategoryName.setText("Attendance");
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        attendanceClassRecordRecyclerViewAdapter = new AttendanceClassRecordRecyclerViewAdapter(getActivity(),studentList);
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentListRecyclerView.setAdapter(attendanceClassRecordRecyclerViewAdapter);

        getStudents();
        return view;
    }

    void getStudents(){
        db.collection("studentClasses")
                .whereEqualTo("classCode",act.getClassKey())
                .whereEqualTo("status","approved")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    studentList.add(documentSnapshot.toObject(StudentClassObjectModel.class));
                }
                attendanceClassRecordRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
