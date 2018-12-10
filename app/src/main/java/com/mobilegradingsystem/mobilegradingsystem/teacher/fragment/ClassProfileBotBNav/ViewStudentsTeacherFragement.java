package com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.RegisterStudent;
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
    TextView addStudent;
    Dialog addStudentToClassDialog;


    public ViewStudentsTeacherFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileTeacherBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_view_students_teacher, container, false);
        addStudent = (TextView) view.findViewById(R.id.addStudent);
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentListRecyvlerView);
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentListTeacherRecyclerViewAdapter = new StudentListTeacherRecyclerViewAdapter(getActivity(),studentList);
        studentListRecyclerView.setAdapter(studentListTeacherRecyclerViewAdapter);
        getStudentList();
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudentToClass();
            }
        });
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

    void checkStudentRegistered(final String userSchoolId){
        db.collection("users").whereEqualTo("userSchoolId",userSchoolId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots.getDocuments().size() == 0){
                    Intent i = new Intent(getActivity(), RegisterStudent.class);
                    i.putExtra("classKey",act.getClassKey());
                    i.putExtra("studentId",userSchoolId);
                    getActivity().startActivity(i);
                    addStudentToClassDialog.dismiss();
                }else if(queryDocumentSnapshots.getDocuments().size()>1){
                    Toast.makeText(getContext(),"Hi Developer, You should Check this one Dup: "+queryDocumentSnapshots.getDocuments().size(),Toast.LENGTH_SHORT).show();
                }else if(queryDocumentSnapshots.getDocuments().size() == 1){

                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){

                       final UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);

                        db.collection("studentClasses").whereEqualTo("studentUserId",userProfileObjectModel.getUserId()).whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots.getDocuments().size() >=1){
                                    Toast.makeText(getContext(),"You cannot add the same student on the same class",Toast.LENGTH_SHORT).show();
                                }else {
                                    String studentClassKey = db.collection("studentClasses").document().getId();
                                    StudentClassObjectModel studentClassObjectModel =
                                            new StudentClassObjectModel(studentClassKey,userProfileObjectModel.getUserId(),act.getClassKey(),userProfileObjectModel.getUserSchoolId(),"approved");
                                    db.collection("studentClasses")
                                            .document(studentClassKey).set(studentClassObjectModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    addStudentToClassDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    void registerStudent(String studentId){
        final Dialog dialog = new Dialog(getContext());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_add_student_to_subject);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.show();
    }


    void addStudentToClass(){
        addStudentToClassDialog = new Dialog(getContext());
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        addStudentToClassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addStudentToClassDialog.setCancelable(true);
        addStudentToClassDialog.setContentView(R.layout.dlg_add_student_to_subject);
        Window window = addStudentToClassDialog.getWindow();

        addStudentToClassDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView saveStudent = (TextView) addStudentToClassDialog.findViewById(R.id.saveStudent);
        final EditText studentId = (EditText) addStudentToClassDialog.findViewById(R.id.studentId);
        saveStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentId.getText().toString().trim()!=""){
                    checkStudentRegistered(studentId.getText().toString());
                }
            }
        });
        addStudentToClassDialog.show();
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
