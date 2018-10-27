package com.mobilegradingsystem.mobilegradingsystem.student.fragmentClassProfileBotNav;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FinalTermGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.ClssProfileStudentBotNav;
import com.mobilegradingsystem.mobilegradingsystem.teacher.fragment.ClassProfileBotBNav.ItemListDialogFragment;

import javax.annotation.Nullable;

public class DashboardClassStudentFragement extends Fragment {
    FirebaseFirestore db;
    ClssProfileStudentBotNav act;
    BottomSheetBehavior bottomSheetBehavior;
    ItemListDialogFragment itemListDialogFragment;
    TextView subjectName;
    TextView subjectSched;
    TextView studentNumbers,className,announcementNumber;
    EditText title,desciption;
    String loading = "loading...";
    TextView midtermGrade, fGrade,tentativeFinal;

    public DashboardClassStudentFragement(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        act = (ClssProfileStudentBotNav) getActivity();
        db = FirebaseFirestore.getInstance();
        View view = inflater.inflate(R.layout.frag_class_dashboard_student, container, false);
        subjectName = (TextView) view.findViewById(R.id.subjectName);
        midtermGrade = (TextView) view.findViewById(R.id.midtermGrade);
        tentativeFinal = (TextView) view.findViewById(R.id.tentativeFinal);
        fGrade = (TextView) view.findViewById(R.id.finalGrade);
        subjectSched = (TextView) view.findViewById(R.id.sched);
        getSubjectDetails();
        return view;

    }

    void getSubjectDetails(){
        db.collection("class").document(act.getClassKey()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TeacherClassObjectModel teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                subjectName.setText(teacherClassObjectModel.getName());
                subjectSched.setText(teacherClassObjectModel.getSched());


            }
        });
        db.collection("studentClasses").whereEqualTo("classCode",act.getClassKey()).whereEqualTo("studentUserId", FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentClassObjectModel studentClassObjectModel = documentSnapshot.toObject(StudentClassObjectModel.class);
                    setStudenFinalGrade(studentClassObjectModel);
                }

            }
        });
    }

    private void setStudenFinalGrade(final StudentClassObjectModel studentClassObjectModel){
        FirebaseFirestore.getInstance().collection("termGrade").document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+"midterm").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final FinalTermGradeObjectModel finalTermGradeObjectModelMidterm = documentSnapshot.toObject(FinalTermGradeObjectModel.class);
                try {
                    midtermGrade.setText(finalTermGradeObjectModelMidterm.getGrade()+"");
                }catch (NullPointerException exceps){
                    fGrade.setText("No Grade(Incomplete scores)");
                }
                FirebaseFirestore.getInstance().collection("termGrade").document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+"finals").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        try {
                            FinalTermGradeObjectModel finalTermGradeObjectModelFinals = documentSnapshot.toObject(FinalTermGradeObjectModel.class);
                            Double finalGrade =  (finalTermGradeObjectModelMidterm.getGrade()+(2*finalTermGradeObjectModelFinals.getGrade()))/3;
                            tentativeFinal.setText(finalTermGradeObjectModelFinals.getGrade()+"");
                            fGrade.setText(finalGrade+"");
                        }catch (NullPointerException ex){
                            fGrade.setText("No Grade(Incomplete scores)");
                        }
                    }
                });
            }
        });
    }

}
