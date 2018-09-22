package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class TeacherProfileProfileObjectModel {
    private String userId;
    private String teacherName;
    private String teacherId;
    private String accountStatus;

    public TeacherProfileProfileObjectModel(){

    }
    public TeacherProfileProfileObjectModel(
            String userId,
            String teacherId,
            String teacherName,
            String accountStatus
    ){
      this.userId = userId;
      this.teacherId = teacherId;
      this.teacherName = teacherName;
      this.accountStatus = accountStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
