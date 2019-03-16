package com.mobilegradingsystem.mobilegradingsystem.viewsAdapter.teacher.classRecordAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.util.ArrayUtils;
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
import java.util.Arrays;

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
                    solveStudentTermGrade(studentGradeObjectModel,grade,studentClassObjectModel);
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
                    solveStudentTermGrade(studentGradeObjectModel,grade,studentClassObjectModel);
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
                                                System.out.println("exam %D exam"+i);
                                                studentGradeObjectModel.setExam(Double.parseDouble(i));
                                                solveStudentTermGrade(studentGradeObjectModel,grade,studentClassObjectModel);
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
                                                                System.out.println("%D project"+i);
                                                                studentGradeObjectModel.setProject(Double.parseDouble(i));
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
                                                                System.out.println("%D quiz longtest"+i);
                                                                studentGradeObjectModel.setQuizLongTest(Double.parseDouble(i));
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
                System.out.println("attendance: "+studentGradeObjectModel.getAttendance()* classRecordVersion.getAttendance()
                        +"Quiz Long Test: "+getRating((int) studentGradeObjectModel.getQuizLongTest()) * classRecordVersion.getQuizLongTest()+" "+
                        " Projects"+getRating((int)studentGradeObjectModel.getProject()) * classRecordVersion.getProjects()+" Exam: "+
                       getRating((int) studentGradeObjectModel.getExam())* classRecordVersion.getExam()+" Class Participation: "+
                        getRating((int)studentGradeObjectModel.getClassParticipation()) * classRecordVersion.getClassParticipation()+" Character: "+
                        studentGradeObjectModel.getCharacter() * classRecordVersion.getCharacter()
                );
                termGrade += studentGradeObjectModel.getAttendance()*classRecordVersion.getAttendance();
                termGrade += studentGradeObjectModel.getCharacter()*classRecordVersion.getCharacter();
                termGrade += getRating((int)studentGradeObjectModel.getClassParticipation()) * classRecordVersion.getClassParticipation();
                termGrade += getRating((int) studentGradeObjectModel.getExam())* classRecordVersion.getExam();
                termGrade += getRating((int)studentGradeObjectModel.getProject()) * classRecordVersion.getProjects();
                termGrade += getRating((int) studentGradeObjectModel.getQuizLongTest()) * classRecordVersion.getQuizLongTest();
                System.out.println("final Grade "+termGrade);
                grade.setText(getEquivalentGrade((int)Math.round(termGrade))+" (" +(termGrade)+")");
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
        double ptotal = 0;
        double sTotal = 0;
        double parNum = 0;

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

    double getEquivalentGrade(double finalGrade){
        Integer[] gradeScore = {95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 80, 79, 78, 77, 76, 75, 74, 73, 72, 71, 70, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55};
        double[] equivalentScore = {
                1.0,
                1.1,
                1.2,
                1.3,
                1.4,
                1.5,
                1.6,
                1.7,
                1.8,
                1.9,
                2.0,
                2.1,
                2.2,
                2.3,
                2.4,
                2.5,
                2.6,
                2.7,
                2.8,
                2.9,
                3.0,
                3.1,
                3.2,
                3.3,
                3.4,
                3.5,
                3.6,
                3.7,
                3.8,
                3.9,
                4.0,
                4.1,
                4.2,
                4.3,
                4.4,
                4.5,
                4.6,
                4.7,
                4.8,
                4.9,
                5.0};


        return equivalentScore[Arrays.asList(gradeScore).indexOf((int)finalGrade)];
    }

    int getRating(int score){
        Integer[] rawScore = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};
        int[] rating =  {65,
                65,
                65,
                65,
                65,
                66,
                66,
                66,
                66,
                66,
                67,
                67,
                67,
                67,
                67,
                68,
                68,
                68,
                68,
                68,
                69,
                69,
                69,
                69,
                69,
                70,
                70,
                70,
                70,
                70,
                70,
                71,
                71,
                71,
                71,
                71,
                71,
                72,
                72,
                72,
                72,
                72,
                72,
                73,
                73,
                73,
                73,
                73,
                73,
                74,
                75,
                75,
                76,
                76,
                77,
                77,
                78,
                78,
                79,
                79,
                80,
                80,
                81,
                81,
                82,
                82,
                83,
                83,
                84,
                84,
                85,
                85,
                85,
                86,
                86,
                86,
                87,
                87,
                87,
                88,
                88,
                88,
                89,
                89,
                89,
                90,
                90,
                90,
                91,
                91,
                91,
                92,
                92,
                92,
                93,
                93,
                93,
                94,
                94,
                94,
                95};

        return rating[Arrays.asList(rawScore).indexOf(score)];

    }
}


