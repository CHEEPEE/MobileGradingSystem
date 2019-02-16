package com.mobilegradingsystem.mobilegradingsystem.objectModel.student;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ServerTimestamp;
import com.mobilegradingsystem.mobilegradingsystem.objectModel.StudentProfileProfileObjectModel;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentProfile;

import java.util.Date;

import javax.annotation.Nullable;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class StudentClassObjectModel {
   private String key;
   private String studentUserId;
   private String studentId;
   private String classCode;
   private String status;
   private String lName;
   private @ServerTimestamp
    Date timeStamp;
   private StudentProfileProfileObjectModel studentProfileProfileObjectModel;


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
