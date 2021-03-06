package com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.AttendanceClassRecordRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.annotation.Nullable;

public class ClassAttendanceFragement extends Fragment {
    FirebaseFirestore db;
    ClassRecordActBotNav act;
    ArrayList<StudentClassObjectModel> studentList = new ArrayList<>();
    ArrayList<StudentClassObjectModel> filteredList = new ArrayList<>();
    AttendanceClassRecordRecyclerViewAdapter attendanceClassRecordRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    TextView classRecordCategoryName;
    ArrayList<StudentProfileProfileObjectModel> studentProfileProfileObjectModels = new ArrayList<>();
    EditText search;
    ArrayList<StudentProfileProfileObjectModel> studentProfileProfileObjectModelsFiltered = new ArrayList<>();
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
        classRecordCategoryName.setText("Attendance ("+(act.getClassRecordVersion().getAttendance()*100)+"%)");
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        search = (EditText) view.findViewById(R.id.search);
        attendanceClassRecordRecyclerViewAdapter = new AttendanceClassRecordRecyclerViewAdapter(getActivity(),filteredList,act.getTerm(),studentProfileProfileObjectModels);
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentListRecyclerView.setAdapter(attendanceClassRecordRecyclerViewAdapter);
        getStudents();
        filter("");
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
        return view;
    }

    void getStudents(){
        db.collection("studentClasses")
                .whereEqualTo("classCode",act.getClassKey())
                .whereEqualTo("status","approved")
                .orderBy("lName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                studentList.clear();
                try {
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                        StudentProfileProfileObjectModel studentProfileProfileObjectModel = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                        StudentClassObjectModel studentClassObjectModel = documentSnapshot.toObject(StudentClassObjectModel.class);
                        studentClassObjectModel.setfName(studentProfileProfileObjectModel.getfName());
                        studentClassObjectModel.setlName(studentProfileProfileObjectModel.getlName());
                        studentList.add(studentClassObjectModel);

                    }
                   filter("");



                }catch (NullPointerException ex){

                }

            }
        });
    }

    void filter(String filter){
        if (!filter.equals("")){
            filteredList.clear();
            for (StudentClassObjectModel studentClassObjectModel:studentList){
                if (studentClassObjectModel.getfName().contains(filter) || studentClassObjectModel.getlName().contains(filter)){
                    filteredList.add(studentClassObjectModel);
                }
            }
            attendanceClassRecordRecyclerViewAdapter.notifyDataSetChanged();
        }else {
            filteredList.clear();
            for (StudentClassObjectModel studentClassObjectModel:studentList){
                    filteredList.add(studentClassObjectModel);
            }
            attendanceClassRecordRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


}
