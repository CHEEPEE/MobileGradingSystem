package com.mobilegradingsystem.mobilegradingsystem.objectModel;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class YearLevelObjectModel {

    private String department;
    private String key;
    private String program;
    private String yearLevel;
    public YearLevelObjectModel(){

    }
    public YearLevelObjectModel(String key, String department,String program,String yearLevel
    ){
        this.department= department;
        this.key = key;
        this.program = program;
        this.yearLevel =yearLevel;

    }

    public String getKey() {
        return key;
    }

    public String getProgram() {
        return program;
    }

    public String getDepartment() {
        return department;
    }

    public String getYearLevel() {
        return yearLevel;
    }

}
