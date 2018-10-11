package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.app.Dialog;
import android.content.Context;
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
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentAttendenceCharacterClassObjectModel;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class AttendanceClassRecordRecyclerViewAdapter
        extends RecyclerView.Adapter<AttendanceClassRecordRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    private Context context;
    private String term;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView studentName,grade;
        public CircleImageView userImage;
        public ConstraintLayout container;
        public MyViewHolder(View view){
            super(view);
            studentName = (TextView) view.findViewById(R.id.studentName);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);
            container = (ConstraintLayout) view.findViewById(R.id.container);
            grade = (TextView) view.findViewById(R.id.grade);

        }
    }

    public AttendanceClassRecordRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels,String term){

        this.studentClassObjectModelArrayList = studentClassObjectModels;
        this.context =c;
        this.term = term;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_on_class,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StudentClassObjectModel studentClassObjectModel = studentClassObjectModelArrayList.get(position);
        final  FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore.getInstance().collection("studentProfile")
                .document(studentClassObjectModel.getStudentUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        StudentProfileProfileObjectModel studentProfile = documentSnapshot.toObject(StudentProfileProfileObjectModel.class);
                        holder.studentName.setText(studentProfile.getfName()+" "+studentProfile.getlName());
                    }
                });

        FirebaseFirestore.getInstance().collection("users")
                .document(studentClassObjectModel.getStudentUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        UserProfileObjectModel userProfileObjectModel = documentSnapshot.toObject(UserProfileObjectModel.class);
                        GlideApp.with(context).load(userProfileObjectModel.getUserImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.userImage);
                    }
                });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGradeDialog(studentClassObjectModel);
            }
        });
        db.collection("attendance")
                .whereEqualTo("studentUserId",studentClassObjectModel.getStudentUserId())
                .whereEqualTo("term",term)
                .whereEqualTo("classCode",studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentAttendenceCharacterClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentAttendenceCharacterClassObjectModel.class);
                    holder.grade.setText(attendenceClassObjectModel.getValue()+"");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentClassObjectModelArrayList.size();
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, ProgramsObjectModel programsObjectModel);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickListener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

   private void setGradeDialog(final StudentClassObjectModel studentClassObjectModel){
        final Dialog  dialog = new Dialog(context);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       dialog.setCancelable(true);
       dialog.setContentView(R.layout.dlg_input_grade_attendace_character);
       Window window = dialog.getWindow();
       dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
       window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
       dialog.show();
       final EditText inputGrade = (EditText) dialog.findViewById(R.id.inputGrade);
       dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              try {
                  if (!inputGrade.getText().toString().equals(null)){
                      if (Integer.parseInt(inputGrade.getText().toString()) < 65){
                          inputGrade.setError("Grade should not be less than 65");
                      }else if (Integer.parseInt(inputGrade.getText().toString())>95){
                          inputGrade.setError("Grade should not be more than 95");
                      }else {
                          String key = db.collection("attendance").document().getId();
                          StudentAttendenceCharacterClassObjectModel studentAttendenceCharacterClassObjectModel =
                                  new StudentAttendenceCharacterClassObjectModel(key,
                                          studentClassObjectModel.getStudentId(),
                                          studentClassObjectModel.getStudentUserId(),
                                          Double.parseDouble(inputGrade.getText().toString()),
                                          studentClassObjectModel.getClassCode(),term);
                          db.collection("attendance").document(key)
                                  .set(studentAttendenceCharacterClassObjectModel)
                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                          dialog.dismiss();
                                      }
                                  });
                      }
                  }else {
                      inputGrade.setError("Empty" +
                              "");
                  }
              }catch (NumberFormatException e){

              }

           }
       });
   }
}


