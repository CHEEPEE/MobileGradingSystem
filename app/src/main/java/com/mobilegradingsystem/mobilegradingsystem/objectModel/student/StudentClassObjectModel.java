package com.mobilegradingsystem.mobilegradingsystem.objectModel.student;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class StudentClassObjectModel {
   private String key;

   private String studentUserId;
   private String studentId;
   private String classCode;
   private String status;
   private @ServerTimestamp
    Date timeStamp;


    public StudentClassObjectModel(){

    }
    public StudentClassObjectModel(String key, String studentUserId, String classCode, String studentId,String status
    ){
       this.key = key;
       this.studentUserId = studentUserId;
       this.studentId = studentId;
       this.classCode = classCode;
       this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getKey() {
        return key;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getStatus() {
        return status;
    }
}
