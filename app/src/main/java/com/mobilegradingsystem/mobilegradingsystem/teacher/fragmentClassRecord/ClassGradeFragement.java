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
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FinalTermGradeObjectModel;
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
        classRecordCategoryName.setText((act.getTerm().equals("midterm")?"Midterm":"Tentative Final")+" Grade");
        TextView btnAddPar = (TextView) view.findViewById(R.id.btnAddPar);
        btnAddPar.setVisibility(View.INVISIBLE);
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        studentListTeacherRecyclerViewAdapter = new GradeStudentListTeacherRecyclerViewAdapter(getActivity(),studentList,act.getTerm());
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentListRecyclerView.setAdapter(studentListTeacherRecyclerViewAdapter);

        getStudents();
        return view;
    }

    void getStudents(){
        db.collection("studentClasses")
                .whereEqualTo("status","approved")
                .whereEqualTo("classCode",act.getClassKey())
                .orderBy("lName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void setStudenFinalGrade(final StudentClassObjectModel studentClassObjectModel, final TextView grade){
        FirebaseFirestore.getInstance().collection("termGrade").document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+"midterm").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final FinalTermGradeObjectModel finalTermGradeObjectModelMidterm = documentSnapshot.toObject(FinalTermGradeObjectModel.class);
                FirebaseFirestore.getInstance().collection("termGrade").document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+"finals").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        try {
                            FinalTermGradeObjectModel finalTermGradeObjectModelFinals = documentSnapshot.toObject(FinalTermGradeObjectModel.class);
                            Double finalGrade =  (finalTermGradeObjectModelMidterm.getGrade()+(2*finalTermGradeObjectModelFinals.getGrade()))/3;
                            grade.setText(finalGrade+"");
                        }catch (NullPointerException ex){
                            grade.setText("No Grade(Incomplete scores)");
                        }
                    }
                });
            }
        });
    }

}
