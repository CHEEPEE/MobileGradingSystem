package com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;

import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;

import javax.annotation.Nullable;

public class DashboardClassTeacherFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileTeacherBotNav act;
    BottomSheetBehavior bottomSheetBehavior;
    ItemListDialogFragment itemListDialogFragment;
    TextView studentNumbers,className,announcementNumber;
    EditText title,desciption;
    String loading = "loading...";

    public DashboardClassTeacherFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileTeacherBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_dashboard_class_teacher, container, false);
        studentNumbers = (TextView) view.findViewById(R.id.studentNumbers);
        className = (TextView) view.findViewById(R.id.announcementTitle);
        announcementNumber = (TextView) view.findViewById(R.id.announcementsNumber);
        studentNumbers.setText(loading);
        className.setText(loading);
        announcementNumber.setText(loading);

        getClassCredentials();
        getStudentNumbers();
        getAnnouncementsNumber();
        view.findViewById(R.id.midterm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClassRecordActBotNav.class);
                i.putExtra("key",act.getClassKey());
                getActivity().startActivity(i);
            }
        });

        return view;
    }

    void getClassCredentials(){
        db.collection("class").document(act.getClassKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TeacherClassObjectModel teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                className.setText(teacherClassObjectModel.getName());
            }
        });
    }
    void getStudentNumbers(){
        db.collection("studentClasses").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               studentNumbers.setText(queryDocumentSnapshots.getDocuments().size()+"");
            }
        });
    }
    void getAnnouncementsNumber(){
            db.collection("announcement").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    announcementNumber.setText(queryDocumentSnapshots.getDocuments().size()+"");
                }
            });
    }




}
