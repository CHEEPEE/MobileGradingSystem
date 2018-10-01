package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.student;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;

import java.util.ArrayList;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class AnnoucementListStudentsRecyclerViewAdapter
        extends RecyclerView.Adapter<AnnoucementListStudentsRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<AnnouncementObjectModel> announcementObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title,description,date;

        public MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            date = (TextView) view.findViewById(R.id.date);

        }
    }

    public AnnoucementListStudentsRecyclerViewAdapter(Context c, ArrayList<AnnouncementObjectModel> announcementObjectModels){

        this.announcementObjectModelArrayList = announcementObjectModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement_student,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AnnouncementObjectModel announcementObjectModel = announcementObjectModelArrayList.get(position);
        holder.title.setText(announcementObjectModel.getTitle());
        holder.description.setText(announcementObjectModel.getDescription());
        holder.date.setText(announcementObjectModel.getTimeStamp()+"");

    }

    @Override
    public int getItemCount() {
        return announcementObjectModelArrayList.size();
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


