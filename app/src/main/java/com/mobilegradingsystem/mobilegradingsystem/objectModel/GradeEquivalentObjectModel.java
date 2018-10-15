package com.mobilegradingsystem.mobilegradingsystem.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class GradeEquivalentObjectModel {
    private String classRecordCode;
    private ArrayList<String> equivalent;
    private ArrayList<String> rawScore;


    public GradeEquivalentObjectModel(){

    }
    public GradeEquivalentObjectModel(String classRecordCode,ArrayList<String> equivalent,ArrayList<String> rawScore
    ){
       this.classRecordCode = classRecordCode;
       this.equivalent = equivalent;
       this.rawScore =rawScore;
    }


    public ArrayList<String> getEquivalent() {
        return equivalent;
    }

    public ArrayList<String> getRawScore() {
        return rawScore;
    }

    public String getClassRecordCode() {
        return classRecordCode;
    }

}
