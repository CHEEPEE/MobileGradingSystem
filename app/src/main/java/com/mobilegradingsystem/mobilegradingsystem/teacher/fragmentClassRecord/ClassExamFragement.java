package com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.CharacterClassRecordRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.ExamStudentsClassRecordRecyclerViewAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassExamFragement extends Fragment {
    FirebaseFirestore db;
    ClassRecordActBotNav act;
    ArrayList<StudentClassObjectModel> studentList = new ArrayList<>();
    ArrayList<StudentClassObjectModel> filteredList = new ArrayList<>();
    ExamStudentsClassRecordRecyclerViewAdapter studentListTeacherRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    TextView bntSetMaxScore;
    TextView type;
    EditText search;
    public ClassExamFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClassRecordActBotNav) getActivity();

        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_exam, container, false);
        type = (TextView) view.findViewById(R.id.type);
        type.setText("Exam ("+(act.getClassRecordVersion().getExam()*100)+"%)");
        bntSetMaxScore = (TextView) view.findViewById(R.id.bntSetMaxScore);
        bntSetMaxScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpadateMaxScore();
            }
        });
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        studentListTeacherRecyclerViewAdapter = new ExamStudentsClassRecordRecyclerViewAdapter(getActivity(),filteredList,act.getClassKey(),act.getTerm());
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
                studentList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    studentList.add(documentSnapshot.toObject(StudentClassObjectModel.class));
                }
                filter("");
                studentListTeacherRecyclerViewAdapter.notifyDataSetChanged();
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
    private void setUpadateMaxScore(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_input_exam_max_score);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final EditText maxScore = dialog.findViewById(R.id.inputGrade);
        db.collection("examTotalScore").document(act.getClassKey()+act.getTerm()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.getData() != null){
                    maxScore.setText(documentSnapshot.get("maxScode").toString());
                }
            }
        });
        final String key = db.collection("examTotalScore").document().getId();

        dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParticipationCategoryGradeObjectModel categoryGradeObjectModel = new ParticipationCategoryGradeObjectModel(key,act.getClassKey(),Integer.parseInt(maxScore.getText().toString()),act.getTerm());
                db.collection("examTotalScore").document(act.getClassKey()+act.getTerm()).set(categoryGradeObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

}
