package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.Utils;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.AnnouncementObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FinalTermGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.TempUserObject;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.TeacherClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;
import com.mobilegradingsystem.mobilegradingsystem.teacher.ClssProfileTeacherBotNav;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class StudentListTeacherRecyclerViewAdapter
        extends RecyclerView.Adapter<StudentListTeacherRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView studentName,status,grade;
        public CircleImageView userImage;
        public ConstraintLayout container;
        public MyViewHolder(View view){
            super(view);
            studentName = (TextView) view.findViewById(R.id.studentName);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);
            status = (TextView) view.findViewById(R.id.status);
            container  = (ConstraintLayout) view.findViewById(R.id.container);
            grade = (TextView) view.findViewById(R.id.grade);

        }
    }

    public StudentListTeacherRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels){

        this.studentClassObjectModelArrayList = studentClassObjectModels;
        this.context =c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_on_class_with_status,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final StudentClassObjectModel studentClassObjectModel = studentClassObjectModelArrayList.get(position);
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
        holder.status.setText(studentClassObjectModel.getStatus());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setStatus(studentClassObjectModel);
                getAccountDetails(studentClassObjectModel);

            }
        });
        setStudenFinalGrade(studentClassObjectModel,holder.grade);
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

    private void setStudenFinalGrade(final StudentClassObjectModel studentClassObjectModel, final TextView grade){
        FirebaseFirestore.getInstance().collection("termGrade").document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+"midterm").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                final FinalTermGradeObjectModel finalTermGradeObjectModelMidterm = documentSnapshot.toObject(FinalTermGradeObjectModel.class);
                FirebaseFirestore.getInstance().collection("termGrade").document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+"finals").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                       try {
                           FinalTermGradeObjectModel finalTermGradeObjectModelFinals = documentSnapshot.toObject(FinalTermGradeObjectModel.class);
                           Double finalGrade =  (finalTermGradeObjectModelMidterm.getGrade()+(2*finalTermGradeObjectModelFinals.getGrade()))/3;
                           grade.setText(finalGrade+"");
                       }catch (NullPointerException ex){
                           grade.setText("No Grade(Incomplete scores)");
                       }
                    }
                });
            }
        });
    }

    private void getAccountDetails(final StudentClassObjectModel studentClassObjectModel){
        final Dialog dialog = new Dialog(context);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dlg_student_details);
        TextView removeStudent = (TextView) dialog.findViewById(R.id.removeStudent);
        final TextView studentName = (TextView) dialog.findViewById(R.id.studentName);
        final TextView studentId = (TextView) dialog.findViewById(R.id.studentId);
        final TextView password = (TextView) dialog.findViewById(R.id.password);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        studentId.setText(studentClassObjectModel.getStudentId());
        db.collection("tempCreateUsers").whereEqualTo("userSchoolId",studentClassObjectModel.getStudentId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    final TempUserObject tempUserObject = documentSnapshot.toObject(TempUserObject.class);
                    studentName.setText(tempUserObject.getfName()+" "+tempUserObject.getlName());
                    password.setText(tempUserObject.getPassword());
                    password.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            copyToClibBoard(tempUserObject.getPassword());
                        }
                    });
                }
            }
        });

        removeStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(studentClassObjectModel);
                dialog.dismiss();
            }
        });
    }

    void copyToClibBoard(String code){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("userId", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context,"Copy to Clipboard "+code,Toast.LENGTH_SHORT).show();
    }

    private void setStatus(final StudentClassObjectModel studentClassObjectModel){
        final Dialog dialog = new Dialog(context);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        String message = studentClassObjectModel.getStatus().equals("approved") ? "Remove Student?": "Approve Student?";
        dialog.setContentView(R.layout.dlg_set_student_on_class_status);
        Window window = dialog.getWindow();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        TextView label = (TextView) dialog.findViewById(R.id.message);
        label.setText(message);

            dialog.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (studentClassObjectModel.getStatus().equals("pending")) {
                        db.collection("studentClasses").document(studentClassObjectModel.getKey()).update("status", "approved").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                            }
                        });
                    }else if (studentClassObjectModel.getStatus().equals("approved")) {
                        db.collection("studentClasses").document(studentClassObjectModel.getKey()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });

    }
}


