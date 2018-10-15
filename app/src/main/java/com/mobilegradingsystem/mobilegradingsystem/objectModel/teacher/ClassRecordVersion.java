package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ClassRecordVersion {
   private double attendance;
   private double character;
   private double classParticipation;
   private double exam;
   private double projects;
   private double quizLongTest;





    public ClassRecordVersion(){

    }
    public ClassRecordVersion(double attendance,
                              double character,
                              double classParticipation,
                              double exam,
                              double projects,
                              double quizLongTest

    ){
        this.attendance =attendance;
        this.character = character;
        this.classParticipation = classParticipation;
        this.exam = exam;
        this.projects =projects;
        this.quizLongTest  =quizLongTest;

    }

    public double getQuizLongTest() {
        return quizLongTest;
    }

    public double getExam() {
        return exam;
    }

    public double getClassParticipation() {
        return classParticipation;
    }

    public double getCharacter() {
        return character;
    }

    public double getProjects() {
        return projects;
    }

    public double getAttendance() {
        return attendance;
    }
}
