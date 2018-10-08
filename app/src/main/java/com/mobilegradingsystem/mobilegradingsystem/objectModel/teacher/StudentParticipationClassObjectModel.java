package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class StudentParticipationClassObjectModel {
    private String key;
    private String studentId;
    private String studentUserId;
    private double value;
    private String classCode;
    private String partKey;

    public StudentParticipationClassObjectModel(){

    }
    public StudentParticipationClassObjectModel(String key, String studentId, String studentUserId, double value, String classCode,String partKey
    ){
       this.key = key;
       this.studentUserId = studentUserId;
       this.studentId = studentId;
       this.classCode = classCode;
       this.value = value;
       this.partKey = partKey;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getKey() {
        return key;
    }

    public double getValue() {
        return value;
    }

    public String getPartKey() {
        return partKey;
    }
}
