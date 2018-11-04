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
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FeedbacksObjectModel;

import java.util.ArrayList;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class FeedbackRecyclerViewAdapter
        extends RecyclerView.Adapter<FeedbackRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<FeedbacksObjectModel> feedbacksObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
     public TextView feedback;
        public MyViewHolder(View view){
            super(view);
            feedback = (TextView) view.findViewById(R.id.feedback);
        }
    }

    public FeedbackRecyclerViewAdapter(Context c, ArrayList<FeedbacksObjectModel> feedbacksObjectModels){
        this.feedbacksObjectModelArrayList = feedbacksObjectModels;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item_buble,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final  FeedbacksObjectModel feedbacksObjectModel = feedbacksObjectModelArrayList.get(position);

         try {
          holder.feedback.setText(feedbacksObjectModel.getFeedback());
      }catch (NullPointerException ex){

      }
    }

    @Override
    public int getItemCount() {
        return feedbacksObjectModelArrayList.size();
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


