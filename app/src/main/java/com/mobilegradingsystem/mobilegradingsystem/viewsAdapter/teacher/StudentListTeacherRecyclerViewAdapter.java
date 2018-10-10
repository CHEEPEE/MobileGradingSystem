package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class StudentListTeacherRecyclerViewAdapter
        extends RecyclerView.Adapter<StudentListTeacherRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView studentName,status;
        public CircleImageView userImage;
        public ConstraintLayout container;
        public MyViewHolder(View view){
            super(view);
            studentName = (TextView) view.findViewById(R.id.studentName);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);
            status = (TextView) view.findViewById(R.id.status);
            container  = (ConstraintLayout) view.findViewById(R.id.container);

        }
    }

    public StudentListTeacherRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels){

        this.studentClassObjectModelArrayList = studentClassObjectModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_on_class_with_status,parent,false);

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
        holder.status.setText(studentClassObjectModel.getStatus());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(studentClassObjectModel);
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

    private void setStatus(final StudentClassObjectModel studentClassObjectModel){
        final Dialog dialog = new Dialog(context);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        String message = studentClassObjectModel.getStatus().equals("approved") ? "Remove Student?": "Approve Student?";
        dialog.setContentView(R.layout.dlg_set_student_on_class_status);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        TextView label = (TextView) dialog.findViewById(R.id.message);
        label.setText(message);

            dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (studentClassObjectModel.getStatus().equals("pending")) {
                        db.collection("studentClasses").document(studentClassObjectModel.getKey()).update("status", "approved").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                            }
                        });
                    }else if (studentClassObjectModel.getStatus().equals("approved")) {
                        db.collection("studentClasses").document(studentClassObjectModel.getKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });

    }
}


