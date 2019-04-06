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
    private String accountStatus;
    private String yearLevelKey;
    private String email;
    private String sectionKey;
    private String phoneNumber;

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
            String accountStatus,
            String yearLevelKey,
            String sectionKey,
            String phoneNumber,
            String email
    ){
      this.userId = userId;
      this.email = email;
      this.departmentKey = departmentKey;
      this.programKey = programKey;
      this.fName = fName;
      this.mNme = mNme;
      this.lName = lName;
      this.studentId = studentId;
      this.accountStatus = accountStatus;
      this.yearLevelKey = yearLevelKey;
      this.sectionKey = sectionKey;
      this.phoneNumber = phoneNumber;

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setYearLevelKey(String yearLevelKey) {
        this.yearLevelKey = yearLevelKey;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getSectionKey() {
        return sectionKey;
    }

    public String getYearLevelKey() {
        return yearLevelKey;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setDepartmentKey(String departmentKey) {
        this.departmentKey = departmentKey;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setmNme(String mNme) {
        this.mNme = mNme;
    }

    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }
}
