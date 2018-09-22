package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student;

import android.content.Context;
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

import java.util.ArrayList;

import javax.annotation.Nullable;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class ClassStudentRecyclerViewAdapter
        extends RecyclerView.Adapter<ClassStudentRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder{
      public TextView className,sched,des,accessCode;
        public MyViewHolder(View view){
            super(view);
            className = (TextView) view.findViewById(R.id.className);
            sched = (TextView) view.findViewById(R.id.classSched);
            des = (TextView) view.findViewById(R.id.des);

        }
    }

    public ClassStudentRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels){

        this.studentClassObjectModelArrayList = studentClassObjectModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_class,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StudentClassObjectModel studentClassObjectModel = studentClassObjectModelArrayList.get(position);
        db.collection("class").document(studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TeacherClassObjectModel teacherClassObjectModel = documentSnapshot.toObject(TeacherClassObjectModel.class);
                holder.className.setText(teacherClassObjectModel.getName());
                holder.des.setText(teacherClassObjectModel.getDescription());
                holder.sched.setText(teacherClassObjectModel.getSched());
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


