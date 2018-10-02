package com.mobilegradingsystem.mobilegradingsystem.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class FeedBackAnnouncementObjectModel {
    private String key;
    private String feedBack;
    private String announceCode;
    private String classCode;
    private String userId;
    private @ServerTimestamp
    Date timeStamp;

    public FeedBackAnnouncementObjectModel(){

    }
    public FeedBackAnnouncementObjectModel(String key,
                                           String feedBack,
                                           String classCode,
                                           String userId,
                                           String announceCode
    ){
        this.key = key;
        this.feedBack = feedBack;
        this.classCode = classCode;
        this.userId = userId;
        this.announceCode = announceCode;
    }

    public String getUserId() {
        return userId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getKey() {
        return key;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public String getAnnounceCode() {
        return announceCode;
    }
}
