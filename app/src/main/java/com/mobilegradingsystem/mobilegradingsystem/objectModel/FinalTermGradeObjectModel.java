package com.mobilegradingsystem.mobilegradingsystem.objectModel;

import java.util.ArrayList;

/**
 * Created by Keji's Lab on 26/11/2017.
 */

public class FinalTermGradeObjectModel {
    private String usereId;
    private String classCode;
    private String term;
    private Double grade;


    public FinalTermGradeObjectModel(){

    }
    public FinalTermGradeObjectModel(String usereId, String classCode, String term, Double grade
    ){
       this.usereId = usereId;
       this.classCode = classCode;
       this.term =term;
       this.grade = grade;
    }

    public String getTerm() {
        return term;
    }

    public String getClassCode() {
        return classCode;
    }

    public Double getGrade() {
        return grade;
    }

    public String getUsereId() {
        return usereId;
    }
}
