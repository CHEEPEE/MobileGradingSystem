package com.mobilegradingsystem.mobilegradingsystem.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class AnnouncementObjectModel {
    private String key;
    private String title;
    private String description;
    private String classCode;
    private @ServerTimestamp
    Date timeStamp;

    public AnnouncementObjectModel(){

    }
    public AnnouncementObjectModel(String key,String title,String description,String classCode
    ){
        this.key = key;
        this.title = title;
        this.description = description;
        this.classCode = classCode;
    }

    public String getDescription() {
        return description;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
