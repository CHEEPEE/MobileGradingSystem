package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class StudentGradeObjectModel {
    private String studentId;
    private String studentUserId;
    private double character;
    private double attendance;
    private double classParticipation;
    private double project;
    private double quizLongTest;
    private double exam;
    private String classCode;
    private String gradingSystemKey;
    private String key;
    private String term;


    public StudentGradeObjectModel(){

    }
    public StudentGradeObjectModel(String studentId,
                                   double character,
                                   double attendance,
                                   double classParticipation,
                                   double project,
                                   double quizLongTest,
                                   double exam,
                                   String classCode,
                                   String gradingSystemKey,
                                   String key,
                                   String studentUserId,String term
    ){
      this.studentId = studentId;
      this.character = character;
      this.attendance = attendance;
      this.classParticipation = classParticipation;
      this.project = project;
      this.quizLongTest = quizLongTest;
      this.exam = exam;
      this.classCode = classCode;
      this.gradingSystemKey = gradingSystemKey;
      this.key = key;
      this.studentUserId = studentUserId;
      this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public String getKey() {
        return key;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getGradingSystemKey() {
        return gradingSystemKey;
    }

    public double getAttendance() {
        return attendance;
    }

    public double getCharacter() {
        return character;
    }

    public double getClassParticipation() {
        return classParticipation;
    }

    public double getExam() {
        return exam;
    }

    public double getProject() {
        return project;
    }

    public double getQuizLongTest() {
        return quizLongTest;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public void setCharacter(double character) {
        this.character = character;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public void setClassParticipation(double classParticipation) {
        this.classParticipation = classParticipation;
    }

    public void setExam(double exam) {
        this.exam = exam;
    }

    public void setGradingSystemKey(String gradingSystemKey) {
        this.gradingSystemKey = gradingSystemKey;
    }

    public void setProject(double project) {
        this.project = project;
    }

    public void setQuizLongTest(double quizLongTest) {
        this.quizLongTest = quizLongTest;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentUserId(String studentUserId) {
        this.studentUserId = studentUserId;
    }
}

