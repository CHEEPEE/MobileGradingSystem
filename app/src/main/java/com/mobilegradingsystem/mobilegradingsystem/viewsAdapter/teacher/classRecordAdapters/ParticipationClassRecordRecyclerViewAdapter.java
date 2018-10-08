package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentAttendenceCharacterClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassPartListStudentsAct;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class ParticipationClassRecordRecyclerViewAdapter
        extends RecyclerView.Adapter<ParticipationClassRecordRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ParticipationCategoryGradeObjectModel> participationCategoryGradeObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView part_date,score;
        public ConstraintLayout container;


        public MyViewHolder(View view) {
            super(view);
            part_date = (TextView) view.findViewById(R.id.part_date);
            score = (TextView) view.findViewById(R.id.score);
            container  =(ConstraintLayout) view.findViewById(R.id.container);
        }
    }

    public ParticipationClassRecordRecyclerViewAdapter(Context c, ArrayList<ParticipationCategoryGradeObjectModel> studentClassObjectModels) {

        this.participationCategoryGradeObjectModelArrayList = studentClassObjectModels;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participation_category_class_record, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel = participationCategoryGradeObjectModelArrayList.get(position);
        holder.part_date.setText(participationCategoryGradeObjectModel.getTimeStamp()+"");
        holder.score.setText(participationCategoryGradeObjectModel.getMaxScode()+"");
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ClassPartListStudentsAct.class);
                i.putExtra("key",participationCategoryGradeObjectModel.getClassCode());
                i.putExtra("partKey",participationCategoryGradeObjectModel.getKey());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return participationCategoryGradeObjectModelArrayList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, ProgramsObjectModel programsObjectModel);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}


