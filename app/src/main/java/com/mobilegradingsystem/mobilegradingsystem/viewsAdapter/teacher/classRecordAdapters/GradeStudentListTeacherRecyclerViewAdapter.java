package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentAttendenceCharacterClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentGradeObjectModel;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class GradeStudentListTeacherRecyclerViewAdapter
        extends RecyclerView.Adapter<GradeStudentListTeacherRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    private Context context;
    private ArrayList<Double> studentProjectValue = new ArrayList<>();
    private ArrayList<Double> projectTotal = new ArrayList<>();
    private String term;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView studentName;
        public CircleImageView userImage;
        public MyViewHolder(View view){
            super(view);
            studentName = (TextView) view.findViewById(R.id.studentName);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);
        }
    }

    public GradeStudentListTeacherRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels,String term){

        this.studentClassObjectModelArrayList = studentClassObjectModels;
        this.context =c;
        this.studentProjectValue = new ArrayList<>(studentClassObjectModels.size());
        this.projectTotal = new ArrayList<>(studentClassObjectModels.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_on_class,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StudentClassObjectModel studentClassObjectModel = studentClassObjectModelArrayList.get(position);
        FirebaseFirestore.getInstance().collection("studentProfile")
                .document(studentClassObjectModel.getStudentUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        StudentProfileProfileObjectModel studentProfile = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                        holder.studentName.setText(studentProfile.getfName()+" "+studentProfile.getlName());
                    }
                });

        FirebaseFirestore.getInstance().collection("users")
                .document(studentClassObjectModel.getStudentUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                        GlideApp.with(context).load(userProfileObjectModel.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.userImage);
                    }
                });

        calculateGrade(studentClassObjectModel.getClassCode(),studentClassObjectModel,position);
    }

    @Override
    public int getItemCount() {
        return studentClassObjectModelArrayList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, ProgramsObjectModel programsObjectModel);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private void calculateGrade(String classCode, final StudentClassObjectModel studentClassObjectModel, final int positon){
        final ProjectValue projectValue = new ProjectValue();
        final StudentGradeObjectModel studentGradeObjectModel = new StudentGradeObjectModel();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("attendance")
                .whereEqualTo("term",term)
                .whereEqualTo("studentUserId",studentClassObjectModel.getStudentUserId())
                .whereEqualTo("classCode",studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentAttendenceCharacterClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentAttendenceCharacterClassObjectModel.class);
                    studentGradeObjectModel.setAttendance(attendenceClassObjectModel.getValue());
                }
            }
        });
        db.collection("character")
                .whereEqualTo("term",term)
                .whereEqualTo("studentUserId",studentClassObjectModel.getStudentUserId())
                .whereEqualTo("classCode",studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentAttendenceCharacterClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentAttendenceCharacterClassObjectModel.class);
                    studentGradeObjectModel.setCharacter(attendenceClassObjectModel.getValue());
                }
            }
        });
//        db.collection("projectCategory")
//                .whereEqualTo("classCode",classCode)
//                .orderBy("timeStamp", Query.Direction.DESCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        int classPartNum = queryDocumentSnapshots.getDocuments().size();
//
//
//                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
//                            ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel = documentSnapshot.toObject(ParticipationCategoryGradeObjectModel.class);
//                            projectTotal.set(positon,projectTotal.get(positon)+participationCategoryGradeObjectModel.getMaxScode());
//                            db.collection("project")
//                                    .whereEqualTo("studentUserId",studentClassObjectModel.getStudentUserId())
//                                    .whereEqualTo("classCode",studentClassObjectModel.getClassCode())
////                .orderBy("timeStamp", Query.Direction.DESCENDING)
//                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//
//                                            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
//                                                StudentAttendenceCharacterClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentAttendenceCharacterClassObjectModel.class);
//                                                studentProjectValue.set(positon,studentProjectValue.get(positon)+attendenceClassObjectModel.getValue());
//                                            }
//                                            System.out.println(projectTotal.get(positon)+"/"+studentProjectValue.get(positon));
//                                        }
//                                    });
//                        }
//                    }
//                });

    }

    private class  ProjectValue{
        int ptotal;
        int sTotal;

        public ProjectValue(){

        }
        public int getPtotal() {
            return ptotal;
        }

        public int getsTotal() {
            return sTotal;
        }

        public void setPtotal(int ptotal) {
            this.ptotal = ptotal;
        }

        public void setsTotal(int sTotal) {
            this.sTotal = sTotal;
        }
    }
}


