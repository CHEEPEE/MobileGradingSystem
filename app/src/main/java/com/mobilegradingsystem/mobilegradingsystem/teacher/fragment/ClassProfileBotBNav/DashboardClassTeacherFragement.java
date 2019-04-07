package com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassRecordActBotNav;

import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.FeedBackStudentList;

import javax.annotation.Nullable;

public class DashboardClassTeacherFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileTeacherBotNav act;
    BottomSheetBehavior bottomSheetBehavior;
    ItemListDialogFragment itemListDialogFragment;
    TextView studentNumbers,className,announcementNumber,feedbacks;
    EditText title,desciption;
    Dialog updateClassDialog;
    String loading = "loading...";
    TextView settings,classCode,showQRCode,copyCode;
    TextView delete;
    TeacherClassObjectModel oldClassModel;

    public DashboardClassTeacherFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileTeacherBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_dashboard_class_teacher, container, false);
        studentNumbers = (TextView) view.findViewById(R.id.studentNumbers);
        className = (TextView) view.findViewById(R.id.announcementTitle);
        announcementNumber = (TextView) view.findViewById(R.id.announcementsNumber);
        settings = (TextView) view.findViewById(R.id.settings);
        delete = (TextView) view.findViewById(R.id.delete);
        classCode = (TextView) view.findViewById(R.id.classCode);
        showQRCode = (TextView) view.findViewById(R.id.showQRCode);
        copyCode = (TextView) view.findViewById(R.id.copyClassCode);
        feedbacks = (TextView) view.findViewById(R.id.feedbacks);
        studentNumbers.setText(loading);
        className.setText(loading);
        announcementNumber.setText(loading);

        getClassCredentials();
        getStudentNumbers();
        getAnnouncementsNumber();
        view.findViewById(R.id.midterm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClassRecordActBotNav.class);
                i.putExtra("key",act.getClassKey());
                i.putExtra("term","midterm");
                getActivity().startActivity(i);
            }
        });
        view.findViewById(R.id.finals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ClassRecordActBotNav.class);
                i.putExtra("key",act.getClassKey());
                i.putExtra("term","finals");
                getActivity().startActivity(i);
            }
        });
        feedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), FeedBackStudentList.class);
                i.putExtra("classCode",act.getClassKey());
                getActivity().startActivity(i);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                deleteClass();
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Delete Class/Subject");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteClass();
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
        showQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewQRCode(act.getClassKey());
            }
        });
        classCode.setText(act.getClassKey());
        copyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClibBoard(act.getClassKey());
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatClass();
            }
        });
        return view;
    }


    void deleteClass(){
        db.collection("class").document(act.getClassKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("studentClasses").whereEqualTo("classCode",act.getClassKey()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            StudentClassObjectModel studentClassObjectModel = documentSnapshot.toObject(StudentClassObjectModel.class);
                            db.collection("studentClasses").document(studentClassObjectModel.getKey()).delete();
                        }
                    }
                });
                getActivity().finish();
                Utils.message("Class/Subject Deleted",getContext());
            }
        });
    }

    void getClassCredentials(){
        db.collection("class").document(act.getClassKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TeacherClassObjectModel teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                try {
                    className.setText(teacherClassObjectModel.getName());
                }catch (NullPointerException ex){

                }
            }
        });
    }
    void getStudentNumbers(){
        db.collection("studentClasses").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
               studentNumbers.setText(queryDocumentSnapshots.getDocuments().size()+"");
            }
        });
    }
    void getAnnouncementsNumber(){
            db.collection("announcement").whereEqualTo("classCode",act.getClassKey()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    announcementNumber.setText(queryDocumentSnapshots.getDocuments().size()+"");
                }
            });
    }

    void updatClass(){
        updateClassDialog = new Dialog(getContext());
        updateClassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateClassDialog.setCancelable(true);
        updateClassDialog.setContentView(R.layout.dlg_add_class_subject);
        Window window = updateClassDialog.getWindow();
        updateClassDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final EditText className,schedule,description;
        updateClassDialog.show();
        className = (EditText) updateClassDialog.findViewById(R.id.announcementTitle);
        schedule = (EditText) updateClassDialog.findViewById(R.id.schedule);
        description = (EditText) updateClassDialog.findViewById(R.id.inputName);

        db.collection("class").document(act.getClassKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TeacherClassObjectModel teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                oldClassModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                className.setText(teacherClassObjectModel.getName());
                schedule.setText(teacherClassObjectModel.getSched());
                description.setText(teacherClassObjectModel.getDescription());
            }
        });
        updateClassDialog.findViewById(R.id.saveClass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClass(className.getText().toString(),
                        schedule.getText().toString(),
                        description.getText().toString());
            }
        });
    }

    void saveClass(String name,String schedule,String description){
        TeacherClassObjectModel teacherClassObjectModel =
                new TeacherClassObjectModel(act.getClassKey(), FirebaseAuth.getInstance().getUid(),name,schedule,description,oldClassModel.getSemester(),oldClassModel.getSchoolYear());
        db.collection("class").document(act.getClassKey()).set(teacherClassObjectModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateClassDialog.dismiss();
                    }
                });
    }


    void copyToClibBoard(String code){
        ClipboardManager clipboard = (ClipboardManager) act.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("userId", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(act,"Copy to Clipboard "+code,Toast.LENGTH_SHORT).show();
    }

    void viewQRCode(String code){
        final Dialog dialog = new Dialog(act);
        String api = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=";
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_class_qr_code);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        ImageView qrCode = (ImageView) dialog.findViewById(R.id.qrCode);
        GlideApp.with(act).load(api+code).diskCacheStrategy(DiskCacheStrategy.ALL).into(qrCode);
    }
}
