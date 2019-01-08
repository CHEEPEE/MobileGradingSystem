package com.mobilegradingsystem.mobilegradingsystem.teacher;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FeedbacksObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.FeedbackRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FeedBackStudentList extends AppCompatActivity {
    String classCode;
    FirebaseFirestore db;
    RecyclerView feedbackList;
    Context context;
    RatingBar averageRatingBar;
    TextView averageRating;

    ArrayList<FeedbacksObjectModel> feedbacksObjectModels = new ArrayList<>();
    FeedbacksObjectModel feedbackObjectModelGlobal = new FeedbacksObjectModel();
    FeedbackRecyclerViewAdapter feedbackRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_student_list);
        db = FirebaseFirestore.getInstance();
        context = FeedBackStudentList.this;
        classCode = getIntent().getExtras().getString("classCode");
        feedbackList = (RecyclerView) findViewById(R.id.recyclerFeedBack);
        averageRating = (TextView) findViewById(R.id.averageRating);
        averageRatingBar = (RatingBar) findViewById(R.id.averageRatingBar);
        getFeedback();
        feedbackRecyclerViewAdapter = new FeedbackRecyclerViewAdapter(context,feedbacksObjectModels);
        feedbackList.setLayoutManager(new LinearLayoutManager(context));
        feedbackList.setAdapter(feedbackRecyclerViewAdapter);
    }

    private void getFeedback(){
        db.collection("feedbacks")
                .whereEqualTo("classCode",classCode).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                feedbacksObjectModels.clear();
                float total = 0;
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    FeedbacksObjectModel feedbacksObjectModel = documentSnapshot.toObject(FeedbacksObjectModel.class);
                    feedbacksObjectModels.add(feedbacksObjectModel);
                    total += feedbacksObjectModel.getRating();
                }

                averageRating.setText((total/queryDocumentSnapshots.getDocuments().size())+"");
                averageRatingBar.setRating((total/queryDocumentSnapshots.getDocuments().size()));

                feedbackRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
