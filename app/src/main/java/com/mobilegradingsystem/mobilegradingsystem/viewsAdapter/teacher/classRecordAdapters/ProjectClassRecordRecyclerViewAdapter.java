package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassPartListStudentsAct;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassProjectListStudentsAct;

import java.util.ArrayList;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class ProjectClassRecordRecyclerViewAdapter
        extends RecyclerView.Adapter<ProjectClassRecordRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ParticipationCategoryGradeObjectModel> participationCategoryGradeObjectModelArrayList = new ArrayList<>();
    private Context context;
    private String term;

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

    public ProjectClassRecordRecyclerViewAdapter(Context c, ArrayList<ParticipationCategoryGradeObjectModel> studentClassObjectModels,String term) {

        this.participationCategoryGradeObjectModelArrayList = studentClassObjectModels;
        this.context = c;
        this.term = term;
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
                Intent i = new Intent(context, ClassProjectListStudentsAct.class);
                i.putExtra("key",participationCategoryGradeObjectModel.getClassCode());
                i.putExtra("partKey",participationCategoryGradeObjectModel.getKey());
                i.putExtra("term",term);
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


