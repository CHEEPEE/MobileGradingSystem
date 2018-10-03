package com.mobilegradingsystem.mobilegradingsystem.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FeedBackAnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student.FeedBackListStudentsRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class FeedBackAct extends AppCompatActivity {
    String announcementKey;
    FirebaseFirestore db;
    AnnouncementObjectModel announcementObjectModel;
    TextView announcementTitle,timeStamp,description;
    EditText inputFeedBack;
    ImageView sendFeedBack;
    String uid;
    FirebaseAuth mAuth;
    RecyclerView feedBackList;
    ArrayList<FeedBackAnnouncementObjectModel> feedBackAnnouncementObjectModelArrayList = new ArrayList<>();
    FeedBackListStudentsRecyclerViewAdapter feedBackListStudentsRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        announcementTitle = (TextView) findViewById(R.id.announcementTitle);
        description = (TextView) findViewById(R.id.description);
        timeStamp = (TextView) findViewById(R.id.date);
        inputFeedBack = (EditText) findViewById(R.id.inputComment);
        sendFeedBack = (ImageView) findViewById(R.id.comment);
        feedBackList = (RecyclerView) findViewById(R.id.feedBackList);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        announcementKey = getIntent().getExtras().getString("key");
        db = FirebaseFirestore.getInstance();

        db.collection("announcement").document(announcementKey).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                announcementObjectModel = documentSnapshot.toObject(AnnouncementObjectModel.class);
                announcementTitle.setText(announcementObjectModel.getTitle());
                timeStamp.setText(announcementObjectModel.getTimeStamp()+"");
                description.setText(announcementObjectModel.getDescription());
            }
        });
        sendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeedBack();
            }
        });
        getFeedBacks();

    }

    void getFeedBacks(){
        feedBackListStudentsRecyclerViewAdapter = new FeedBackListStudentsRecyclerViewAdapter(this,feedBackAnnouncementObjectModelArrayList);
        feedBackList.setAdapter(feedBackListStudentsRecyclerViewAdapter);
        feedBackList.setLayoutManager(new LinearLayoutManager(this));
        db.collection("announcementFeedBack")
                .whereEqualTo("announceCode",announcementKey)
                .orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    feedBackAnnouncementObjectModelArrayList.clear();
                    for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                        FeedBackAnnouncementObjectModel feedBackAnnouncementObjectModel = documentSnapshot.toObject(FeedBackAnnouncementObjectModel.class);
                        feedBackAnnouncementObjectModelArrayList.add(feedBackAnnouncementObjectModel);
                    }
                    feedBackListStudentsRecyclerViewAdapter.notifyDataSetChanged();
                    feedBackList.scrollToPosition(feedBackAnnouncementObjectModelArrayList.size());
            }
        });
    }

    void saveFeedBack(){
        String feedBack = inputFeedBack.getText().toString();
        String key = db.collection("announcementFeedBack").document().getId();
        FeedBackAnnouncementObjectModel feedBackAnnouncementObjectModel =
                new FeedBackAnnouncementObjectModel(key,feedBack,announcementObjectModel.getClassCode(),uid,announcementObjectModel.getKey());
        db.collection("announcementFeedBack").document(key).set(feedBackAnnouncementObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                inputFeedBack.setText("");
            }
        });
    }
}
