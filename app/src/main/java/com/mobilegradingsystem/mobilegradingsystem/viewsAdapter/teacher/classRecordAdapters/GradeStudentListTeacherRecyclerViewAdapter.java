package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobilegradingsystem.mobilegradingsystem.R;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.FinalTermGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.GradeEquivalentObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.ProgramsObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.UserProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.student.StudentClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ClassRecordVersion;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ExamGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.ParticipationCategoryGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentAttendenceCharacterClassObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentGradeObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher.StudentParticipationClassObjectModel;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Keji's Lab on 19/01/2018.
 */

public class GradeStudentListTeacherRecyclerViewAdapter
        extends RecyclerView.Adapter<GradeStudentListTeacherRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<StudentClassObjectModel> studentClassObjectModelArrayList = new ArrayList<>();
    private Context context;
    private ArrayList<Double> studentProjectValue = new ArrayList<>();
    private ArrayList<Double> projectTotal = new ArrayList<>();
    private String term;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView studentName,grade;
        public CircleImageView userImage;
        public MyViewHolder(View view){
            super(view);
            studentName = (TextView) view.findViewById(R.id.studentName);
            grade = (TextView) view.findViewById(R.id.grade);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);
        }
    }

    public GradeStudentListTeacherRecyclerViewAdapter(Context c, ArrayList<StudentClassObjectModel> studentClassObjectModels,String term){

        this.studentClassObjectModelArrayList = studentClassObjectModels;
        this.context =c;
        this.studentProjectValue = new ArrayList<>(studentClassObjectModels.size());
        this.projectTotal = new ArrayList<>(studentClassObjectModels.size());
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

        calculateGrade(studentClassObjectModel.getClassCode(),studentClassObjectModel,position,holder.grade);
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

    private void calculateGrade(String classCode, final StudentClassObjectModel studentClassObjectModel, final int positon, final TextView grade){

        final StudentGradeObjectModel studentGradeObjectModel = new StudentGradeObjectModel();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("attendance")
                .whereEqualTo("term",term)
                .whereEqualTo("studentUserId",studentClassObjectModel.getStudentUserId())
                .whereEqualTo("classCode",studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentAttendenceCharacterClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentAttendenceCharacterClassObjectModel.class);
                    studentGradeObjectModel.setAttendance(attendenceClassObjectModel.getValue());
                }
            }
        });

        db.collection("character")
                .whereEqualTo("term",term)
                .whereEqualTo("studentUserId",studentClassObjectModel.getStudentUserId())
                .whereEqualTo("classCode",studentClassObjectModel.getClassCode()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                    StudentAttendenceCharacterClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentAttendenceCharacterClassObjectModel.class);
                    studentGradeObjectModel.setCharacter(attendenceClassObjectModel.getValue());
                }
            }
        });
        db.collection("examTotalScore").document(classCode+term).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                 final ExamGradeObjectModel examGradeObjectModel = documentSnapshot.toObject(ExamGradeObjectModel.class);
                db.collection("exam")
                        .document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+term).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        final StudentParticipationClassObjectModel examStudentGrade = documentSnapshot.toObject(StudentParticipationClassObjectModel.class);
                        try {
                            db.collection("scoreEquivalents").document("4TYJW5v4LBujNmMCNEPs").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    GradeEquivalentObjectModel gradeEquivalentObjectModel = documentSnapshot.toObject(GradeEquivalentObjectModel.class);
                                    for (String i:gradeEquivalentObjectModel.getRawScore()){
                                        try {
                                            int raw = (int) Math.round((examStudentGrade.getValue()/examGradeObjectModel.getMaxScode())*100);
                                            if (Integer.parseInt(raw+"")==Integer.parseInt(i)){
                                                System.out.println("exam %D "+i);
                                                studentGradeObjectModel.setExam(Double.parseDouble(i));
                                            }
                                        }catch (NullPointerException ex){

                                        }
                                    }
                                }
                            });
                        }catch (NullPointerException ex){

                        }
                    }
                });
            }
        });


        db.collection("projectCategory")
                .whereEqualTo("classCode",classCode)
                .whereEqualTo("term",term)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final int classPartNum = queryDocumentSnapshots.getDocuments().size();
                        final ProjectValue projectValue1 = new ProjectValue();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel = documentSnapshot.toObject(ParticipationCategoryGradeObjectModel.class);
                            projectValue1.setPtotal(projectValue1.getPtotal() + participationCategoryGradeObjectModel.getMaxScode());
                            System.out.println("max Score "+participationCategoryGradeObjectModel.getMaxScode());
                            System.out.println(studentClassObjectModel.getStudentUserId()+participationCategoryGradeObjectModel.getKey());
                            db.collection("project")
                                    .document(studentClassObjectModel.getStudentUserId()+participationCategoryGradeObjectModel.getKey())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                           try {
                                               StudentParticipationClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentParticipationClassObjectModel.class);
                                               projectValue1.setsTotal(projectValue1.getsTotal() + attendenceClassObjectModel.getValue());
                                               System.out.println("inside : "+attendenceClassObjectModel.getValue());
                                               projectValue1.setParNum(projectValue1.getParNum()+1);
                                           }catch (NullPointerException ex){

                                           }
                                            if (classPartNum == projectValue1.getParNum()){
                                                db.collection("scoreEquivalents").document("4TYJW5v4LBujNmMCNEPs").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                        GradeEquivalentObjectModel gradeEquivalentObjectModel = documentSnapshot.toObject(GradeEquivalentObjectModel.class);
                                                        System.out.println("percentage: "+((projectValue1.getsTotal()/projectValue1.getPtotal())*100));
                                                        System.out.println("percentage: "+((projectValue1.getsTotal()/projectValue1.getPtotal())*100));
                                                        for (String i:gradeEquivalentObjectModel.getRawScore()){
                                                            int raw = (int) Math.round((projectValue1.getsTotal()/projectValue1.getPtotal())*100);
                                                            if (Integer.parseInt(raw+"")==Integer.parseInt(i)){
                                                                System.out.println("%D "+i);
                                                                studentGradeObjectModel.setProject(Double.parseDouble(i));
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });

        db.collection("quizLongTestCategory")
                .whereEqualTo("classCode",classCode)
                .whereEqualTo("term",term)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final int classPartNum = queryDocumentSnapshots.getDocuments().size();
                        final ProjectValue projectValue1 = new ProjectValue();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel = documentSnapshot.toObject(ParticipationCategoryGradeObjectModel.class);
                            projectValue1.setPtotal(projectValue1.getPtotal() + participationCategoryGradeObjectModel.getMaxScode());
                            System.out.println("max Score "+participationCategoryGradeObjectModel.getMaxScode());
                            System.out.println(studentClassObjectModel.getStudentUserId()+participationCategoryGradeObjectModel.getKey());
                            db.collection("quizLongTest")
                                    .document(studentClassObjectModel.getStudentUserId()+participationCategoryGradeObjectModel.getKey())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            try {
                                                StudentParticipationClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentParticipationClassObjectModel.class);
                                                projectValue1.setsTotal(projectValue1.getsTotal() + attendenceClassObjectModel.getValue());
                                                System.out.println("inside : "+attendenceClassObjectModel.getValue());
                                                projectValue1.setParNum(projectValue1.getParNum()+1);
                                            }catch (NullPointerException ex){

                                            }
                                            if (classPartNum == projectValue1.getParNum()){
                                                db.collection("scoreEquivalents").document("4TYJW5v4LBujNmMCNEPs").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                        GradeEquivalentObjectModel gradeEquivalentObjectModel = documentSnapshot.toObject(GradeEquivalentObjectModel.class);
                                                        System.out.println("percentage: "+((projectValue1.getsTotal()/projectValue1.getPtotal())*100));
                                                        System.out.println("percentage: "+((projectValue1.getsTotal()/projectValue1.getPtotal())*100));
                                                        for (String i:gradeEquivalentObjectModel.getRawScore()){
                                                            int raw = (int) Math.round((projectValue1.getsTotal()/projectValue1.getPtotal())*100);
                                                            if (Integer.parseInt(raw+"")==Integer.parseInt(i)){
                                                                System.out.println("%D "+i);
                                                                studentGradeObjectModel.setQuizLongTest(Double.parseDouble(i));
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });

        db.collection("participationCategory")
                .whereEqualTo("classCode",classCode)
                .whereEqualTo("term",term)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final int classPartNum = queryDocumentSnapshots.getDocuments().size();
                        final ProjectValue projectValue1 = new ProjectValue();
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                            ParticipationCategoryGradeObjectModel participationCategoryGradeObjectModel = documentSnapshot.toObject(ParticipationCategoryGradeObjectModel.class);
                            projectValue1.setPtotal(projectValue1.getPtotal() + participationCategoryGradeObjectModel.getMaxScode());
                            System.out.println("max Score "+participationCategoryGradeObjectModel.getMaxScode());
                            System.out.println(studentClassObjectModel.getStudentUserId()+participationCategoryGradeObjectModel.getKey());
                            db.collection("participation")
                                    .document(studentClassObjectModel.getStudentUserId()+participationCategoryGradeObjectModel.getKey())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            try {
                                                StudentParticipationClassObjectModel attendenceClassObjectModel = documentSnapshot.toObject(StudentParticipationClassObjectModel.class);
                                                projectValue1.setsTotal(projectValue1.getsTotal() + attendenceClassObjectModel.getValue());
                                                System.out.println("inside : "+attendenceClassObjectModel.getValue());
                                                projectValue1.setParNum(projectValue1.getParNum()+1);
                                            }catch (NullPointerException ex){

                                            }
                                            if (classPartNum == projectValue1.getParNum()){
                                                db.collection("scoreEquivalents").document("4TYJW5v4LBujNmMCNEPs").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                        GradeEquivalentObjectModel gradeEquivalentObjectModel = documentSnapshot.toObject(GradeEquivalentObjectModel.class);
                                                        System.out.println("percentage: "+((projectValue1.getsTotal()/projectValue1.getPtotal())*100));
                                                        System.out.println("percentage: "+((projectValue1.getsTotal()/projectValue1.getPtotal())*100));
                                                        for (String i:gradeEquivalentObjectModel.getRawScore()){
                                                            int raw = (int) Math.round((projectValue1.getsTotal()/projectValue1.getPtotal())*100);
                                                            if (Integer.parseInt(raw+"")==Integer.parseInt(i)){
                                                                System.out.println("%D "+i);
                                                                studentGradeObjectModel.setClassParticipation(Double.parseDouble(i));
                                                                System.out.println("Stude Grade Object Model"+
                                                                        "attendance : "+studentGradeObjectModel.getAttendance()+" "+
                                                                        "Character : "+studentGradeObjectModel.getCharacter()+" "+
                                                                        "participation : "+ studentGradeObjectModel.getClassParticipation()+" "+
                                                                        "exam : "+studentGradeObjectModel.getExam()+" "+
                                                                        "project : "+studentGradeObjectModel.getProject()+" "+
                                                                        "Long Test : "+studentGradeObjectModel.getQuizLongTest()
                                                                );
                                                                solveStudentTermGrade(studentGradeObjectModel,grade,studentClassObjectModel);
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    void solveStudentTermGrade(final StudentGradeObjectModel studentGradeObjectModel, final TextView grade, final StudentClassObjectModel studentClassObjectModel){
        FirebaseFirestore.getInstance().collection("classRecordVersion").document("4TYJW5v4LBujNmMCNEPs").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                double termGrade = 0;
                ClassRecordVersion classRecordVersion = documentSnapshot.toObject(ClassRecordVersion.class);
                System.out.println(classRecordVersion.getAttendance()
                        +" "+classRecordVersion.getQuizLongTest()+" "+
                        " "+classRecordVersion.getProjects()+" "+
                        classRecordVersion.getExam()+" "+
                        classRecordVersion.getClassParticipation()+" "+
                        classRecordVersion.getCharacter()
                );
                termGrade += studentGradeObjectModel.getAttendance()*classRecordVersion.getAttendance();
                termGrade += studentGradeObjectModel.getCharacter()*classRecordVersion.getCharacter();
                termGrade += studentGradeObjectModel.getClassParticipation()*classRecordVersion.getClassParticipation();
                termGrade += studentGradeObjectModel.getExam()*classRecordVersion.getExam();
                termGrade += studentGradeObjectModel.getProject()*classRecordVersion.getProjects();
                termGrade += studentGradeObjectModel.getQuizLongTest()*classRecordVersion.getQuizLongTest();
                System.out.println("final Grade "+termGrade);
                grade.setText(termGrade+"");
                setFinalTermGrade(termGrade,studentClassObjectModel);
            }
        });
    }
    void setFinalTermGrade(final Double grade, StudentClassObjectModel studentClassObjectModel)
    {
        FinalTermGradeObjectModel finalTermGradeObjectModel = new FinalTermGradeObjectModel(studentClassObjectModel.getStudentUserId(),studentClassObjectModel.getClassCode(),term,grade);
        FirebaseFirestore.getInstance()
                .collection("termGrade")
                .document(studentClassObjectModel.getClassCode()+studentClassObjectModel.getStudentUserId()+term)
                .set(finalTermGradeObjectModel);
    }

    private class  ProjectValue{
        double ptotal;
        double sTotal;
        double parNum;

        public ProjectValue(){

        }

        public double getParNum() {
            return parNum;
        }

        public void setParNum(double parNum) {
            this.parNum = parNum;
        }

        public double getPtotal() {
            return ptotal;
        }

        public double getsTotal() {
            return sTotal;
        }

        public void setPtotal(double ptotal) {
            this.ptotal = ptotal;
        }

        public void setsTotal(double sTotal) {
            this.sTotal = sTotal;
        }
    }
}


