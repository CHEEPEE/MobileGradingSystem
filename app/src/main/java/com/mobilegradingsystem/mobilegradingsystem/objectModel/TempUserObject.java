package com.mobilegradingsystem.mobilegradingsystem.objectModel;

public class TempUserObject {
    private String email;
    private String password;
    private String userType;
    private String userSchoolId;
    private String fName;
    private String mName;
    private String lName;
    private String departmentKey;
    private String programKey;
    private String sectionKey;
    private String studentId;
    private String yearLevelKey;
    private String accountStatus;
    private String userName;
    private String classCode;
    private String phoneNumber;




    public TempUserObject(){

    }
    public TempUserObject(String email,String password,String userType,String userSchoolId){

    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserSchoolId() {
        return userSchoolId;
    }

    public String getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getYearLevelKey() {
        return yearLevelKey;
    }

    public String getSectionKey() {
        return sectionKey;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getlName() {
        return lName;
    }

    public String getfName() {
        return fName;
    }

    public String getDepartmentKey() {
        return departmentKey;
    }

    public String getmName() {
        return mName;
    }

    public String getProgramKey() {
        return programKey;
    }

    public String getUserName() {
        return userName;
    }


    //    setter start

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setDepartmentKey(String departmentKey) {
        this.departmentKey = departmentKey;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setProgramKey(String programKey) {
        this.programKey = programKey;
    }

    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    public void setUserSchoolId(String userSchoolId) {
        this.userSchoolId = userSchoolId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setYearLevelKey(String yearLevelKey) {
        this.yearLevelKey = yearLevelKey;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassCode() {
        return classCode;
    }
}
