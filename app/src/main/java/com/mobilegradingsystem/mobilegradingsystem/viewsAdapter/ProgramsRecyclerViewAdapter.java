package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;

import java.util.ArrayList;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class ProgramsRecyclerViewAdapter
        extends RecyclerView.Adapter<ProgramsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<ProgramsObjectModel> programsObjectModelArrayList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder{
      public ImageView addImage;
      public ImageView galleryImage;
      public TextView programName;
        public MyViewHolder(View view){
            super(view);
            programName = (TextView) view.findViewById(R.id.department);
        }
    }

    public ProgramsRecyclerViewAdapter(Context c, ArrayList<ProgramsObjectModel> programsObjectModels){

        this.programsObjectModelArrayList = programsObjectModels;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_department,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProgramsObjectModel programsObjectModel = programsObjectModelArrayList.get(position);
        holder.programName.setText(programsObjectModel.getProgram());
        holder.programName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v,position,programsObjectModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return programsObjectModelArrayList.size();
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


