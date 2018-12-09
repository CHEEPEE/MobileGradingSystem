package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class TeacherClassObjectModel {
    private String userId;
    private String classKey;
    private String name;
    private String sched;
    private String description;
    private String classRecordVersionKey;
    private @ServerTimestamp
    Date timeStamp;
    private String semester;
    private String schoolYear;


    public TeacherClassObjectModel(){

    }
    public TeacherClassObjectModel(
           String classKey,
           String userId,
           String name,
           String sched,
           String description,
           String semester,
           String schoolYear


    ){
      this.userId = userId;
      this.classKey = classKey;
      this.name = name;
      this.sched = sched;
      this.description = description;
      this.classRecordVersionKey = "4TYJW5v4LBujNmMCNEPs";
      this.semester = semester;
      this.schoolYear = schoolYear;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getClassKey() {
        return classKey;
    }

    public String getDescription() {
        return description;
    }

    public String getSched() {
        return sched;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public String getSemester() {
        return semester;
    }

    public String getClassRecordVersionKey() {
        return classRecordVersionKey;
    }

}
