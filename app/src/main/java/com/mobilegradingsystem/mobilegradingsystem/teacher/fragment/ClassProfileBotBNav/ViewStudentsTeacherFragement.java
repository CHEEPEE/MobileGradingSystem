package com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student.ClassStudentRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ViewStudentsTeacherFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileTeacherBotNav act;
    ArrayList<StudentClassObjectModel> studentList = new ArrayList<>();
    StudentListTeacherRecyclerViewAdapter studentListTeacherRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    String studentListFromRegistrar;


    public ViewStudentsTeacherFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileTeacherBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_view_students_teacher, container, false);

        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentListRecyvlerView);
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentListTeacherRecyclerViewAdapter = new StudentListTeacherRecyclerViewAdapter(getActivity(),studentList);
        studentListRecyclerView.setAdapter(studentListTeacherRecyclerViewAdapter);
        getStudentList();
        view.findViewById(R.id.viewStudents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStudentsFromRegistrar();
            }
        });
        return view;
    }
    void getStudentList(){
        db.collection("studentClasses").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentClassObjectModel studentClassObjectModel = documentSnapshot.toObject(StudentClassObjectModel.class);
                    studentList.add(studentClassObjectModel);
                }
                studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    void getStudentsFromRegistrar(){

        final Dialog dialog = new Dialog(getContext());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_student_from_registrar);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        final TextView studentList = (TextView) dialog.findViewById(R.id.studentList);

        db.collection("subjectStudentList").whereEqualTo("classKey",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentListFromRegistrar = "";
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    studentListFromRegistrar+=documentSnapshot.get("studentName")+"\n";
                }
                studentList.setText(studentListFromRegistrar);
            }
        });
    }

}
