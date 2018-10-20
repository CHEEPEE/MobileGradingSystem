package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FeedBackAnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.FeedBackAct;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class FeedBackListStudentsRecyclerViewAdapter
        extends RecyclerView.Adapter<FeedBackListStudentsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<FeedBackAnnouncementObjectModel> feedBackAnnouncementObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView userName,description;
        public CircleImageView userAccountImage;

        public MyViewHolder(View view){
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            description = (TextView) view.findViewById(R.id.des);
            userAccountImage = (CircleImageView) view.findViewById(R.id.accountImage);
        }
    }

    public FeedBackListStudentsRecyclerViewAdapter(Context c, ArrayList<FeedBackAnnouncementObjectModel> feedBackAnnouncementObjectModels){

        this.feedBackAnnouncementObjectModelArrayList = feedBackAnnouncementObjectModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_bubble,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FeedBackAnnouncementObjectModel feedBackAnnouncementObjectModel = feedBackAnnouncementObjectModelArrayList.get(position);
        holder.description.setText(feedBackAnnouncementObjectModel.getFeedBack());

        FirebaseFirestore.getInstance().collection("users")
                .document(feedBackAnnouncementObjectModel.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                GlideApp.with(context)
                        .load(userProfileObjectModel.getUserImage())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.userAccountImage);
                if (userProfileObjectModel.getUserType().equals("student")){
                    FirebaseFirestore.getInstance().collection("studentProfile")
                            .document(feedBackAnnouncementObjectModel.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            StudentProfileProfileObjectModel studentProfileProfileObjectModel = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                            holder.userName.setText(studentProfileProfileObjectModel.getfName()+" "+studentProfileProfileObjectModel.getlName());
                        }
                    });
                }else {
                    FirebaseFirestore.getInstance().collection("teacherProfile")
                            .document(feedBackAnnouncementObjectModel.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            TeacherProfileProfileObjectModel teacherProfileProfileObjectModel = documentSnapshot.toObject(TeacherProfileProfileObjectModel.class);
                            holder.userName.setText(teacherProfileProfileObjectModel.getTeacherName());
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedBackAnnouncementObjectModelArrayList.size();
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


