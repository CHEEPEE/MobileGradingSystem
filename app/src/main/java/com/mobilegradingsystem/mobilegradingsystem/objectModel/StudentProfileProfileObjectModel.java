package com.mobilegradingsystem.mobilegradingsystem.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class StudentProfileProfileObjectModel {
    private String userId;
    private String departmentKey;
    private String programKey;
    private String fName;
    private String mNme;
    private String lName;
    private String studentId;
    private String accoutStatus;
    private String yearLevelKey;
    private String sectionKey;

    public StudentProfileProfileObjectModel(){

    }
    public StudentProfileProfileObjectModel(
            String userId,
            String departmentKey,
            String programKey,
            String fName,
            String mNme,
            String lName,
            String studentId,
            String accoutStatus,
            String yearLevelKey,
            String sectionKey
    ){
      this.userId = userId;
      this.departmentKey = departmentKey;
      this.programKey = programKey;
      this.fName = fName;
      this.mNme = mNme;
      this.lName = lName;
      this.studentId = studentId;
      this.accoutStatus = accoutStatus;
      this.yearLevelKey = yearLevelKey;
      this.sectionKey = sectionKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getDepartmentKey() {
        return departmentKey;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getmNme() {
        return mNme;
    }

    public String getProgramKey() {
        return programKey;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getAccoutStatus() {
        return accoutStatus;
    }

    public String getSectionKey() {
        return sectionKey;
    }

    public String getYearLevelKey() {
        return yearLevelKey;
    }
}
