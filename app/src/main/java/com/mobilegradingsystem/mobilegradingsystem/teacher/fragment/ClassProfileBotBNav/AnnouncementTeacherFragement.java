package com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.AnnoucementListTeacherRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class AnnouncementTeacherFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileTeacherBotNav act;
    BottomSheetBehavior bottomSheetBehavior;
    ItemListDialogFragment itemListDialogFragment;
    TextView save;
    EditText title,desciption;
    AnnoucementListTeacherRecyclerViewAdapter annoucementListTeacherRecyclerViewAdapter;
    RecyclerView announcementList;
    ArrayList<AnnouncementObjectModel> announcementObjectModelArrayList = new ArrayList<>();

    public AnnouncementTeacherFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileTeacherBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_announcement_teaher, container, false);
        announcementList = (RecyclerView) view.findViewById(R.id.announcementList);
        title = (EditText) view.findViewById(R.id.title);
        desciption = (EditText) view.findViewById(R.id.desciption);
        save = (TextView) view.findViewById(R.id.btnSave);
        bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.nestedScrollView));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                String state = "";

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING: {
//                        state = "DRAGGING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {
//                        state = "SETTLING";
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        state = "EXPANDED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        state = "COLLAPSED";
                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN: {
//                        state = "HIDDEN";
                        break;
                    }
                }

//                Toast.makeText(getActivity(), "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        view.findViewById(R.id.btnAddAnnouncement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        view.findViewById(R.id.closeSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnnouncement();
            }
        });

        getAnnouncements();
        return view;
    }

    void saveAnnouncement(){
        String getTitle =title.getText().toString();
        String des = desciption.getText().toString();
        String key = db.collection("announcement").document().getId();

        AnnouncementObjectModel announcementObjectModel = new AnnouncementObjectModel(key,getTitle,des,act.getClassKey());
        db.collection("announcement").document(key).set(announcementObjectModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    void getAnnouncements(){
        annoucementListTeacherRecyclerViewAdapter = new AnnoucementListTeacherRecyclerViewAdapter(getActivity(),announcementObjectModelArrayList);
        announcementList.setLayoutManager(new LinearLayoutManager(getActivity()));
        announcementList.setAdapter(annoucementListTeacherRecyclerViewAdapter);
        db.collection("announcement").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                announcementObjectModelArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    AnnouncementObjectModel announcementObjectModel = documentSnapshot.toObject(AnnouncementObjectModel.class);
                    announcementObjectModelArrayList.add(announcementObjectModel);
                }
                annoucementListTeacherRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

}
