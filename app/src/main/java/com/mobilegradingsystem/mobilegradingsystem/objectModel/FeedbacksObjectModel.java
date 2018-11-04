package com.mobilegradingsystem.mobilegradingsystem.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class FeedbacksObjectModel {
    private String key;
    private String feedback;
    private String studentUserId;
    private String classCode;
    private @ServerTimestamp
    Date timeStamp;

    public FeedbacksObjectModel(){

    }
    public FeedbacksObjectModel(String key,
                                String feedback,
                                String studentUserId,
                                String classCode
    ){
        this.key = key;
        this.feedback = feedback;
        this.studentUserId = studentUserId;
        this.classCode = classCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getKey() {
        return key;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getStudentUserId() {
        return studentUserId;
    }

    public String getFeedback() {
        return feedback;
    }


}
