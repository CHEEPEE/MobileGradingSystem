package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class TeacherProfileProfileObjectModel {
    private String userId;
    private String teacherName;
    private String teacherId;

    public TeacherProfileProfileObjectModel(){

    }
    public TeacherProfileProfileObjectModel(
            String userId,
            String teacherId,
            String teacherName
    ){
      this.userId = userId;
      this.teacherId = teacherId;
      this.teacherName = teacherName;
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

}
