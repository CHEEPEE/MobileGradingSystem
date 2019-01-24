package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.ClssProfileStudentBotNav;

import java.util.ArrayList;

import javax.annotation.Nullable;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class ClassStudentRecyclerViewAdapter
        extends RecyclerView.Adapter<ClassStudentRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
      public TextView className,sched,des,accessCode,vieClass,teacherName,schoolYear,semester;
        public MyViewHolder(View view){
            super(view);
            className = (TextView) view.findViewById(R.id.announcementTitle);
            sched = (TextView) view.findViewById(R.id.classSched);
            des = (TextView) view.findViewById(R.id.des);
            vieClass=  (TextView) view.findViewById(R.id.vieClass);
            teacherName = (TextView) view.findViewById(R.id.teacherName);
            schoolYear = (TextView) view.findViewById(R.id.schoolYear);
            semester = (TextView) view.findViewById(R.id.semester);
        }
    }

    public ClassStudentRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels){
        this.context = c;
        this.studentClassObjectModelArrayList = studentClassObjectModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_class,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final StudentClassObjectModel studentClassObjectModel = studentClassObjectModelArrayList.get(position);
        db.collection("class").document(studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final TeacherClassObjectModel teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
              try {
                  holder.className.setText(teacherClassObjectModel.getName());
                  holder.des.setText(teacherClassObjectModel.getDescription());
                  holder.sched.setText(teacherClassObjectModel.getSched());
                  holder.schoolYear.setText(teacherClassObjectModel.getSchoolYear());
                  holder.semester.setText(teacherClassObjectModel.getSemester().equals("1")?"1st Semester":"2nd Semester");
              }catch (NullPointerException ex){

              }
            }
        });

        holder.vieClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ClssProfileStudentBotNav.class);
                i.putExtra("classKey",studentClassObjectModel.getClassCode());
                context.startActivity(i);
            }
        });
        db.collection("class").document(studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                db.collection("teacherProfile").document(documentSnapshot.get("userId").toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        holder.teacherName.setText(documentSnapshot.get("teacherName").toString());
                    }
                });
            }
        });
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

    private void addMenuDialog(){

    }
}


