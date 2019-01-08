package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.DepartmentObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FeedbacksObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class FeedbackRecyclerViewAdapter
        extends RecyclerView.Adapter<FeedbackRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<FeedbacksObjectModel> feedbacksObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
     public TextView feedback,userName;
     public RatingBar ratingBar;
     public CircleImageView circleImageView;
        public MyViewHolder(View view){
            super(view);
            feedback = (TextView) view.findViewById(R.id.feedback);
            userName = (TextView) view.findViewById(R.id.userName);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);
            circleImageView = (CircleImageView) view.findViewById(R.id.userImage);
        }
    }

    public FeedbackRecyclerViewAdapter(Context c, ArrayList<FeedbacksObjectModel> feedbacksObjectModels){
        this.feedbacksObjectModelArrayList = feedbacksObjectModels;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_buble,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final  FeedbacksObjectModel feedbacksObjectModel = feedbacksObjectModelArrayList.get(position);


        FirebaseFirestore.getInstance().collection("users").document(feedbacksObjectModel.getStudentUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                holder.userName.setText(userProfileObjectModel.getUserName());
                GlideApp.with(context).load(userProfileObjectModel.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.circleImageView);
            }
        });

         try {
          holder.feedback.setText(feedbacksObjectModel.getFeedback());
          holder.ratingBar.setRating(feedbacksObjectModel.getRating());
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


