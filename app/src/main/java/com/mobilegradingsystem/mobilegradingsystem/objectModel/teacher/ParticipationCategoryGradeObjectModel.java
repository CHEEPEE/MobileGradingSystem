package com.mobilegradingsystem.mobilegradingsystem.objectModel.teacher;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class ParticipationCategoryGradeObjectModel {
    private String key;
    private String classCode;
    private Integer maxScode;
    private @ServerTimestamp
    Date timeStamp;
    private String term;


    public ParticipationCategoryGradeObjectModel(){

    }
    public ParticipationCategoryGradeObjectModel(String key, String classCode,
                                                 Integer maxScode,String term
    ){
     this.key = key;
     this.classCode = classCode;
     this.maxScode = maxScode;
     this.term = term;
    }
    public void deleteParticipation(String category,String key){
        FirebaseFirestore.getInstance().collection(category).document(key).delete();
    }

    public String getTerm() {
        return term;
    }

    public String getKey() {
        return key;
    }

    public String getClassCode() {
        return classCode;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Integer getMaxScode() {
        return maxScode;
    }


}

