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
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.AttendanceClassRecordRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.CharacterClassRecordRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassCharacterFragement extends Fragment {
    FirebaseFirestore db;
    ClassRecordActBotNav act;
    ArrayList<StudentClassObjectModel> studentList = new ArrayList<>();
    ArrayList<StudentClassObjectModel> filteredList = new ArrayList<>();
    CharacterClassRecordRecyclerViewAdapter studentListTeacherRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    EditText search;
    BottomSheetBehavior bottomSheetBehavior;

    TextView classRecordCategoryName;

    public ClassCharacterFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClassRecordActBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_record_character_attendance, container, false);
        classRecordCategoryName = (TextView) view.findViewById(R.id.classRecordCategoryName);
        classRecordCategoryName.setText("Character ("+(act.getClassRecordVersion().getCharacter()*100)+"%)");
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        studentListTeacherRecyclerViewAdapter = new CharacterClassRecordRecyclerViewAdapter(getActivity(),filteredList,act.getTerm());
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentListRecyclerView.setAdapter(studentListTeacherRecyclerViewAdapter);

        getStudents();
        search = (EditText) view.findViewById(R.id.search);
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
                .whereEqualTo("status","approved")
                .whereEqualTo("classCode",act.getClassKey())
                .orderBy("lName")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    studentList.clear();
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                        studentList.add(documentSnapshot.toObject(StudentClassObjectModel.class));
                    }
                    filter("");
                    studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
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
            studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
        }else {
            filteredList.clear();
            for (StudentClassObjectModel studentClassObjectModel:studentList){
                filteredList.add(studentClassObjectModel);
            }
            studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
