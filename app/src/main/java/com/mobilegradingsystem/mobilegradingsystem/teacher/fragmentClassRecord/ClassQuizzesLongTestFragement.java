package com.mobilegradingsystem.mobilegradingsystem.teacher.fragmentClassRecord;

import android.app.Dialog;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.StudentListTeacherRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.ProjectClassRecordRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters.QuizLongTestClassRecordRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ClassQuizzesLongTestFragement extends Fragment {
    FirebaseFirestore db;
    ClassRecordActBotNav act;
    ArrayList<ParticipationCategoryGradeObjectModel> participationCategoryGradeObjectModelArrayList = new ArrayList<>();
    QuizLongTestClassRecordRecyclerViewAdapter participationClassRecordRecyclerViewAdapter;
    RecyclerView studentListRecyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    TextView btnAddPar,type;


    public ClassQuizzesLongTestFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClassRecordActBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_participation, container, false);
        type  =(TextView) view.findViewById(R.id.type);
        studentListRecyclerView = (RecyclerView) view.findViewById(R.id.studentlist);
        participationClassRecordRecyclerViewAdapter = new QuizLongTestClassRecordRecyclerViewAdapter(getActivity(), participationCategoryGradeObjectModelArrayList,act.getTerm());
        studentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentListRecyclerView.setAdapter(participationClassRecordRecyclerViewAdapter);
        btnAddPar = (TextView) view.findViewById(R.id.btnAddPar);
        type.setText("Quiz and Long Test ("+(act.getClassRecordVersion().getQuizLongTest()*100)+"%)");
        btnAddPar.setText("Add Quiz/Long Test");
        btnAddPar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addParDialog();
            }
        });
        getParticipationCategory();

        return view;
    }
    private void addParDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_input_participation_score);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final String key = db.collection("quizLongTestCategory").document().getId();
        final EditText maxScore = dialog.findViewById(R.id.inputGrade);
        final TextView done = dialog.findViewById(R.id.done);
//        done.setText("Add Quiz/Long Test");
        dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParticipationCategoryGradeObjectModel categoryGradeObjectModel =
                        new ParticipationCategoryGradeObjectModel(key,act.getClassKey(),Integer.parseInt(maxScore.getText().toString()),act.getTerm());
                db.collection("quizLongTestCategory").document(key).set(categoryGradeObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

    void getParticipationCategory(){
        db.collection("quizLongTestCategory")
                .whereEqualTo("term",act.getTerm())
                .whereEqualTo("classCode",act.getClassKey())
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        participationCategoryGradeObjectModelArrayList.clear();
                       try {
                           for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                               ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel = documentSnapshot.toObject(ParticipationCategoryGradeObjectModel.class);
                               participationCategoryGradeObjectModelArrayList.add(participationCategoryGradeObjectModel);
                           }
                       }catch (NullPointerException e1){

                       }
                        participationClassRecordRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
    }

}
