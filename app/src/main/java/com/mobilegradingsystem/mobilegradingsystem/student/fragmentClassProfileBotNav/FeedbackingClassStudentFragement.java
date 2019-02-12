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
import android.widget.RatingBar;
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
    TextView averageRating;
    RatingBar averageRatingBar;
    FeedbacksObjectModel feedbackObjectModelGlobal = new FeedbacksObjectModel();
    boolean hasFeedBack = false;
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
        averageRating = (TextView) view.findViewById(R.id.aveRating);
        averageRatingBar = (RatingBar) view.findViewById(R.id.aveRatingBar);
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
                addFeedback.setClickable(true);
                if (queryDocumentSnapshots.getDocuments().size() == 0){
                    addFeedback.setText("Add Feedback");

                }else {
                    addFeedback.setText("Update Feedback");
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                        feedbackObjectModelGlobal = documentSnapshot.toObject(FeedbacksObjectModel.class);
                    }
                    hasFeedBack = true;
                }
            }
        });

        db.collection("feedbacks")
                .whereEqualTo("classCode",act.getClassKey())
//                .whereEqualTo("studentUserId",FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        feedbacksObjectModels.clear();
                        float total = 0;
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            FeedbacksObjectModel feedbacksObjectModel = documentSnapshot.toObject(FeedbacksObjectModel.class);
                            feedbacksObjectModels.add(feedbacksObjectModel);
                            System.out.println(feedbacksObjectModel.getFeedback());
                            total+=feedbacksObjectModel.getRating();
                        }
                        averageRating.setText(((total/queryDocumentSnapshots.getDocuments().size())+"").equals("NaN")?"0":(total/queryDocumentSnapshots.getDocuments().size())+"");
                        averageRatingBar.setRating((total/queryDocumentSnapshots.getDocuments().size()));

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
        final RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar);
        ratingBar.setRating(0);
        final TextView saveFeedback = (TextView) dialog.findViewById(R.id.saveFeedback);



        if (hasFeedBack){
//            update feedback
              addFeddback.setText(feedbackObjectModelGlobal.getFeedback());
              ratingBar.setRating(feedbackObjectModelGlobal.getRating());
                saveFeedback.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        saveFeedback.setClickable(false);
                        if (addFeddback.getText().toString().trim().length() != 0){
                            FeedbacksObjectModel feedbacksObjectModel = new FeedbacksObjectModel(feedbackObjectModelGlobal.getKey(),
                                    addFeddback.getText().toString(),
                                    FirebaseAuth.getInstance().getUid(),
                                    act.getClassKey(),ratingBar.getRating()
                            );
                            db.collection("feedbacks")
                                    .document(feedbackObjectModelGlobal.getKey())
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
        }else {
//            add Feedback
            final String key = db.collection("feedbacks").document().getId();
            saveFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    saveFeedback.setClickable(false);
                    if (addFeddback.getText().toString().trim().length() != 0){
                        FeedbacksObjectModel feedbacksObjectModel = new FeedbacksObjectModel(key,
                                addFeddback.getText().toString(),
                                FirebaseAuth.getInstance().getUid(),
                                act.getClassKey(),ratingBar.getRating()
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
        }

        dialog.show();
    }
}
