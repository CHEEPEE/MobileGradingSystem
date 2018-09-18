package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.DepartmentObjectModel;

import java.util.ArrayList;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class DepartmentRecyclerViewAdapter
        extends RecyclerView.Adapter<DepartmentRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<DepartmentObjectModel> departmentObjectModelArrayList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder{
      public ImageView addImage;
      public ImageView galleryImage;
      public TextView  department;
        public MyViewHolder(View view){
            super(view);
            department = (TextView) view.findViewById(R.id.department);

        }
    }

    public DepartmentRecyclerViewAdapter(Context c, ArrayList<DepartmentObjectModel> galleryDataModels){

        this.departmentObjectModelArrayList = galleryDataModels;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_department,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final DepartmentObjectModel departmentObjectModel = departmentObjectModelArrayList.get(position);
        holder.department.setText(departmentObjectModel.getDepartment());
        holder.department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v,position,departmentObjectModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return departmentObjectModelArrayList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, DepartmentObjectModel departmentObjectModel);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private void addMenuDialog(){

    }
}


