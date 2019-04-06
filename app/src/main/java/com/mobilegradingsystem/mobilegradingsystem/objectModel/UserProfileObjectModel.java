package com.mobilegradingsystem.mobilegradingsystem.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class UserProfileObjectModel {
    private String userId;
    private String userImage = "https://firebasestorage.googleapis.com/v0/b/classrecordsystem-f6067.appspot.com/o/assets%2Fser1.png?alt=media&token=f6b84fc2-2ecd-4cef-981f-ac427f2aeeb8";
    private String userName;
    private String contactNumber;
    private String email;
    private String userType;
    private String userSchoolId;

    public UserProfileObjectModel(){

    }
    public UserProfileObjectModel(String userId,
                                  String userImage, String userName, String contactNumber, String email,String userType,String userSchoolId){
        this.userId=userId;
        this.userImage=userImage;
        this.userName= userName;
        this.contactNumber= contactNumber;
        this.email=email;
        this.userType = userType;
        this.userSchoolId = userSchoolId;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserSchoolId(String userSchoolId) {
        this.userSchoolId = userSchoolId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserSchoolId() {
        return userSchoolId;
    }
}
