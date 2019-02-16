package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassProjectListStudentsAct;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassQuizLongTestListStudentsAct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class QuizLongTestClassRecordRecyclerViewAdapter
        extends RecyclerView.Adapter<QuizLongTestClassRecordRecyclerViewAdapter.MyViewHolder> {
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

    public QuizLongTestClassRecordRecyclerViewAdapter(Context c, ArrayList<ParticipationCategoryGradeObjectModel> studentClassObjectModels,String term) {

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

        try {
            holder.part_date.setText(new SimpleDateFormat("E MMM dd yyyy @ hh:mm a").format(participationCategoryGradeObjectModel.getTimeStamp()));
        }catch (NullPointerException ex){

        }
        holder.score.setText(participationCategoryGradeObjectModel.getMaxScode()+"");
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ClassQuizLongTestListStudentsAct.class);
                i.putExtra("key",participationCategoryGradeObjectModel.getClassCode());
                i.putExtra("partKey",participationCategoryGradeObjectModel.getKey());
                i.putExtra("term",term);
                context.startActivity(i);
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Proceed to delete "+ new SimpleDateFormat("E MMM dd yyyy @ hh:mm a").format(participationCategoryGradeObjectModel.getTimeStamp()))

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    participationCategoryGradeObjectModel.deleteParticipation("quizLongTestCategory",participationCategoryGradeObjectModel.getKey());
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }catch (NullPointerException ex){

                }
                return true;
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


