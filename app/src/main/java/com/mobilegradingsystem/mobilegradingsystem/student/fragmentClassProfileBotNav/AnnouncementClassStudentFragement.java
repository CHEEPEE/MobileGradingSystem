package com.mobilegradingsystem.mobilegradingsystem.student.fragmentClassProfileBotNav;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.ClssProfileStudentBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.ItemListDialogFragment;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student.AnnoucementListStudentsRecyclerViewAdapter;
import com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.AnnoucementListTeacherRecyclerViewAdapter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class AnnouncementClassStudentFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileStudentBotNav act;
    BottomSheetBehavior bottomSheetBehavior;
    ItemListDialogFragment itemListDialogFragment;
    TextView studentNumbers,className,announcementNumber;
    EditText title,desciption;
    String loading = "loading...";
    AnnoucementListStudentsRecyclerViewAdapter annoucementListStudentsRecyclerViewAdapter;
    ArrayList<AnnouncementObjectModel> announcementObjectModelArrayList = new ArrayList<>();
    RecyclerView announcementList;

    public AnnouncementClassStudentFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileStudentBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_announcement_student, container, false);
        announcementList = (RecyclerView) view.findViewById(R.id.announcementList);
        getAnnouncements();
        return view;
    }

    void getAnnouncements(){
        annoucementListStudentsRecyclerViewAdapter = new AnnoucementListStudentsRecyclerViewAdapter(getActivity(),announcementObjectModelArrayList);
        announcementList.setLayoutManager(new LinearLayoutManager(getActivity()));
        announcementList.setAdapter(annoucementListStudentsRecyclerViewAdapter);
        db.collection("announcement")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .whereEqualTo("classCode",act.getClassKey())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                announcementObjectModelArrayList.clear();
                          for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                              AnnouncementObjectModel announcementObjectModel = documentSnapshot.toObject(AnnouncementObjectModel.class);
                              announcementObjectModelArrayList.add(announcementObjectModel);
                          }
                annoucementListStudentsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
