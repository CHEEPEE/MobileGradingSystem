package com.mobilegradingsystem.mobilegradingsystem.objectModel;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class UserProfileObjectModel {
    private String userId;
    private String userImage;
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
