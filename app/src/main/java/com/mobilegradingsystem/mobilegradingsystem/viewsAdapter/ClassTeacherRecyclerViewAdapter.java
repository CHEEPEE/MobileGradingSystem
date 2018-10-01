package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClassProfileTeacherAct;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class ClassTeacherRecyclerViewAdapter
        extends RecyclerView.Adapter<ClassTeacherRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<TeacherClassObjectModel> teacherClassObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
      public TextView className,sched,des,accessCode;
      public ImageView viewClassProrfile,copyCode;
        public MyViewHolder(View view){
            super(view);
            className = (TextView) view.findViewById(R.id.className);
            sched = (TextView) view.findViewById(R.id.classSched);
            des = (TextView) view.findViewById(R.id.des);
            accessCode = (TextView) view.findViewById(R.id.accessCode);
            viewClassProrfile = (ImageView) view.findViewById(R.id.viewClassProrfile);
            copyCode = (ImageView) view.findViewById(R.id.copyCode);
        }
    }

    public ClassTeacherRecyclerViewAdapter(Context c, ArrayList<TeacherClassObjectModel> teacherClassObjectModels){

        this.teacherClassObjectModelArrayList = teacherClassObjectModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_class,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TeacherClassObjectModel teacherClassObjectModel = teacherClassObjectModelArrayList.get(position);
        holder.className.setText(teacherClassObjectModel.getName());
        holder.sched.setText(teacherClassObjectModel.getSched());
        holder.des.setText(teacherClassObjectModel.getDescription());
        holder.accessCode.setText(teacherClassObjectModel.getClassKey());
        holder.viewClassProrfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ClssProfileTeacherBotNav.class);
                i.putExtra("classKey",teacherClassObjectModel.getClassKey());
                context.startActivity(i);
            }
        });
        holder.copyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClibBoard(teacherClassObjectModel.getClassKey());
            }
        });
    }
    @Override
    public int getItemCount() {
        return teacherClassObjectModelArrayList.size();
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

    void copyToClibBoard(String code){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("userId", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context,"Copy to Clipboard "+code,Toast.LENGTH_SHORT).show();
    }
}


