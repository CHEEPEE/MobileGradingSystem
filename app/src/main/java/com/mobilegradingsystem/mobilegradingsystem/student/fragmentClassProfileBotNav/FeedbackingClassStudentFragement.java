package com.mobilegradingsystem.mobilegradingsystem.student.fragmentClassProfileBotNav;

import android.app.Dialog;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FeedbacksObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.ClssProfileStudentBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.FeedbackRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FeedbackingClassStudentFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileStudentBotNav act;
    TextView addFeedback;
    ArrayList<FeedbacksObjectModel> feedbacksObjectModels = new ArrayList<>();
    RecyclerView feedback;
    FeedbackRecyclerViewAdapter feedbackRecyclerViewAdapter;
    public FeedbackingClassStudentFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileStudentBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_student_feedback, container, false);
        addFeedback = (TextView) view.findViewById(R.id.inputName);
        feedback = (RecyclerView) view.findViewById(R.id.feedbackList);
        addFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFeedback();
            }
        });
        feedbackRecyclerViewAdapter = new FeedbackRecyclerViewAdapter(getContext(),feedbacksObjectModels);
        feedback.setLayoutManager(new LinearLayoutManager(getContext()));
        feedback.setAdapter(feedbackRecyclerViewAdapter);

        getFeedbacks();

        return view;
    }

    void getFeedbacks(){
        db.collection("feedbacks")
                .whereEqualTo("classCode",act.getClassKey())
                .whereEqualTo("studentUserId",FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        feedbacksObjectModels.clear();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            FeedbacksObjectModel feedbacksObjectModel = documentSnapshot.toObject(FeedbacksObjectModel.class);
                            feedbacksObjectModels.add(feedbacksObjectModel);
                            System.out.println(feedbacksObjectModel.getFeedback());
                        }
                        feedbackRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
    }

    void addFeedback(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_add_feedback_subject);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final EditText addFeddback = (EditText) dialog.findViewById(R.id.inputName);
        final TextView saveFeedback = (TextView) dialog.findViewById(R.id.saveFeedback);
        final String key = db.collection("feedbacks").document().getId();

        saveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeedback.setClickable(false);
                if (addFeddback.getText().toString().trim().length() != 0){
                    FeedbacksObjectModel feedbacksObjectModel = new FeedbacksObjectModel(key,
                            addFeddback.getText().toString(),
                            FirebaseAuth.getInstance().getUid(),
                            act.getClassKey()
                    );
                    db.collection("feedbacks")
                            .document(key)
                            .set(feedbacksObjectModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                        }
                    });
                }

            }
        });
        dialog.show();
    }
}
